package com.grupo10.bff_vitacare.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.grupo10.bff_vitacare.dto.MedicationDto;
import com.grupo10.bff_vitacare.dto.MedicationRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class MedicationServiceClientTest {

    private MockRestServiceServer server;
    private MedicationServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new MedicationServiceClient(builder, "http://medication-service");
    }

    @Test
    void createMedicationPostsToMedicationsEndpoint() {
        MedicationRequestDto request = new MedicationRequestDto();
        request.setNombreMedicamento("Metformina");
        request.setDosis("850 mg");
        request.setFrecuenciaHoras(12);

        server.expect(requestTo("http://medication-service/api/medications"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idPaciente").value(1))
                .andExpect(jsonPath("$.activo").value(1))
                .andRespond(withSuccess("{\"idMedicamento\":1,\"nombreMedicamento\":\"Metformina\"}", MediaType.APPLICATION_JSON));

        MedicationDto result = client.createMedication(1L, request);

        assertThat(result.getNombreMedicamento()).isEqualTo("Metformina");
    }

    @Test
    void createMedicationThrowsOnError() {
        server.expect(requestTo("http://medication-service/api/medications"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.createMedication(1L, new MedicationRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void getByIdReturnsTheMedication() {
        server.expect(requestTo("http://medication-service/api/medications/1"))
                .andRespond(withSuccess("{\"idMedicamento\":1}", MediaType.APPLICATION_JSON));

        MedicationDto result = client.getById(1L);

        assertThat(result.getIdMedicamento()).isEqualTo(1L);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        server.expect(requestTo("http://medication-service/api/medications/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getById(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void listByPatientReturnsAllMedications() {
        server.expect(requestTo("http://medication-service/api/medications/patient/1"))
                .andRespond(withSuccess("[{\"idMedicamento\":1},{\"idMedicamento\":2}]", MediaType.APPLICATION_JSON));

        List<MedicationDto> result = client.listByPatient(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void listActiveByPatientReturnsOnlyActiveMedications() {
        server.expect(requestTo("http://medication-service/api/medications/patient/1/active"))
                .andRespond(withSuccess("[{\"idMedicamento\":1}]", MediaType.APPLICATION_JSON));

        List<MedicationDto> result = client.listActiveByPatient(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void deactivatePatchesToDeactivateEndpoint() {
        server.expect(requestTo("http://medication-service/api/medications/1/deactivate"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"idMedicamento\":1,\"activo\":0}", MediaType.APPLICATION_JSON));

        MedicationDto result = client.deactivate(1L);

        assertThat(result.getActivo()).isEqualTo(0);
    }

    @Test
    void deactivateThrowsOnError() {
        server.expect(requestTo("http://medication-service/api/medications/1/deactivate"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.deactivate(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteCallsDeleteEndpoint() {
        server.expect(requestTo("http://medication-service/api/medications/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.delete(1L);

        server.verify();
    }

    @Test
    void deleteThrowsOnError() {
        server.expect(requestTo("http://medication-service/api/medications/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.delete(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }
}
