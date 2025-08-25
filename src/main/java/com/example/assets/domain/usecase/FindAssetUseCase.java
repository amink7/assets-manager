package com.example.assets.domain.usecase;

import com.example.assets.domain.model.Asset;

import java.util.Optional;
import java.util.UUID;

/**
 * Use case interface for finding an asset by its identifier.
 */
public interface FindAssetUseCase {

    /**
     * Finds an asset by its unique identifier.
     *
     * @param id UUID of the asset
     * @return optional containing the asset if found
     */
    Optional<Asset> execute(UUID id);
}