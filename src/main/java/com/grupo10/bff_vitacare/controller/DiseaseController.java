package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.dto.DiseaseDto;
import com.grupo10.bff_vitacare.service.DiseaseCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone el catálogo de enfermedades disponibles para que el paciente elija la suya.
 */
@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    private final DiseaseCatalogService diseaseCatalogService;

    public DiseaseController(DiseaseCatalogService diseaseCatalogService) {
        this.diseaseCatalogService = diseaseCatalogService;
    }

    @GetMapping
    public ResponseEntity<List<DiseaseDto>> listDiseases() {
        return ResponseEntity.ok(diseaseCatalogService.listDiseases());
    }

}
