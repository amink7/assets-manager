package com.example.assets.app;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.ports.AssetRepositoryPort;
import com.example.assets.domain.usecase.FindAssetUseCase;

import java.util.Optional;
import java.util.UUID;

/**
 * Application service for retrieving a single asset.
 */
public class FindAssetService implements FindAssetUseCase {

    private final AssetRepositoryPort repo;

    public FindAssetService(AssetRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Asset> execute(UUID id) {
        return repo.findById(id);
    }
}