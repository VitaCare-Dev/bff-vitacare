package com.grupo10.bff_vitacare.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.grupo10.bff_vitacare.client.ChatbotServiceClient;
import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.ChatMessageRequestDto;
import com.grupo10.bff_vitacare.dto.ChatMessageResponseDto;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
import com.grupo10.bff_vitacare.dto.MedicalThresholdDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import com.grupo10.bff_vitacare.service.PatientContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Orquesta el envío de mensajes al chatbot IA para el usuario autenticado.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final PatientContextService patientContextService;
    private final PatientServiceClient patientServiceClient;
    private final ChatbotServiceClient chatbotServiceClient;

    /**
     * @param patientContextService servicio que resuelve el paciente a partir del token
     * @param patientServiceClient  cliente hacia {@code patient-service}, para el contexto médico
     * @param chatbotServiceClient  cliente hacia {@code chatbot-service}
     */
    public ChatController(PatientContextService patientContextService, PatientServiceClient patientServiceClient,
                           ChatbotServiceClient chatbotServiceClient) {
        this.patientContextService = patientContextService;
        this.patientServiceClient = patientServiceClient;
        this.chatbotServiceClient = chatbotServiceClient;
    }

    /**
     * {@code POST /api/chat}: envía un mensaje del usuario autenticado al chatbot IA,
     * incluyendo el contexto médico del paciente (enfermedades/umbrales) para
     * que la IA responda de forma más pertinente a su condición.
     *
     * @param jwt     ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param request mensaje a enviar
     * @return 200 con la respuesta generada por el chatbot
     */
    @PostMapping
    public ResponseEntity<ChatMessageResponseDto> sendMessage(@AuthenticationPrincipal Jwt jwt,
                                                               @RequestBody ChatMessageRequestDto request) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        String contextoPaciente = buildContextoPaciente(patient.getIdPaciente());
        String respuesta = chatbotServiceClient.sendMessage(patient.getIdUsuario(), request.getMensaje(), contextoPaciente);

        ChatMessageResponseDto response = new ChatMessageResponseDto();
        response.setRespuesta(respuesta);
        return ResponseEntity.ok(response);
    }

    /**
     * Arma una descripción en texto plano de las enfermedades crónicas y los
     * umbrales médicos del paciente, para que {@code chatbot-service} la
     * incluya en el prompt de sistema sin tener que conocer el modelo de
     * datos de {@code patient-service}.
     *
     * @param idPaciente identificador del paciente
     * @return la descripción del contexto, o {@code null} si no tiene enfermedades registradas
     */
    private String buildContextoPaciente(Long idPaciente) {
        List<DiseaseDto> enfermedades = patientServiceClient.getPatientDiseases(idPaciente);
        if (enfermedades.isEmpty()) {
            return null;
        }

        StringBuilder contexto = new StringBuilder("El paciente tiene las siguientes enfermedades crónicas registradas: ");
        contexto.append(enfermedades.stream().map(DiseaseDto::getNombreEnfermedad).collect(Collectors.joining(", ")));
        contexto.append(".");

        try {
            MedicalThresholdDto umbral = patientServiceClient.getThresholds(idPaciente);
            contexto.append(" Sus umbrales médicos personalizados son: glucosa entre ")
                    .append(umbral.getGlucosaMin()).append(" y ").append(umbral.getGlucosaMax())
                    .append(" mg/dL, presión arterial hasta ").append(umbral.getSistolicaMax())
                    .append("/").append(umbral.getDiastolicaMax())
                    .append(" mmHg, temperatura hasta ").append(umbral.getTemperaturaMax()).append(" °C.");
        } catch (UpstreamErrorException e) {
            // Sin umbrales calculados todavía (ej. ninguna de sus enfermedades
            // tiene una regla médica asociada): el contexto de enfermedades
            // solo sigue siendo útil sin los umbrales.
        }

        return contexto.toString();
    }

}
