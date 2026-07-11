package com.grupo10.bff_vitacare.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.grupo10.bff_vitacare.service.AuthContextService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifica que, sin configurar {@code cors.allowed-origins} (valor por
 * defecto), ningún origen web queda autorizado — a diferencia del
 * comportamiento anterior de {@code allowedOriginPatterns("*")}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigCorsDefaultTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthContextService authContextService;

    @Test
    void corsPreflightHasNoAllowOriginHeaderWhenNoOriginIsConfigured() throws Exception {
        mockMvc.perform(options("/api/me")
                        .header(HttpHeaders.ORIGIN, "http://sitio-no-autorizado.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name()))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
