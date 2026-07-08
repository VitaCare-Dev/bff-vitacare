package com.grupo10.bff_vitacare.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * URL firmada (con SAS) hacia la que el cliente sube directamente la foto de
 * perfil a Azure Blob Storage, sin que el archivo pase por el BFF.
 */
@Data
@NoArgsConstructor
public class PhotoUploadUrlDto {
    private String uploadUrl;

    public PhotoUploadUrlDto(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
}
