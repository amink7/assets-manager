package com.example.assets.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO representing the request body for uploading an asset file.
 */
@Data
public class AssetFileUploadRequest {

    @NotBlank
    private String filename;

    @NotBlank
    private String encodedFile;

    @NotBlank
    private String contentType;
}
