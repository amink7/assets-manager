package com.example.assets.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * DTO representing the response after uploading an asset file.
 */
@Data
@AllArgsConstructor
public class AssetFileUploadResponse {

    private UUID id;
}
