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

    /**
     * @param diseaseCatalogService servicio que expone el catálogo de enfermedades
     */
    public DiseaseController(DiseaseCatalogService diseaseCatalogService) {
        this.diseaseCatalogService = diseaseCatalogService;
    }

    /**
     * {@code GET /api/diseases}: lista el catálogo completo de enfermedades crónicas.
     *
     * @return 200 con el catálogo de enfermedades
     */
    @GetMapping
    public ResponseEntity<List<DiseaseDto>> listDiseases() {
        return ResponseEntity.ok(diseaseCatalogService.listDiseases());
    }

}
