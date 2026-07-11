package com.grupo10.bff_vitacare.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Genera URLs firmadas (SAS de servicio) hacia Azure Blob Storage para que la
 * app suba y lea directamente la foto de perfil del paciente, sin que el
 * archivo pase por el BFF. El contenedor es privado: toda lectura/escritura
 * requiere una de estas URLs de corta duración.
 *
 * <p>El SAS se firma "a mano" (HMAC-SHA256 sobre la cadena canónica que
 * documenta Microsoft) en vez de usar el SDK {@code azure-storage-blob}:
 * ese SDK arrastra Netty/Reactor y una versión propia de Jackson que
 * chocaban con la serialización JSON del resto del BFF (rompía la carga del
 * contexto de Spring y la serialización de los demás clientes REST). Firmar
 * un SAS es solo una operación criptográfica local, no requiere el SDK.
 */
@Service
public class ProfilePhotoService {

    private static final int SAS_EXPIRY_MINUTES = 15;
    private static final String SAS_VERSION = "2020-12-06";
    private static final DateTimeFormatter EXPIRY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final String connectionString;
    private final String containerName;
    private final HttpClient httpClient;

    // Se resuelven perezosamente (recién al primer uso real), no en el
    // constructor: así el BFF arranca igual aunque la variable de entorno
    // AZURE_STORAGE_CONNECTION_STRING todavía no esté configurada (por
    // ejemplo en desarrollo local antes de crear el secreto, o en tests que
    // no ejercitan la foto de perfil).
    private volatile String accountName;
    private volatile byte[] accountKey;

    /**
     * @param connectionString cadena de conexión de la cuenta de Storage (secreto, nunca se loguea)
     * @param containerName    nombre del contenedor Blob donde se guardan las fotos de perfil
     * @param httpClient       cliente HTTP usado para verificar la existencia de un blob antes de confirmarlo
     */
    public ProfilePhotoService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.profile-photos-container:profile-photos}") String containerName,
            HttpClient httpClient) {
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.httpClient = httpClient;
    }

    private synchronized void ensureCredentialsResolved() {
        if (accountName == null) {
            accountName = extractValue(connectionString, "AccountName");
            accountKey = Base64.getDecoder().decode(extractValue(connectionString, "AccountKey"));
        }
    }

    /**
     * Genera una URL con SAS de escritura para que el paciente suba su foto
     * de perfil directamente a Azure. Siempre apunta al mismo blob del
     * paciente (la subida siguiente reemplaza a la anterior).
     *
     * @param idPaciente identificador del paciente
     * @return la URL firmada, válida por {@value #SAS_EXPIRY_MINUTES} minutos
     */
    public String generateUploadUrl(Long idPaciente) {
        return signedUrl(blobName(idPaciente), "cw");
    }

    /**
     * Genera una URL con SAS de lectura de corta duración para mostrar una
     * foto de perfil ya subida.
     *
     * @param baseBlobUrl URL base del blob (sin SAS), tal como se guarda en {@code patient-service}
     * @return la URL firmada, válida por {@value #SAS_EXPIRY_MINUTES} minutos
     */
    public String generateReadUrl(String baseBlobUrl) {
        return signedUrl(blobNameFromUrl(baseBlobUrl), "r");
    }

    /**
     * URL base (sin SAS) del blob de foto de perfil de un paciente, para
     * persistir en {@code patient-service} tras confirmar una subida.
     *
     * @param idPaciente identificador del paciente
     * @return la URL base del blob
     */
    public String getBaseBlobUrl(Long idPaciente) {
        ensureCredentialsResolved();
        return blobUrl(blobName(idPaciente));
    }

    /**
     * Verifica contra Azure Blob Storage (vía HEAD) que la foto de perfil de
     * un paciente ya fue subida, antes de confirmar/persistir su URL en
     * {@code patient-service}. Sin esta comprobación, una subida interrumpida
     * o fallida dejaría al paciente con una URL que apunta a un blob
     * inexistente.
     *
     * @param idPaciente identificador del paciente
     * @return {@code true} si el blob existe y es accesible; {@code false} en caso contrario
     */
    public boolean blobExists(Long idPaciente) {
        ensureCredentialsResolved();
        String url = signedUrl(blobName(idPaciente), "r");

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }

    private String signedUrl(String blobName, String permissions) {
        ensureCredentialsResolved();
        String expiry = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(SAS_EXPIRY_MINUTES).format(EXPIRY_FORMAT);
        String canonicalizedResource = "/blob/" + accountName + "/" + containerName + "/" + blobName;

        // Formato oficial del "string-to-sign" de un SAS de servicio para
        // Blob Storage (versión 2020-12-06): cada línea es un campo, en este
        // orden fijo; los campos que no aplican van vacíos.
        String stringToSign = String.join(
                "\n",
                permissions,
                "", // signedStart: sin hora de inicio, válido desde ya
                expiry,
                canonicalizedResource,
                "", // signedIdentifier: sin política de acceso guardada
                "", // signedIP
                "https", // signedProtocol
                SAS_VERSION,
                "b", // signedResource: blob individual
                "", // signedSnapshotTime
                "", // signedEncryptionScope
                "", // rscc (Cache-Control)
                "", // rscd (Content-Disposition)
                "", // rsce (Content-Encoding)
                "", // rscl (Content-Language)
                "" // rsct (Content-Type)
                );

        String signature = sign(stringToSign);

        String query = "sv=" + SAS_VERSION
                + "&sr=b"
                + "&sp=" + permissions
                + "&se=" + urlEncode(expiry)
                + "&spr=https"
                + "&sig=" + urlEncode(signature);

        return blobUrl(blobName) + "?" + query;
    }

    private String sign(String stringToSign) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(accountKey, "HmacSHA256"));
            byte[] hash = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible firmar la URL de la foto de perfil", e);
        }
    }

    private String blobUrl(String blobName) {
        return "https://" + accountName + ".blob.core.windows.net/" + containerName + "/" + blobName;
    }

    private String blobName(Long idPaciente) {
        return "paciente-" + idPaciente + ".jpg";
    }

    private String blobNameFromUrl(String baseBlobUrl) {
        return baseBlobUrl.substring(baseBlobUrl.lastIndexOf('/') + 1);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String extractValue(String connectionString, String key) {
        String prefix = key + "=";
        for (String part : connectionString.split(";")) {
            if (part.startsWith(prefix)) {
                return part.substring(prefix.length());
            }
        }
        throw new IllegalArgumentException("La cadena de conexión no incluye " + key);
    }
}
