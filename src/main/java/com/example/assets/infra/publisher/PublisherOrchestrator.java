package com.example.assets.infra.publisher;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.model.AssetStatus;
import com.example.assets.domain.ports.AssetPublisherPort;
import com.example.assets.domain.ports.AssetRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Orchestrates the asynchronous publishing of assets.
 * It updates the asset status in the repository and delegates publishing
 * to the configured AssetPublisherPort.
 */
@Component
public class PublisherOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(PublisherOrchestrator.class);

    private final AssetRepositoryPort repo;
    private final AssetPublisherPort publisher;

    public PublisherOrchestrator(AssetRepositoryPort repo, AssetPublisherPort publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    @Async
    @Transactional
    public void publishAsync(UUID assetId, byte[] data, String filename, String contentType) {
        repo.findById(assetId).ifPresent(asset -> {
            repo.save(copy(asset, AssetStatus.PROCESSING, null, null, null));

            try {
                log.info("Publishing asset {}", asset.id());
                var info = publisher.publish(data, filename, contentType);
                log.info("Successfully published asset {}", asset.id());
                repo.save(copy(asset, AssetStatus.PUBLISHED, info.url(), info.size(), Instant.now()));
            } catch (Exception e) {
                log.error("Failed to publish asset {}", asset.id(), e);
                repo.save(copy(asset, AssetStatus.FAILED, null, null, null));
            }
        });
    }

    private Asset copy(Asset source, AssetStatus status, String url, Long size, Instant uploadDate) {
        return new Asset(
                source.id(),
                source.filename(),
                source.contentType(),
                url != null ? url : source.url(),
                size != null ? size : source.size(),
                uploadDate != null ? uploadDate : source.uploadDate(),
                status
        );
    }
}
