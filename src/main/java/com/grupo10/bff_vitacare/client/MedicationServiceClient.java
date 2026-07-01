package com.grupo10.bff_vitacare.client;

import java.time.LocalDate;
import java.util.List;

import com.grupo10.bff_vitacare.dto.MedicationDto;
import com.grupo10.bff_vitacare.dto.MedicationRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia {@code medication-service}, usado para gestionar el
 * tratamiento de medicamentos del paciente autenticado.
 */
@Component
public class MedicationServiceClient {

    private final RestClient restClient;

    public MedicationServiceClient(RestClient.Builder restClientBuilder,
                                    @Value("${medication-service.base-url}") String medicationServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(medicationServiceBaseUrl).build();
    }

    public MedicationDto createMedication(Long idPaciente, MedicationRequestDto datos) {
        return restClient.post()
                .uri("/api/medications")
                .body(new MedicationCreateRequest(idPaciente, datos))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible registrar el medicamento");
                })
                .body(MedicationDto.class);
    }

    public MedicationDto getById(Long idMedicamento) {
        return restClient.get()
                .uri("/api/medications/{id}", idMedicamento)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Medicamento no encontrado");
                })
                .body(MedicationDto.class);
    }

    public List<MedicationDto> listByPatient(Long idPaciente) {
        return restClient.get()
                .uri("/api/medications/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MedicationDto>>() {
                });
    }

    public List<MedicationDto> listActiveByPatient(Long idPaciente) {
        return restClient.get()
                .uri("/api/medications/patient/{idPaciente}/active", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MedicationDto>>() {
                });
    }

    public MedicationDto deactivate(Long idMedicamento) {
        return restClient.patch()
                .uri("/api/medications/{id}/deactivate", idMedicamento)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible desactivar el medicamento");
                })
                .body(MedicationDto.class);
    }

    public void delete(Long idMedicamento) {
        restClient.delete()
                .uri("/api/medications/{id}", idMedicamento)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar el medicamento");
                })
                .toBodilessEntity();
    }

    /** Espejo de {@code MedicationRequestDto} de {@code medication-service}. */
    private static final class MedicationCreateRequest {
        private final Long idPaciente;
        private final String nombreMedicamento;
        private final String dosis;
        private final int frecuenciaHoras;
        private final LocalDate fechaInicio;
        private final LocalDate fechaTermino;
        private final int activo;

        MedicationCreateRequest(Long idPaciente, MedicationRequestDto datos) {
            this.idPaciente = idPaciente;
            this.nombreMedicamento = datos.getNombreMedicamento();
            this.dosis = datos.getDosis();
            this.frecuenciaHoras = datos.getFrecuenciaHoras();
            this.fechaInicio = datos.getFechaInicio();
            this.fechaTermino = datos.getFechaTermino();
            this.activo = 1;
        }

        public Long getIdPaciente() {
            return idPaciente;
        }

        public String getNombreMedicamento() {
            return nombreMedicamento;
        }

        public String getDosis() {
            return dosis;
        }

        public int getFrecuenciaHoras() {
            return frecuenciaHoras;
        }

        public LocalDate getFechaInicio() {
            return fechaInicio;
        }

        public LocalDate getFechaTermino() {
            return fechaTermino;
        }

        public int getActivo() {
            return activo;
        }
    }

}
