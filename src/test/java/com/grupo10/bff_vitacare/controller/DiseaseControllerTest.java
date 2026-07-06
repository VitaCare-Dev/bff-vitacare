package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.DiseaseDto;
import com.grupo10.bff_vitacare.service.DiseaseCatalogService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DiseaseControllerTest {

    @Mock
    private DiseaseCatalogService diseaseCatalogService;

    @InjectMocks
    private DiseaseController diseaseController;

    @Test
    void listDiseasesReturnsTheCatalog() {
        when(diseaseCatalogService.listDiseases()).thenReturn(List.of(new DiseaseDto()));

        ResponseEntity<List<DiseaseDto>> response = diseaseController.listDiseases();

        assertThat(response.getBody()).hasSize(1);
    }
}
