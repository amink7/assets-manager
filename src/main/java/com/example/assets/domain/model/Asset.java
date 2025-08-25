package com.example.assets.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model representing an asset.
 *
 * @param id          unique identifier of the asset
 * @param filename    original file name
 * @param contentType MIME type of the file
 * @param url         storage URL where the file is published
 * @param size        size of the file in bytes
 * @param uploadDate  timestamp when the file was uploaded/published
 * @param status      current processing status of the asset
 */
public record Asset(
        UUID id,
        String filename,
        String contentType,
        String url,
        Long size,
        Instant uploadDate,
        AssetStatus status
) {
}
