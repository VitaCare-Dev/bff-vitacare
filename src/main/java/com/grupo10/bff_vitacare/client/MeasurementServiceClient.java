package com.grupo10.bff_vitacare.client;

import java.time.LocalDate;
import java.util.List;

import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.PageResponseDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

/**
 * Cliente HTTP hacia {@code measurement-service}, usado para registrar y
 * consultar mediciones de salud del paciente autenticado.
 */
@Component
public class MeasurementServiceClient {

    private final RestClient restClient;

    /**
     * @param restClientBuilder          builder de {@link RestClient} inyectado por Spring
     * @param measurementServiceBaseUrl  URL base de {@code measurement-service}
     */
    public MeasurementServiceClient(RestClient.Builder restClientBuilder,
                                     @Value("${measurement-service.base-url}") String measurementServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(measurementServiceBaseUrl).build();
    }

    /**
     * Registra una medición de glucosa para el paciente indicado.
     *
     * @param idPaciente identificador del paciente
     * @param datos      datos de la medición
     * @return la medición creada
     * @throws UpstreamErrorException si {@code measurement-service} rechaza el registro
     */
    public GlucoseDto createGlucose(Long idPaciente, GlucoseRequestDto datos) {
        return restClient.post()
                .uri("/api/glucose")
                .body(new GlucoseCreateRequest(idPaciente, datos))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible registrar la glucosa");
                })
                .body(GlucoseDto.class);
    }

    /**
     * Lista el historial paginado de mediciones de glucosa de un paciente,
     * opcionalmente acotado a un rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param page       número de página solicitado (base 0)
     * @param size       tamaño de página solicitado
     * @param desde      fecha inicial (inclusive) del rango a consultar, o {@code null} para no acotar
     * @param hasta      fecha final (inclusive) del rango a consultar, o {@code null} para no acotar
     * @return la página de mediciones de glucosa del paciente
     */
    public PageResponseDto<GlucoseDto> listGlucoseByPatient(
            Long idPaciente, int page, int size, LocalDate desde, LocalDate hasta) {
        return restClient.get()
                .uri(uriBuilder -> pagedHistoryUri(
                        uriBuilder, "/api/glucose/patient/{idPaciente}", page, size, desde, hasta, idPaciente))
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponseDto<GlucoseDto>>() {
                });
    }

    /**
     * Obtiene la medición de glucosa más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la última medición de glucosa
     * @throws UpstreamErrorException si el paciente no tiene mediciones registradas
     */
    public GlucoseDto getLatestGlucose(Long idPaciente) {
        return restClient.get()
                .uri("/api/glucose/patient/{idPaciente}/latest", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "El paciente no tiene mediciones de glucosa");
                })
                .body(GlucoseDto.class);
    }

    /**
     * Registra un perfil lipídico para el paciente indicado.
     *
     * @param idPaciente identificador del paciente
     * @param datos      datos del perfil lipídico
     * @return el perfil lipídico creado
     * @throws UpstreamErrorException si {@code measurement-service} rechaza el registro
     */
    public LipidsDto createLipids(Long idPaciente, LipidsRequestDto datos) {
        return restClient.post()
                .uri("/api/lipids")
                .body(new LipidsCreateRequest(idPaciente, datos))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible registrar el perfil lipídico");
                })
                .body(LipidsDto.class);
    }

    /**
     * Lista el historial paginado de perfiles lipídicos de un paciente,
     * opcionalmente acotado a un rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param page       número de página solicitado (base 0)
     * @param size       tamaño de página solicitado
     * @param desde      fecha inicial (inclusive) del rango a consultar, o {@code null} para no acotar
     * @param hasta      fecha final (inclusive) del rango a consultar, o {@code null} para no acotar
     * @return la página de perfiles lipídicos del paciente
     */
    public PageResponseDto<LipidsDto> listLipidsByPatient(
            Long idPaciente, int page, int size, LocalDate desde, LocalDate hasta) {
        return restClient.get()
                .uri(uriBuilder -> pagedHistoryUri(
                        uriBuilder, "/api/lipids/patient/{idPaciente}", page, size, desde, hasta, idPaciente))
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponseDto<LipidsDto>>() {
                });
    }

    /**
     * Obtiene el perfil lipídico más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return el último perfil lipídico
     * @throws UpstreamErrorException si el paciente no tiene mediciones registradas
     */
    public LipidsDto getLatestLipids(Long idPaciente) {
        return restClient.get()
                .uri("/api/lipids/patient/{idPaciente}/latest", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "El paciente no tiene mediciones lipídicas");
                })
                .body(LipidsDto.class);
    }

    /**
     * Registra signos vitales para el paciente indicado.
     *
     * @param idPaciente identificador del paciente
     * @param datos      datos de los signos vitales
     * @return la medición creada
     * @throws UpstreamErrorException si {@code measurement-service} rechaza el registro
     */
    public VitalsDto createVitals(Long idPaciente, VitalsRequestDto datos) {
        return restClient.post()
                .uri("/api/vitals")
                .body(new VitalsCreateRequest(idPaciente, datos))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible registrar los signos vitales");
                })
                .body(VitalsDto.class);
    }

    /**
     * Lista el historial paginado de signos vitales de un paciente,
     * opcionalmente acotado a un rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param page       número de página solicitado (base 0)
     * @param size       tamaño de página solicitado
     * @param desde      fecha inicial (inclusive) del rango a consultar, o {@code null} para no acotar
     * @param hasta      fecha final (inclusive) del rango a consultar, o {@code null} para no acotar
     * @return la página de mediciones de signos vitales del paciente
     */
    public PageResponseDto<VitalsDto> listVitalsByPatient(
            Long idPaciente, int page, int size, LocalDate desde, LocalDate hasta) {
        return restClient.get()
                .uri(uriBuilder -> pagedHistoryUri(
                        uriBuilder, "/api/vitals/patient/{idPaciente}", page, size, desde, hasta, idPaciente))
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponseDto<VitalsDto>>() {
                });
    }

    /**
     * Obtiene la medición de signos vitales más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la última medición de signos vitales
     * @throws UpstreamErrorException si el paciente no tiene mediciones registradas
     */
    public VitalsDto getLatestVitals(Long idPaciente) {
        return restClient.get()
                .uri("/api/vitals/patient/{idPaciente}/latest", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "El paciente no tiene signos vitales registrados");
                })
                .body(VitalsDto.class);
    }

    /**
     * Lista el historial combinado de controles de salud (glucosa, lípidos y
     * signos vitales agrupados por control) de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return el historial de controles del paciente
     */
    public List<HealthControlDto> getHealthHistory(Long idPaciente) {
        return restClient.get()
                .uri("/api/controls/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<HealthControlDto>>() {
                });
    }

    /**
     * Busca una medición de glucosa por su identificador de control.
     *
     * @param idControl identificador del control
     * @return la medición de glucosa encontrada
     * @throws UpstreamErrorException si no existe
     */
    public GlucoseDto getGlucoseById(Long idControl) {
        return restClient.get()
                .uri("/api/glucose/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Medición de glucosa no encontrada");
                })
                .body(GlucoseDto.class);
    }

    /**
     * Elimina una medición de glucosa.
     *
     * @param idControl identificador del control a eliminar
     * @throws UpstreamErrorException si {@code measurement-service} rechaza la eliminación
     */
    public void deleteGlucose(Long idControl) {
        restClient.delete()
                .uri("/api/glucose/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar la medición de glucosa");
                })
                .toBodilessEntity();
    }

    /**
     * Busca un perfil lipídico por su identificador de control.
     *
     * @param idControl identificador del control
     * @return el perfil lipídico encontrado
     * @throws UpstreamErrorException si no existe
     */
    public LipidsDto getLipidsById(Long idControl) {
        return restClient.get()
                .uri("/api/lipids/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Medición lipídica no encontrada");
                })
                .body(LipidsDto.class);
    }

    /**
     * Elimina un perfil lipídico.
     *
     * @param idControl identificador del control a eliminar
     * @throws UpstreamErrorException si {@code measurement-service} rechaza la eliminación
     */
    public void deleteLipids(Long idControl) {
        restClient.delete()
                .uri("/api/lipids/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar la medición lipídica");
                })
                .toBodilessEntity();
    }

    /**
     * Busca una medición de signos vitales por su identificador de control.
     *
     * @param idControl identificador del control
     * @return la medición de signos vitales encontrada
     * @throws UpstreamErrorException si no existe
     */
    public VitalsDto getVitalsById(Long idControl) {
        return restClient.get()
                .uri("/api/vitals/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Medición de signos vitales no encontrada");
                })
                .body(VitalsDto.class);
    }

    /**
     * Elimina una medición de signos vitales.
     *
     * @param idControl identificador del control a eliminar
     * @throws UpstreamErrorException si {@code measurement-service} rechaza la eliminación
     */
    public void deleteVitals(Long idControl) {
        restClient.delete()
                .uri("/api/vitals/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar los signos vitales");
                })
                .toBodilessEntity();
    }

    /**
     * Construye la URI de un endpoint de historial paginado, agregando los
     * parámetros de página/tamaño y, si vienen presentes, el rango de fechas.
     *
     * @param uriBuilder builder de URI provisto por {@link RestClient}
     * @param path       ruta del endpoint, con el placeholder {@code {idPaciente}}
     * @param page       número de página solicitado (base 0)
     * @param size       tamaño de página solicitado
     * @param desde      fecha inicial (inclusive) del rango a consultar, o {@code null} para no acotar
     * @param hasta      fecha final (inclusive) del rango a consultar, o {@code null} para no acotar
     * @param idPaciente identificador del paciente, usado para completar el placeholder de la ruta
     * @return la URI final ya construida
     */
    private java.net.URI pagedHistoryUri(UriBuilder uriBuilder, String path, int page, int size,
            LocalDate desde, LocalDate hasta, Long idPaciente) {
        uriBuilder.path(path);
        uriBuilder.queryParam("page", page);
        uriBuilder.queryParam("size", size);
        if (desde != null) {
            uriBuilder.queryParam("desde", desde);
        }
        if (hasta != null) {
            uriBuilder.queryParam("hasta", hasta);
        }
        return uriBuilder.build(idPaciente);
    }

    /** Espejo de {@code GlucosaRequestDto} de {@code measurement-service}. */
    private static final class GlucoseCreateRequest {
        private final Long idPaciente;
        private final String notas;
        private final int glucosa;
        private final String periodo;

        GlucoseCreateRequest(Long idPaciente, GlucoseRequestDto datos) {
            this.idPaciente = idPaciente;
            this.notas = datos.getNotas();
            this.glucosa = datos.getGlucosa();
            this.periodo = datos.getPeriodo();
        }

        public Long getIdPaciente() {
            return idPaciente;
        }

        public String getNotas() {
            return notas;
        }

        public int getGlucosa() {
            return glucosa;
        }

        public String getPeriodo() {
            return periodo;
        }
    }

    /** Espejo de {@code LipidosRequestDto} de {@code measurement-service}. */
    private static final class LipidsCreateRequest {
        private final Long idPaciente;
        private final String notas;
        private final int colesterolTotal;
        private final int colesterolLDL;
        private final int colesterolHDL;
        private final int trigliceridos;

        LipidsCreateRequest(Long idPaciente, LipidsRequestDto datos) {
            this.idPaciente = idPaciente;
            this.notas = datos.getNotas();
            this.colesterolTotal = datos.getColesterolTotal();
            this.colesterolLDL = datos.getColesterolLDL();
            this.colesterolHDL = datos.getColesterolHDL();
            this.trigliceridos = datos.getTrigliceridos();
        }

        public Long getIdPaciente() {
            return idPaciente;
        }

        public String getNotas() {
            return notas;
        }

        public int getColesterolTotal() {
            return colesterolTotal;
        }

        public int getColesterolLDL() {
            return colesterolLDL;
        }

        public int getColesterolHDL() {
            return colesterolHDL;
        }

        public int getTrigliceridos() {
            return trigliceridos;
        }
    }

    /** Espejo de {@code MedicionVitalRequestDto} de {@code measurement-service}. */
    private static final class VitalsCreateRequest {
        private final Long idPaciente;
        private final String notas;
        private final Integer presionSistolica;
        private final Integer presionDiastolica;
        private final double temperatura;
        private final double peso;

        VitalsCreateRequest(Long idPaciente, VitalsRequestDto datos) {
            this.idPaciente = idPaciente;
            this.notas = datos.getNotas();
            this.presionSistolica = datos.getPresionSistolica();
            this.presionDiastolica = datos.getPresionDiastolica();
            this.temperatura = datos.getTemperatura();
            this.peso = datos.getPeso();
        }

        public Long getIdPaciente() {
            return idPaciente;
        }

        public String getNotas() {
            return notas;
        }

        public Integer getPresionSistolica() {
            return presionSistolica;
        }

        public Integer getPresionDiastolica() {
            return presionDiastolica;
        }

        public double getTemperatura() {
            return temperatura;
        }

        public double getPeso() {
            return peso;
        }
    }

}
