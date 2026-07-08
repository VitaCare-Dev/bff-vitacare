package com.grupo10.bff_vitacare.controller;

import java.time.LocalDate;
import java.util.List;

import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.PageResponseDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.service.MeasurementOrchestrationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registra y consulta las mediciones de salud del paciente autenticado.
 */
@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementOrchestrationService measurementOrchestrationService;

    /**
     * @param measurementOrchestrationService servicio que orquesta las mediciones de salud
     */
    public MeasurementController(MeasurementOrchestrationService measurementOrchestrationService) {
        this.measurementOrchestrationService = measurementOrchestrationService;
    }

    /**
     * {@code POST /api/measurements/glucose}: registra una medición de glucosa.
     *
     * @param jwt     ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param request datos de la medición
     * @return 201 con la medición creada
     */
    @PostMapping("/glucose")
    public ResponseEntity<GlucoseDto> createGlucose(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestBody GlucoseRequestDto request) {
        GlucoseDto response = measurementOrchestrationService.createGlucose(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * {@code GET /api/measurements/glucose}: lista el historial paginado de
     * mediciones de glucosa, opcionalmente acotado a un rango de fechas.
     *
     * @param jwt   ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param page  número de página solicitado (base 0)
     * @param size  tamaño de página solicitado
     * @param desde fecha inicial (inclusive) del rango, en formato {@code yyyy-MM-dd}
     * @param hasta fecha final (inclusive) del rango, en formato {@code yyyy-MM-dd}
     * @return 200 con la página de mediciones de glucosa del paciente
     */
    @GetMapping("/glucose")
    public ResponseEntity<PageResponseDto<GlucoseDto>> listGlucose(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(measurementOrchestrationService.listGlucose(jwt, page, size, desde, hasta));
    }

    /**
     * {@code GET /api/measurements/glucose/latest}: devuelve la medición de glucosa más reciente.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con la última medición de glucosa
     */
    @GetMapping("/glucose/latest")
    public ResponseEntity<GlucoseDto> getLatestGlucose(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getLatestGlucose(jwt));
    }

    /**
     * {@code GET /api/measurements/glucose/{id}}: busca una medición de glucosa por su identificador.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador del control
     * @return 200 con la medición de glucosa encontrada
     */
    @GetMapping("/glucose/{id}")
    public ResponseEntity<GlucoseDto> getGlucoseById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return ResponseEntity.ok(measurementOrchestrationService.getGlucoseById(jwt, id));
    }

    /**
     * {@code DELETE /api/measurements/glucose/{id}}: elimina una medición de glucosa.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador del control a eliminar
     * @return 204 sin contenido
     */
    @DeleteMapping("/glucose/{id}")
    public ResponseEntity<Void> deleteGlucose(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        measurementOrchestrationService.deleteGlucose(jwt, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /api/measurements/lipids}: registra un perfil lipídico.
     *
     * @param jwt     ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param request datos del perfil lipídico
     * @return 201 con el perfil lipídico creado
     */
    @PostMapping("/lipids")
    public ResponseEntity<LipidsDto> createLipids(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody LipidsRequestDto request) {
        LipidsDto response = measurementOrchestrationService.createLipids(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * {@code GET /api/measurements/lipids}: lista el historial paginado de
     * perfiles lipídicos, opcionalmente acotado a un rango de fechas.
     *
     * @param jwt   ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param page  número de página solicitado (base 0)
     * @param size  tamaño de página solicitado
     * @param desde fecha inicial (inclusive) del rango, en formato {@code yyyy-MM-dd}
     * @param hasta fecha final (inclusive) del rango, en formato {@code yyyy-MM-dd}
     * @return 200 con la página de perfiles lipídicos del paciente
     */
    @GetMapping("/lipids")
    public ResponseEntity<PageResponseDto<LipidsDto>> listLipids(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(measurementOrchestrationService.listLipids(jwt, page, size, desde, hasta));
    }

    /**
     * {@code GET /api/measurements/lipids/latest}: devuelve el perfil lipídico más reciente.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con el último perfil lipídico
     */
    @GetMapping("/lipids/latest")
    public ResponseEntity<LipidsDto> getLatestLipids(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getLatestLipids(jwt));
    }

    /**
     * {@code GET /api/measurements/lipids/{id}}: busca un perfil lipídico por su identificador.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador del control
     * @return 200 con el perfil lipídico encontrado
     */
    @GetMapping("/lipids/{id}")
    public ResponseEntity<LipidsDto> getLipidsById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return ResponseEntity.ok(measurementOrchestrationService.getLipidsById(jwt, id));
    }

    /**
     * {@code DELETE /api/measurements/lipids/{id}}: elimina un perfil lipídico.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador del control a eliminar
     * @return 204 sin contenido
     */
    @DeleteMapping("/lipids/{id}")
    public ResponseEntity<Void> deleteLipids(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        measurementOrchestrationService.deleteLipids(jwt, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /api/measurements/vitals}: registra signos vitales.
     *
     * @param jwt     ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param request datos de los signos vitales
     * @return 201 con la medición creada
     */
    @PostMapping("/vitals")
    public ResponseEntity<VitalsDto> createVitals(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody VitalsRequestDto request) {
        VitalsDto response = measurementOrchestrationService.createVitals(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * {@code GET /api/measurements/vitals}: lista el historial paginado de
     * signos vitales, opcionalmente acotado a un rango de fechas.
     *
     * @param jwt   ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param page  número de página solicitado (base 0)
     * @param size  tamaño de página solicitado
     * @param desde fecha inicial (inclusive) del rango, en formato {@code yyyy-MM-dd}
     * @param hasta fecha final (inclusive) del rango, en formato {@code yyyy-MM-dd}
     * @return 200 con la página de mediciones de signos vitales del paciente
     */
    @GetMapping("/vitals")
    public ResponseEntity<PageResponseDto<VitalsDto>> listVitals(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(measurementOrchestrationService.listVitals(jwt, page, size, desde, hasta));
    }

    /**
     * {@code GET /api/measurements/vitals/latest}: devuelve la medición de signos vitales más reciente.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con la última medición de signos vitales
     */
    @GetMapping("/vitals/latest")
    public ResponseEntity<VitalsDto> getLatestVitals(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getLatestVitals(jwt));
    }

    /**
     * {@code GET /api/measurements/vitals/{id}}: busca una medición de signos vitales por su identificador.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador del control
     * @return 200 con la medición de signos vitales encontrada
     */
    @GetMapping("/vitals/{id}")
    public ResponseEntity<VitalsDto> getVitalsById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return ResponseEntity.ok(measurementOrchestrationService.getVitalsById(jwt, id));
    }

    /**
     * {@code DELETE /api/measurements/vitals/{id}}: elimina una medición de signos vitales.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador del control a eliminar
     * @return 204 sin contenido
     */
    @DeleteMapping("/vitals/{id}")
    public ResponseEntity<Void> deleteVitals(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        measurementOrchestrationService.deleteVitals(jwt, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/measurements/history}: lista el historial combinado de
     * controles de salud (glucosa, lípidos y signos vitales agrupados por control).
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con el historial de controles del paciente
     */
    @GetMapping("/history")
    public ResponseEntity<List<HealthControlDto>> getHistory(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getHealthHistory(jwt));
    }

}
