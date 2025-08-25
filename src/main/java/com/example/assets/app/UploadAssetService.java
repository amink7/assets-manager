package com.example.assets.app;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.model.AssetStatus;
import com.example.assets.domain.ports.AssetRepositoryPort;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.infra.publisher.PublisherOrchestrator;

import java.util.UUID;

/**
 * Application service for uploading assets.
 * Creates a pending asset, saves it, and triggers asynchronous publishing.
 */
public class UploadAssetService implements UploadAssetUseCase {

    private final AssetRepositoryPort repo;
    private final PublisherOrchestrator orchestrator;

    public UploadAssetService(AssetRepositoryPort repo, PublisherOrchestrator orchestrator) {
        this.repo = repo;
        this.orchestrator = orchestrator;
    }

    @Override
    public UUID execute(String filename, String contentType, byte[] data) {
        var id = UUID.randomUUID();

        var pending = new Asset(
                id,
                filename,
                contentType,
                null,
                null,
                null,
                AssetStatus.PENDING
        );

        repo.save(pending);
        orchestrator.publishAsync(id, data, filename, contentType);

        return id;
    }
}
