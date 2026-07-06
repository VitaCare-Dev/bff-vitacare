package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.AiAlertServiceClient;
import com.grupo10.bff_vitacare.dto.AlertaDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.RecomendacionDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class AiAlertOrchestrationServiceTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private AiAlertServiceClient aiAlertServiceClient;

    @InjectMocks
    private AiAlertOrchestrationService aiAlertOrchestrationService;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        PatientDto patient = new PatientDto();
        patient.setIdPaciente(1L);
        lenient().when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
    }

    private AlertaDto alerta(Long id) {
        AlertaDto alerta = new AlertaDto();
        alerta.setIdAlertaIa(id);
        return alerta;
    }

    private RecomendacionDto recomendacion(Long id) {
        RecomendacionDto recomendacion = new RecomendacionDto();
        recomendacion.setIdRecomendacion(id);
        return recomendacion;
    }

    @Test
    void getAlertasDelegatesToTheClient() {
        when(aiAlertServiceClient.listAlertas(1L)).thenReturn(List.of(alerta(1L)));

        assertThat(aiAlertOrchestrationService.getAlertas(jwt)).hasSize(1);
    }

    @Test
    void getAlertasNoLeidasDelegatesToTheClient() {
        when(aiAlertServiceClient.listAlertasNoLeidas(1L)).thenReturn(List.of(alerta(1L)));

        assertThat(aiAlertOrchestrationService.getAlertasNoLeidas(jwt)).hasSize(1);
    }

    @Test
    void marcarAlertaLeidaSucceedsWhenOwnedByCurrentPatient() {
        when(aiAlertServiceClient.listAlertas(1L)).thenReturn(List.of(alerta(5L)));

        aiAlertOrchestrationService.marcarAlertaLeida(jwt, 5L);

        verify(aiAlertServiceClient).marcarAlertaLeida(5L);
    }

    @Test
    void marcarAlertaLeidaThrowsWhenNotOwnedByCurrentPatient() {
        when(aiAlertServiceClient.listAlertas(1L)).thenReturn(List.of(alerta(99L)));

        assertThatThrownBy(() -> aiAlertOrchestrationService.marcarAlertaLeida(jwt, 5L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void marcarTodasAlertasLeidasDelegatesToTheClient() {
        aiAlertOrchestrationService.marcarTodasAlertasLeidas(jwt);

        verify(aiAlertServiceClient).marcarTodasAlertasLeidas(1L);
    }

    @Test
    void getRecomendacionesDelegatesToTheClient() {
        when(aiAlertServiceClient.listRecomendaciones(1L)).thenReturn(List.of(recomendacion(1L)));

        assertThat(aiAlertOrchestrationService.getRecomendaciones(jwt)).hasSize(1);
    }

    @Test
    void getRecomendacionesNoLeidasDelegatesToTheClient() {
        when(aiAlertServiceClient.listRecomendacionesNoLeidas(1L)).thenReturn(List.of(recomendacion(1L)));

        assertThat(aiAlertOrchestrationService.getRecomendacionesNoLeidas(jwt)).hasSize(1);
    }

    @Test
    void marcarRecomendacionLeidaSucceedsWhenOwnedByCurrentPatient() {
        when(aiAlertServiceClient.listRecomendaciones(1L)).thenReturn(List.of(recomendacion(5L)));

        aiAlertOrchestrationService.marcarRecomendacionLeida(jwt, 5L);

        verify(aiAlertServiceClient).marcarRecomendacionLeida(5L);
    }

    @Test
    void marcarRecomendacionLeidaThrowsWhenNotOwnedByCurrentPatient() {
        when(aiAlertServiceClient.listRecomendaciones(1L)).thenReturn(List.of(recomendacion(99L)));

        assertThatThrownBy(() -> aiAlertOrchestrationService.marcarRecomendacionLeida(jwt, 5L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void marcarTodasRecomendacionesLeidasDelegatesToTheClient() {
        aiAlertOrchestrationService.marcarTodasRecomendacionesLeidas(jwt);

        verify(aiAlertServiceClient).marcarTodasRecomendacionesLeidas(1L);
    }
}
