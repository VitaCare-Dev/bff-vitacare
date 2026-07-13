package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import com.grupo10.bff_vitacare.client.ChatbotServiceClient;
import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.ChatMessageRequestDto;
import com.grupo10.bff_vitacare.dto.ChatMessageResponseDto;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
import com.grupo10.bff_vitacare.dto.MedicalThresholdDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import com.grupo10.bff_vitacare.service.PatientContextService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private PatientServiceClient patientServiceClient;

    @Mock
    private ChatbotServiceClient chatbotServiceClient;

    @InjectMocks
    private ChatController chatController;

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();

    private PatientDto buildPatient() {
        PatientDto patient = new PatientDto();
        patient.setIdPaciente(10L);
        patient.setIdUsuario(1L);
        return patient;
    }

    @Test
    void sendMessageResolvesThePatientAndDelegatesToTheChatbot() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(buildPatient());
        when(patientServiceClient.getPatientDiseases(10L)).thenReturn(List.of());

        ChatMessageRequestDto request = new ChatMessageRequestDto();
        request.setMensaje("hola");
        when(chatbotServiceClient.sendMessage(1L, "hola", null)).thenReturn("hola, ¿en qué puedo ayudarte?");

        ResponseEntity<ChatMessageResponseDto> response = chatController.sendMessage(jwt, request);

        assertThat(response.getBody().getRespuesta()).isEqualTo("hola, ¿en qué puedo ayudarte?");
    }

    @Test
    void sendMessageWithDiseasesAndThresholdsBuildsFullContext() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(buildPatient());

        DiseaseDto diabetes = new DiseaseDto();
        diabetes.setNombreEnfermedad("Diabetes");
        DiseaseDto hipertension = new DiseaseDto();
        hipertension.setNombreEnfermedad("Hipertensión");
        when(patientServiceClient.getPatientDiseases(10L)).thenReturn(List.of(diabetes, hipertension));

        MedicalThresholdDto umbral = new MedicalThresholdDto();
        umbral.setGlucosaMin(70);
        umbral.setGlucosaMax(180);
        umbral.setSistolicaMax(140);
        umbral.setDiastolicaMax(90);
        umbral.setTemperaturaMax(37.5);
        when(patientServiceClient.getThresholds(10L)).thenReturn(umbral);

        ChatMessageRequestDto request = new ChatMessageRequestDto();
        request.setMensaje("¿qué debo comer?");
        when(chatbotServiceClient.sendMessage(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.eq("¿qué debo comer?"),
                org.mockito.ArgumentMatchers.contains("Diabetes, Hipertensión")))
                .thenReturn("respuesta personalizada");

        ResponseEntity<ChatMessageResponseDto> response = chatController.sendMessage(jwt, request);

        assertThat(response.getBody().getRespuesta()).isEqualTo("respuesta personalizada");
    }

    @Test
    void sendMessageWithDiseasesButNoThresholdsOmitsThresholdText() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(buildPatient());

        DiseaseDto disease = new DiseaseDto();
        disease.setNombreEnfermedad("Dislipidemia");
        when(patientServiceClient.getPatientDiseases(10L)).thenReturn(List.of(disease));
        when(patientServiceClient.getThresholds(10L))
                .thenThrow(new UpstreamErrorException(HttpStatus.NOT_FOUND, "sin umbrales"));

        ChatMessageRequestDto request = new ChatMessageRequestDto();
        request.setMensaje("hola");
        when(chatbotServiceClient.sendMessage(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.eq("hola"),
                org.mockito.ArgumentMatchers.argThat(contexto -> contexto != null
                        && contexto.contains("Dislipidemia") && !contexto.contains("umbrales médicos"))))
                .thenReturn("respuesta");

        ResponseEntity<ChatMessageResponseDto> response = chatController.sendMessage(jwt, request);

        assertThat(response.getBody().getRespuesta()).isEqualTo("respuesta");
    }
}
