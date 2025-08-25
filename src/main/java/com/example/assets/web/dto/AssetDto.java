package com.example.assets.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.assets.domain.model.AssetStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO representing an asset exposed via the REST API.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AssetDto {

    private UUID id;

    private String filename;

    private String contentType;

    private String url;

    private Long size;

    private Instant uploadDate;

    private AssetStatus status;
}
