package com.grupo10.bff_vitacare.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Espejo del envoltorio de paginación devuelto por {@code measurement-service}
 * para los endpoints de historial de mediciones.
 */
@Data
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponseDto(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
