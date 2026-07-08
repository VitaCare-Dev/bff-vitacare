package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfilePhotoServiceTest {

    // Cadena de conexión de ejemplo, con el mismo formato que una real, pero
    // con una AccountKey inventada (cualquier Base64 válido sirve, ya que la
    // firma es puramente local): no apunta a ninguna cuenta de Azure real.
    private static final String FAKE_CONNECTION_STRING =
            "DefaultEndpointsProtocol=https;AccountName=vitacareprofilephotos;"
                    + "AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;"
                    + "EndpointSuffix=core.windows.net";

    private ProfilePhotoService profilePhotoService;

    @BeforeEach
    void setUp() {
        profilePhotoService = new ProfilePhotoService(FAKE_CONNECTION_STRING, "profile-photos");
    }

    @Test
    void generateUploadUrlPointsToTheDeterministicBlobForThePatient() {
        String url = profilePhotoService.generateUploadUrl(1L);

        assertThat(url).contains("/profile-photos/paciente-1.jpg");
        assertThat(url).contains("sp=cw");
        assertThat(url).contains("sig=");
    }

    @Test
    void generateUploadUrlDiffersByPatientId() {
        String url1 = profilePhotoService.generateUploadUrl(1L);
        String url2 = profilePhotoService.generateUploadUrl(2L);

        assertThat(url1).contains("paciente-1.jpg");
        assertThat(url2).contains("paciente-2.jpg");
    }

    @Test
    void getBaseBlobUrlHasNoSasQueryParams() {
        String baseUrl = profilePhotoService.getBaseBlobUrl(1L);

        assertThat(baseUrl).isEqualTo(
                "https://vitacareprofilephotos.blob.core.windows.net/profile-photos/paciente-1.jpg");
    }

    @Test
    void generateReadUrlSignsTheSameBlobNameFromTheBaseUrl() {
        String baseUrl = profilePhotoService.getBaseBlobUrl(1L);

        String readUrl = profilePhotoService.generateReadUrl(baseUrl);

        assertThat(readUrl).contains("/profile-photos/paciente-1.jpg");
        assertThat(readUrl).contains("sp=r");
        assertThat(readUrl).contains("sig=");
    }

    @Test
    void doesNotFailToConstructWithAnEmptyConnectionString() {
        // El BFF debe poder arrancar aunque AZURE_STORAGE_CONNECTION_STRING
        // todavía no esté configurada; el error solo debe ocurrir al usar
        // realmente la funcionalidad de fotos.
        new ProfilePhotoService("", "profile-photos");
    }

    @Test
    void throwsOnFirstRealUseWhenTheConnectionStringIsMissingTheAccountKey() {
        ProfilePhotoService service = new ProfilePhotoService("AccountName=foo;", "profile-photos");

        assertThatThrownBy(() -> service.getBaseBlobUrl(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
