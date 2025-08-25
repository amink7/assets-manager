package com.example.assets.domain.ports;

/**
 * Port for publishing assets to an external storage (e.g., local FS, S3, etc.).
 */
public interface AssetPublisherPort {

    /**
     * Publishes an asset to the underlying storage system.
     *
     * @param data        file content as byte array
     * @param filename    name of the file
     * @param contentType MIME type of the file
     * @return information about the published asset (URL and size)
     */
    PublishedInfo publish(byte[] data, String filename, String contentType);

    /**
     * Record representing information of a published asset.
     *
     * @param url  location of the published asset
     * @param size size of the asset in bytes
     */
    record PublishedInfo(String url, long size) {}
}
