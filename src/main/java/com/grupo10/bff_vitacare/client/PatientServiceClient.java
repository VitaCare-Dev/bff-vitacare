package com.grupo10.bff_vitacare.client;

import java.util.List;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia {@code patient-service}, usado para crear la ficha de
 * paciente asociada a un usuario ya sincronizado en {@code user-service}.
 */
@Component
public class PatientServiceClient {

    private final RestClient restClient;

    /**
     * @param restClientBuilder      builder de {@link RestClient} inyectado por Spring
     * @param patientServiceBaseUrl  URL base de {@code patient-service}
     */
    public PatientServiceClient(RestClient.Builder restClientBuilder,
                                 @Value("${patient-service.base-url}") String patientServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(patientServiceBaseUrl).build();
    }

    /**
     * Crea un paciente en {@code patient-service} para el usuario indicado.
     *
     * @param idUsuario identificador interno del usuario ya sincronizado
     * @param datos     datos del paciente a registrar
     * @return el paciente creado
     * @throws RegistrationConflictException si el RUT ya está registrado o el
     *                                        usuario ya tiene un paciente asociado
     */
    public PatientDto createPatient(Long idUsuario, RegisterPatientRequestDto datos) {
        PatientCreateRequest body = new PatientCreateRequest(idUsuario, datos);
        return restClient.post()
                .uri("/api/patients")
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RegistrationConflictException(
                            "No fue posible registrar el paciente con RUT " + datos.getRut());
                })
                .body(PatientDto.class);
    }

    /**
     * Busca el paciente asociado a un usuario.
     *
     * @param idUsuario identificador interno del usuario
     * @return el paciente encontrado
     * @throws PatientNotFoundException si el usuario todavía no tiene un paciente registrado
     */
    public PatientDto findByUserId(Long idUsuario) {
        return restClient.get()
                .uri("/api/patients/by-usuario/{idUsuario}", idUsuario)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PatientNotFoundException(
                            "Debes completar tu registro de paciente antes de continuar");
                })
                .body(PatientDto.class);
    }

    /**
     * Lista el catálogo de enfermedades disponible en {@code patient-service}.
     *
     * @return la lista de enfermedades del catálogo
     */
    public List<DiseaseDto> listDiseases() {
        return restClient.get()
                .uri("/api/diseases")
                .retrieve()
                .body(new ParameterizedTypeReference<List<DiseaseDto>>() {
                });
    }

    /**
     * Asocia una enfermedad crónica a un paciente.
     *
     * @param idPaciente   identificador del paciente, ya resuelto a partir del usuario autenticado
     * @param idEnfermedad identificador de la enfermedad del catálogo
     * @throws RegistrationConflictException si {@code patient-service} rechaza la asociación
     */
    public void registerDisease(Long idPaciente, Long idEnfermedad) {
        restClient.post()
                .uri("/api/chronic-diseases/register")
                .body(Map.of("idPaciente", idPaciente, "idEnfermedad", idEnfermedad))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RegistrationConflictException(
                            "No fue posible asociar la enfermedad " + idEnfermedad + " al paciente");
                })
                .toBodilessEntity();
    }

    /**
     * Obtiene los umbrales médicos derivados de la(s) enfermedad(es) crónica(s) del paciente.
     *
     * @param idPaciente identificador del paciente
     * @return los umbrales médicos
     * @throws UpstreamErrorException si el paciente no tiene umbrales calculados todavía
     *                                 (ej. no registró ninguna enfermedad crónica)
     */
    public MedicalThresholdDto getThresholds(Long idPaciente) {
        return restClient.get()
                .uri("/api/chronic-diseases/thresholds/{idPaciente}", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(),
                            "No hay umbrales médicos calculados para este paciente");
                })
                .body(MedicalThresholdDto.class);
    }

    /**
     * Lista las enfermedades crónicas asociadas a un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la lista de enfermedades del paciente (vacía si no tiene ninguna)
     */
    public List<DiseaseDto> getPatientDiseases(Long idPaciente) {
        return restClient.get()
                .uri("/api/chronic-diseases/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<DiseaseDto>>() {
                });
    }

    /**
     * Elimina un paciente (borrado de cuenta). La base de datos cae en cascada
     * hacia direcciones, enfermedades, umbrales, controles de salud y medicamentos.
     *
     * @param idPaciente identificador del paciente a eliminar
     */
    public void deletePatient(Long idPaciente) {
        restClient.delete()
                .uri("/api/patients/{idPaciente}", idPaciente)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar el paciente");
                })
                .toBodilessEntity();
    }

    /**
     * Actualiza parcialmente los datos de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @param datos      campos a actualizar (los nulos se ignoran, igual que en {@code patient-service})
     * @return el paciente actualizado
     */
    public PatientDto updatePatient(Long idPaciente, UpdatePatientRequestDto datos) {
        return restClient.put()
                .uri("/api/patients/{idPaciente}", idPaciente)
                .body(datos)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible actualizar el paciente");
                })
                .body(PatientDto.class);
    }

    /**
     * Crea una dirección para el paciente indicado.
     *
     * @param idPaciente identificador del paciente
     * @param datos      datos de la dirección
     * @return la dirección creada
     */
    public AddressDto createAddress(Long idPaciente, AddressRequestDto datos) {
        return restClient.post()
                .uri("/api/addresses")
                .body(new AddressUpsertRequest(idPaciente, datos))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible registrar la dirección");
                })
                .body(AddressDto.class);
    }

    /**
     * Lista las direcciones de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return las direcciones del paciente
     */
    public List<AddressDto> listAddressesByPatient(Long idPaciente) {
        return restClient.get()
                .uri("/api/addresses/patient/{idPaciente}", idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AddressDto>>() {
                });
    }

    /**
     * Busca una dirección por su identificador (usado para el chequeo de propiedad).
     *
     * @param idDireccion identificador de la dirección
     * @return la dirección encontrada
     * @throws UpstreamErrorException si no existe
     */
    public AddressDto getAddressById(Long idDireccion) {
        return restClient.get()
                .uri("/api/addresses/{id}", idDireccion)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Dirección no encontrada");
                })
                .body(AddressDto.class);
    }

    /**
     * Actualiza una dirección, reafirmando el {@code idPaciente} dueño para
     * que {@code patient-service} no reasigne el dueño según lo que llegue del cliente.
     *
     * @param idDireccion identificador de la dirección
     * @param idPaciente  identificador del paciente dueño
     * @param datos       campos a actualizar
     * @return la dirección actualizada
     */
    public AddressDto updateAddress(Long idDireccion, Long idPaciente, AddressRequestDto datos) {
        return restClient.put()
                .uri("/api/addresses/{id}", idDireccion)
                .body(new AddressUpsertRequest(idPaciente, datos))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible actualizar la dirección");
                })
                .body(AddressDto.class);
    }

    /**
     * Elimina una dirección.
     *
     * @param idDireccion identificador de la dirección
     */
    public void deleteAddress(Long idDireccion) {
        restClient.delete()
                .uri("/api/addresses/{id}", idDireccion)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar la dirección");
                })
                .toBodilessEntity();
    }

    /** Espejo de {@code AddressRequestDto} de {@code patient-service}. */
    private static final class AddressUpsertRequest {
        private final Long idPaciente;
        private final String calle;
        private final String numero;
        private final String comuna;
        private final String region;

        AddressUpsertRequest(Long idPaciente, AddressRequestDto datos) {
            this.idPaciente = idPaciente;
            this.calle = datos.getCalle();
            this.numero = datos.getNumero();
            this.comuna = datos.getComuna();
            this.region = datos.getRegion();
        }

        public Long getIdPaciente() {
            return idPaciente;
        }

        public String getCalle() {
            return calle;
        }

        public String getNumero() {
            return numero;
        }

        public String getComuna() {
            return comuna;
        }

        public String getRegion() {
            return region;
        }
    }

    /** Espejo de {@code PatientRequestDto} de {@code patient-service}. */
    private static final class PatientCreateRequest {

        private final Long idUsuario;
        private final String rut;
        private final String nombre;
        private final String apellidoPaterno;
        private final String apellidoMaterno;
        private final java.time.LocalDate fechaNacimiento;
        private final String telefonoPrincipal;
        private final String telefonoSecundario;

        PatientCreateRequest(Long idUsuario, RegisterPatientRequestDto datos) {
            this.idUsuario = idUsuario;
            this.rut = datos.getRut();
            this.nombre = datos.getNombre();
            this.apellidoPaterno = datos.getApellidoPaterno();
            this.apellidoMaterno = datos.getApellidoMaterno();
            this.fechaNacimiento = datos.getFechaNacimiento();
            this.telefonoPrincipal = datos.getTelefonoPrincipal();
            this.telefonoSecundario = datos.getTelefonoSecundario();
        }

        public Long getIdUsuario() {
            return idUsuario;
        }

        public String getRut() {
            return rut;
        }

        public String getNombre() {
            return nombre;
        }

        public String getApellidoPaterno() {
            return apellidoPaterno;
        }

        public String getApellidoMaterno() {
            return apellidoMaterno;
        }

        public java.time.LocalDate getFechaNacimiento() {
            return fechaNacimiento;
        }

        public String getTelefonoPrincipal() {
            return telefonoPrincipal;
        }

        public String getTelefonoSecundario() {
            return telefonoSecundario;
        }
    }

}
