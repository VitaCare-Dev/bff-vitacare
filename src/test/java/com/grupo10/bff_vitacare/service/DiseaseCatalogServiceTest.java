package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiseaseCatalogServiceTest {

    @Mock
    private PatientServiceClient patientServiceClient;

    @InjectMocks
    private DiseaseCatalogService diseaseCatalogService;

    @Test
    void listDiseasesDelegatesToTheClient() {
        DiseaseDto disease = new DiseaseDto();
        disease.setIdEnfermedad(1L);
        when(patientServiceClient.listDiseases()).thenReturn(List.of(disease));

        List<DiseaseDto> result = diseaseCatalogService.listDiseases();

        assertThat(result).hasSize(1);
    }
}
