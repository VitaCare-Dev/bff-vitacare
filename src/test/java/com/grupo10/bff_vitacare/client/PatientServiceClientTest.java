package com.grupo10.bff_vitacare.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.grupo10.bff_vitacare.dto.AddressDto;
import com.grupo10.bff_vitacare.dto.AddressRequestDto;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
import com.grupo10.bff_vitacare.dto.MedicalThresholdDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.RegisterPatientRequestDto;
import com.grupo10.bff_vitacare.dto.UpdatePatientRequestDto;
import com.grupo10.bff_vitacare.exception.PatientNotFoundException;
import com.grupo10.bff_vitacare.exception.RegistrationConflictException;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class PatientServiceClientTest {

    private MockRestServiceServer server;
    private PatientServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new PatientServiceClient(builder, "http://patient-service");
    }

    private RegisterPatientRequestDto sampleRegisterRequest() {
        RegisterPatientRequestDto request = new RegisterPatientRequestDto();
        request.setRut("12.345.678-9");
        request.setNombre("María");
        request.setApellidoPaterno("Pérez");
        request.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        request.setTelefonoPrincipal("+56912345678");
        return request;
    }

    @Test
    void createPatientPostsToPatientsEndpoint() {
        server.expect(requestTo("http://patient-service/api/patients"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.rut").value("12.345.678-9"))
                .andRespond(withSuccess("{\"idPaciente\":1,\"rut\":\"12.345.678-9\"}", MediaType.APPLICATION_JSON));

        PatientDto patient = client.createPatient(1L, sampleRegisterRequest());

        assertThat(patient.getIdPaciente()).isEqualTo(1L);
    }

    @Test
    void createPatientThrowsConflictWhenRutAlreadyRegistered() {
        server.expect(requestTo("http://patient-service/api/patients"))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThatThrownBy(() -> client.createPatient(1L, sampleRegisterRequest()))
                .isInstanceOf(RegistrationConflictException.class);
    }

    @Test
    void createPatientThrowsUpstreamErrorPreservingStatusWhenNotAConflict() {
        // DEF-AUTH-02: antes, cualquier 4xx (incluido un 400 de validación sin
        // relación con el RUT) se colapsaba en el mismo 409 "RUT duplicado".
        server.expect(requestTo("http://patient-service/api/patients"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.createPatient(1L, sampleRegisterRequest()))
                .isInstanceOf(UpstreamErrorException.class)
                .satisfies(ex -> assertThat(((UpstreamErrorException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void findByUserIdReturnsThePatient() {
        server.expect(requestTo("http://patient-service/api/patients/by-usuario/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"idPaciente\":1}", MediaType.APPLICATION_JSON));

        PatientDto patient = client.findByUserId(1L);

        assertThat(patient.getIdPaciente()).isEqualTo(1L);
    }

    @Test
    void findByUserIdThrowsWhenNotRegistered() {
        server.expect(requestTo("http://patient-service/api/patients/by-usuario/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.findByUserId(1L))
                .isInstanceOf(PatientNotFoundException.class);
    }

    @Test
    void listDiseasesReturnsTheCatalog() {
        server.expect(requestTo("http://patient-service/api/diseases"))
                .andRespond(withSuccess("[{\"idEnfermedad\":1,\"nombreEnfermedad\":\"Diabetes\"}]", MediaType.APPLICATION_JSON));

        List<DiseaseDto> diseases = client.listDiseases();

        assertThat(diseases).hasSize(1);
        assertThat(diseases.get(0).getNombreEnfermedad()).isEqualTo("Diabetes");
    }

    @Test
    void registerDiseasePostsToChronicDiseasesEndpoint() {
        server.expect(requestTo("http://patient-service/api/chronic-diseases/register"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idPaciente").value(1))
                .andExpect(jsonPath("$.idEnfermedad").value(2))
                .andRespond(withStatus(HttpStatus.CREATED));

        client.registerDisease(1L, 2L);

        server.verify();
    }

    @Test
    void registerDiseaseThrowsConflictOnError() {
        server.expect(requestTo("http://patient-service/api/chronic-diseases/register"))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThatThrownBy(() -> client.registerDisease(1L, 2L))
                .isInstanceOf(RegistrationConflictException.class);
    }

    @Test
    void getThresholdsReturnsTheMedicalThresholds() {
        server.expect(requestTo("http://patient-service/api/chronic-diseases/thresholds/1"))
                .andRespond(withSuccess("{\"idUmbral\":1,\"glucosaMax\":180}", MediaType.APPLICATION_JSON));

        MedicalThresholdDto thresholds = client.getThresholds(1L);

        assertThat(thresholds.getGlucosaMax()).isEqualTo(180);
    }

    @Test
    void getThresholdsThrowsWhenNoneCalculated() {
        server.expect(requestTo("http://patient-service/api/chronic-diseases/thresholds/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getThresholds(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void getPatientDiseasesReturnsTheList() {
        server.expect(requestTo("http://patient-service/api/chronic-diseases/patient/1"))
                .andRespond(withSuccess("[{\"idEnfermedad\":1}]", MediaType.APPLICATION_JSON));

        List<DiseaseDto> diseases = client.getPatientDiseases(1L);

        assertThat(diseases).hasSize(1);
    }

    @Test
    void deletePatientCallsDeleteEndpoint() {
        server.expect(requestTo("http://patient-service/api/patients/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.deletePatient(1L);

        server.verify();
    }

    @Test
    void deletePatientThrowsOnError() {
        server.expect(requestTo("http://patient-service/api/patients/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.deletePatient(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void updatePatientPutsToPatientsEndpoint() {
        UpdatePatientRequestDto request = new UpdatePatientRequestDto();
        request.setNombre("María José");

        server.expect(requestTo("http://patient-service/api/patients/1"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("{\"idPaciente\":1,\"nombre\":\"María José\"}", MediaType.APPLICATION_JSON));

        PatientDto updated = client.updatePatient(1L, request);

        assertThat(updated.getNombre()).isEqualTo("María José");
    }

    @Test
    void updatePatientThrowsOnError() {
        server.expect(requestTo("http://patient-service/api/patients/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.updatePatient(1L, new UpdatePatientRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void updatePhotoUrlPatchesThePhotoEndpoint() {
        server.expect(requestTo("http://patient-service/api/patients/1/photo"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(jsonPath("$.fotoPerfilUrl").value("https://example.blob/paciente-1.jpg"))
                .andRespond(withSuccess(
                        "{\"idPaciente\":1,\"fotoPerfilUrl\":\"https://example.blob/paciente-1.jpg\"}",
                        MediaType.APPLICATION_JSON));

        PatientDto updated = client.updatePhotoUrl(1L, "https://example.blob/paciente-1.jpg");

        assertThat(updated.getFotoPerfilUrl()).isEqualTo("https://example.blob/paciente-1.jpg");
    }

    @Test
    void updatePhotoUrlThrowsOnError() {
        server.expect(requestTo("http://patient-service/api/patients/1/photo"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.updatePhotoUrl(1L, "https://example.blob/paciente-1.jpg"))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void createAddressPostsToAddressesEndpoint() {
        AddressRequestDto request = new AddressRequestDto();
        request.setCalle("Av. Providencia");
        request.setNumero("123");
        request.setComuna("Providencia");
        request.setRegion("Metropolitana");

        server.expect(requestTo("http://patient-service/api/addresses"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idPaciente").value(1))
                .andRespond(withSuccess("{\"idDireccion\":1,\"idPaciente\":1}", MediaType.APPLICATION_JSON));

        AddressDto address = client.createAddress(1L, request);

        assertThat(address.getIdDireccion()).isEqualTo(1L);
    }

    @Test
    void createAddressThrowsOnError() {
        server.expect(requestTo("http://patient-service/api/addresses"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.createAddress(1L, new AddressRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void listAddressesByPatientReturnsTheList() {
        server.expect(requestTo("http://patient-service/api/addresses/patient/1"))
                .andRespond(withSuccess("[{\"idDireccion\":1}]", MediaType.APPLICATION_JSON));

        List<AddressDto> addresses = client.listAddressesByPatient(1L);

        assertThat(addresses).hasSize(1);
    }

    @Test
    void getAddressByIdReturnsTheAddress() {
        server.expect(requestTo("http://patient-service/api/addresses/1"))
                .andRespond(withSuccess("{\"idDireccion\":1}", MediaType.APPLICATION_JSON));

        AddressDto address = client.getAddressById(1L);

        assertThat(address.getIdDireccion()).isEqualTo(1L);
    }

    @Test
    void getAddressByIdThrowsWhenNotFound() {
        server.expect(requestTo("http://patient-service/api/addresses/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getAddressById(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void updateAddressPutsToAddressesEndpoint() {
        AddressRequestDto request = new AddressRequestDto();
        request.setCalle("Nueva calle");

        server.expect(requestTo("http://patient-service/api/addresses/1"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.idPaciente").value(2))
                .andRespond(withSuccess("{\"idDireccion\":1,\"calle\":\"Nueva calle\"}", MediaType.APPLICATION_JSON));

        AddressDto updated = client.updateAddress(1L, 2L, request);

        assertThat(updated.getCalle()).isEqualTo("Nueva calle");
    }

    @Test
    void updateAddressThrowsOnError() {
        server.expect(requestTo("http://patient-service/api/addresses/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.updateAddress(1L, 2L, new AddressRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteAddressCallsDeleteEndpoint() {
        server.expect(requestTo("http://patient-service/api/addresses/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.deleteAddress(1L);

        server.verify();
    }

    @Test
    void deleteAddressThrowsOnError() {
        server.expect(requestTo("http://patient-service/api/addresses/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.deleteAddress(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }
}
