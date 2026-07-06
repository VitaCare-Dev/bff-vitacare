package com.grupo10.bff_vitacare.service;

import java.util.List;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
import org.springframework.stereotype.Service;

/**
 * Expone el catálogo de enfermedades disponible en {@code patient-service}.
 */
@Service
public class DiseaseCatalogService {

    private final PatientServiceClient patientServiceClient;

    /**
     * @param patientServiceClient cliente hacia {@code patient-service}
     */
    public DiseaseCatalogService(PatientServiceClient patientServiceClient) {
        this.patientServiceClient = patientServiceClient;
    }

    /**
     * Lista el catálogo completo de enfermedades crónicas disponibles.
     *
     * @return el catálogo de enfermedades
     */
    public List<DiseaseDto> listDiseases() {
        return patientServiceClient.listDiseases();
    }

}
