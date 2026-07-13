package com.grupo10.bff_vitacare.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;

/**
 * {@link HttpClient} compartido para llamadas HTTP salientes que no pasan por
 * {@code RestClient} (ej. verificar contra Azure Blob Storage si un blob
 * existe). Se expone como bean para poder inyectar un cliente mockeado en
 * tests, en vez de que cada servicio construya el suyo.
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Aplica timeouts de conexión y lectura a todos los {@code RestClient}
     * autoconfigurados que los seis clientes hacia los microservicios de
     * dominio construyen a partir de {@code RestClient.Builder} inyectado.
     *
     * <p>Sin esto, si un microservicio downstream queda colgado (cold start,
     * deadlock, red lenta), el hilo del BFF que atiende esa solicitud queda
     * bloqueado indefinidamente esperando una respuesta que nunca llega,
     * pudiendo agotar el pool de hilos del servidor con pocas solicitudes
     * concurrentes en ese estado.
     *
     * <p>Se usa {@link JdkClientHttpRequestFactory} (respaldado por
     * {@link HttpClient}, que soporta PATCH nativamente) en vez de
     * {@code SimpleClientHttpRequestFactory}: este último está respaldado por
     * {@code HttpURLConnection}, que en el JDK no soporta el método PATCH
     * ({@code ProtocolException: Invalid HTTP method: PATCH}) — rompía
     * silenciosamente {@code PatientServiceClient.updatePhotoUrl}, el único
     * cliente que usa {@code .patch()}, mientras el resto de los endpoints
     * (GET/POST/PUT/DELETE) seguían funcionando con normalidad.
     */
    @Bean
    public RestClientCustomizer restClientTimeoutCustomizer() {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(jdkHttpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));
        return builder -> builder.requestFactory(factory);
    }
}
