package com.example.assets.domain.ports;

import com.example.assets.domain.model.Asset;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for accessing and persisting assets.
 * Abstracts the persistence mechanism (DB, in-memory, etc.).
 */
public interface AssetRepositoryPort {

    /**
     * Saves or updates an asset.
     *
     * @param asset domain asset to save
     * @return the saved asset
     */
    Asset save(Asset asset);
    /**
     * Finds an asset by its ID.
     *
     * @param id UUID of the asset
     * @return optional containing the asset if found
     */
    Optional<Asset> findById(UUID id);

    /**
     * Searches for assets with optional filters and sorting.
     *
     * @param start         optional start date filter
     * @param end           optional end date filter
     * @param filenameRegex optional regex for filename
     * @param filetype      optional file type filter
     * @param asc           true for ascending sort by uploadDate, false for descending
     * @return list of matching assets
     */
    List<Asset> search(
            Instant start,
            Instant end,
            String filenameRegex,
            String filetype,
            boolean asc
    );
}
