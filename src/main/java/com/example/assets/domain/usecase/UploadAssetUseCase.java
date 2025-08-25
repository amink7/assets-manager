package com.example.assets.domain.usecase;

import java.util.UUID;

/**
 * Use case interface for uploading assets.
 * Defines the contract that application services must implement.
 */
public interface UploadAssetUseCase {

    /**
     * Executes the upload of a new asset.
     *
     * @param filename    name of the uploaded file
     * @param contentType MIME type of the file
     * @param data        file content as byte array
     * @return UUID of the newly created asset
     */
    UUID execute(String filename, String contentType, byte[] data);
}
