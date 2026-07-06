package com.grupo10.bff_vitacare.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class MeasurementServiceClientTest {

    private MockRestServiceServer server;
    private MeasurementServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new MeasurementServiceClient(builder, "http://measurement-service");
    }

    @Test
    void createGlucosePostsToGlucoseEndpoint() {
        GlucoseRequestDto request = new GlucoseRequestDto();
        request.setGlucosa(98);
        request.setPeriodo("AYUNAS");

        server.expect(requestTo("http://measurement-service/api/glucose"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idPaciente").value(1))
                .andExpect(jsonPath("$.glucosa").value(98))
                .andRespond(withSuccess("{\"idControl\":1,\"glucosa\":98}", MediaType.APPLICATION_JSON));

        GlucoseDto result = client.createGlucose(1L, request);

        assertThat(result.getGlucosa()).isEqualTo(98);
    }

    @Test
    void createGlucoseThrowsOnError() {
        server.expect(requestTo("http://measurement-service/api/glucose"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.createGlucose(1L, new GlucoseRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void listGlucoseByPatientReturnsTheList() {
        server.expect(requestTo("http://measurement-service/api/glucose/patient/1"))
                .andRespond(withSuccess("[{\"idControl\":1}]", MediaType.APPLICATION_JSON));

        List<GlucoseDto> results = client.listGlucoseByPatient(1L);

        assertThat(results).hasSize(1);
    }

    @Test
    void getLatestGlucoseReturnsTheMostRecent() {
        server.expect(requestTo("http://measurement-service/api/glucose/patient/1/latest"))
                .andRespond(withSuccess("{\"idControl\":1,\"glucosa\":100}", MediaType.APPLICATION_JSON));

        GlucoseDto result = client.getLatestGlucose(1L);

        assertThat(result.getGlucosa()).isEqualTo(100);
    }

    @Test
    void getLatestGlucoseThrowsWhenNoneRegistered() {
        server.expect(requestTo("http://measurement-service/api/glucose/patient/1/latest"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getLatestGlucose(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void createLipidsPostsToLipidsEndpoint() {
        server.expect(requestTo("http://measurement-service/api/lipids"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idPaciente").value(1))
                .andRespond(withSuccess("{\"idControl\":1,\"colesterolTotal\":200}", MediaType.APPLICATION_JSON));

        LipidsDto result = client.createLipids(1L, new LipidsRequestDto());

        assertThat(result.getColesterolTotal()).isEqualTo(200);
    }

    @Test
    void createLipidsThrowsOnError() {
        server.expect(requestTo("http://measurement-service/api/lipids"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.createLipids(1L, new LipidsRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void listLipidsByPatientReturnsTheList() {
        server.expect(requestTo("http://measurement-service/api/lipids/patient/1"))
                .andRespond(withSuccess("[{\"idControl\":1}]", MediaType.APPLICATION_JSON));

        List<LipidsDto> results = client.listLipidsByPatient(1L);

        assertThat(results).hasSize(1);
    }

    @Test
    void getLatestLipidsReturnsTheMostRecent() {
        server.expect(requestTo("http://measurement-service/api/lipids/patient/1/latest"))
                .andRespond(withSuccess("{\"idControl\":1}", MediaType.APPLICATION_JSON));

        LipidsDto result = client.getLatestLipids(1L);

        assertThat(result.getIdControl()).isEqualTo(1L);
    }

    @Test
    void getLatestLipidsThrowsWhenNoneRegistered() {
        server.expect(requestTo("http://measurement-service/api/lipids/patient/1/latest"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getLatestLipids(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void createVitalsPostsToVitalsEndpoint() {
        server.expect(requestTo("http://measurement-service/api/vitals"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idPaciente").value(1))
                .andRespond(withSuccess("{\"idControl\":1,\"temperatura\":36.6}", MediaType.APPLICATION_JSON));

        VitalsDto result = client.createVitals(1L, new VitalsRequestDto());

        assertThat(result.getTemperatura()).isEqualTo(36.6);
    }

    @Test
    void createVitalsThrowsOnError() {
        server.expect(requestTo("http://measurement-service/api/vitals"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.createVitals(1L, new VitalsRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void listVitalsByPatientReturnsTheList() {
        server.expect(requestTo("http://measurement-service/api/vitals/patient/1"))
                .andRespond(withSuccess("[{\"idControl\":1}]", MediaType.APPLICATION_JSON));

        List<VitalsDto> results = client.listVitalsByPatient(1L);

        assertThat(results).hasSize(1);
    }

    @Test
    void getLatestVitalsReturnsTheMostRecent() {
        server.expect(requestTo("http://measurement-service/api/vitals/patient/1/latest"))
                .andRespond(withSuccess("{\"idControl\":1}", MediaType.APPLICATION_JSON));

        VitalsDto result = client.getLatestVitals(1L);

        assertThat(result.getIdControl()).isEqualTo(1L);
    }

    @Test
    void getLatestVitalsThrowsWhenNoneRegistered() {
        server.expect(requestTo("http://measurement-service/api/vitals/patient/1/latest"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getLatestVitals(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void getHealthHistoryReturnsTheList() {
        server.expect(requestTo("http://measurement-service/api/controls/patient/1"))
                .andRespond(withSuccess("[{\"idControl\":1}]", MediaType.APPLICATION_JSON));

        List<HealthControlDto> history = client.getHealthHistory(1L);

        assertThat(history).hasSize(1);
    }

    @Test
    void getGlucoseByIdReturnsTheMeasurement() {
        server.expect(requestTo("http://measurement-service/api/glucose/1"))
                .andRespond(withSuccess("{\"idControl\":1}", MediaType.APPLICATION_JSON));

        GlucoseDto result = client.getGlucoseById(1L);

        assertThat(result.getIdControl()).isEqualTo(1L);
    }

    @Test
    void getGlucoseByIdThrowsWhenNotFound() {
        server.expect(requestTo("http://measurement-service/api/glucose/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getGlucoseById(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteGlucoseCallsDeleteEndpoint() {
        server.expect(requestTo("http://measurement-service/api/glucose/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.deleteGlucose(1L);

        server.verify();
    }

    @Test
    void deleteGlucoseThrowsOnError() {
        server.expect(requestTo("http://measurement-service/api/glucose/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.deleteGlucose(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void getLipidsByIdReturnsTheMeasurement() {
        server.expect(requestTo("http://measurement-service/api/lipids/1"))
                .andRespond(withSuccess("{\"idControl\":1}", MediaType.APPLICATION_JSON));

        LipidsDto result = client.getLipidsById(1L);

        assertThat(result.getIdControl()).isEqualTo(1L);
    }

    @Test
    void getLipidsByIdThrowsWhenNotFound() {
        server.expect(requestTo("http://measurement-service/api/lipids/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getLipidsById(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteLipidsCallsDeleteEndpoint() {
        server.expect(requestTo("http://measurement-service/api/lipids/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.deleteLipids(1L);

        server.verify();
    }

    @Test
    void deleteLipidsThrowsOnError() {
        server.expect(requestTo("http://measurement-service/api/lipids/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.deleteLipids(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void getVitalsByIdReturnsTheMeasurement() {
        server.expect(requestTo("http://measurement-service/api/vitals/1"))
                .andRespond(withSuccess("{\"idControl\":1}", MediaType.APPLICATION_JSON));

        VitalsDto result = client.getVitalsById(1L);

        assertThat(result.getIdControl()).isEqualTo(1L);
    }

    @Test
    void getVitalsByIdThrowsWhenNotFound() {
        server.expect(requestTo("http://measurement-service/api/vitals/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getVitalsById(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteVitalsCallsDeleteEndpoint() {
        server.expect(requestTo("http://measurement-service/api/vitals/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.deleteVitals(1L);

        server.verify();
    }

    @Test
    void deleteVitalsThrowsOnError() {
        server.expect(requestTo("http://measurement-service/api/vitals/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.deleteVitals(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }
}
