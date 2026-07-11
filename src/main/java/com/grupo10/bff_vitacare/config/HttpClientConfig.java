package com.grupo10.bff_vitacare.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

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
     */
    @Bean
    public RestClientCustomizer restClientTimeoutCustomizer() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return builder -> builder.requestFactory(factory);
    }
}
