package com.grupo10.bff_vitacare.client;

import java.util.List;

import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia {@code measurement-service}, usado para registrar y
 * consultar mediciones de salud del paciente autenticado.
 */
@Component
public class MeasurementServiceClient {

    private final RestClient restClient;

    public MeasurementServiceClient(RestClient.Builder restClientBuilder,
                                     @Value("${measurement-service.base-url}") String measurementServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(measurementServiceBaseUrl).build();
    }

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

    public List<GlucoseDto> listGlucoseByPatient(Long idPaciente) {
        return restClient.get()
                .uri("/api/glucose/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GlucoseDto>>() {
                });
    }

    public GlucoseDto getLatestGlucose(Long idPaciente) {
        return restClient.get()
                .uri("/api/glucose/patient/{idPaciente}/latest", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "El paciente no tiene mediciones de glucosa");
                })
                .body(GlucoseDto.class);
    }

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

    public List<LipidsDto> listLipidsByPatient(Long idPaciente) {
        return restClient.get()
                .uri("/api/lipids/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LipidsDto>>() {
                });
    }

    public LipidsDto getLatestLipids(Long idPaciente) {
        return restClient.get()
                .uri("/api/lipids/patient/{idPaciente}/latest", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "El paciente no tiene mediciones lipídicas");
                })
                .body(LipidsDto.class);
    }

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

    public List<VitalsDto> listVitalsByPatient(Long idPaciente) {
        return restClient.get()
                .uri("/api/vitals/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<VitalsDto>>() {
                });
    }

    public VitalsDto getLatestVitals(Long idPaciente) {
        return restClient.get()
                .uri("/api/vitals/patient/{idPaciente}/latest", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "El paciente no tiene signos vitales registrados");
                })
                .body(VitalsDto.class);
    }

    public List<HealthControlDto> getHealthHistory(Long idPaciente) {
        return restClient.get()
                .uri("/api/controls/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<HealthControlDto>>() {
                });
    }

    public GlucoseDto getGlucoseById(Long idControl) {
        return restClient.get()
                .uri("/api/glucose/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Medición de glucosa no encontrada");
                })
                .body(GlucoseDto.class);
    }

    public void deleteGlucose(Long idControl) {
        restClient.delete()
                .uri("/api/glucose/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar la medición de glucosa");
                })
                .toBodilessEntity();
    }

    public LipidsDto getLipidsById(Long idControl) {
        return restClient.get()
                .uri("/api/lipids/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Medición lipídica no encontrada");
                })
                .body(LipidsDto.class);
    }

    public void deleteLipids(Long idControl) {
        restClient.delete()
                .uri("/api/lipids/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar la medición lipídica");
                })
                .toBodilessEntity();
    }

    public VitalsDto getVitalsById(Long idControl) {
        return restClient.get()
                .uri("/api/vitals/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Medición de signos vitales no encontrada");
                })
                .body(VitalsDto.class);
    }

    public void deleteVitals(Long idControl) {
        restClient.delete()
                .uri("/api/vitals/{idControl}", idControl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar los signos vitales");
                })
                .toBodilessEntity();
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
