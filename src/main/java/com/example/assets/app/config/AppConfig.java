package com.example.assets.app.config;

import com.example.assets.app.SearchAssetsService;
import com.example.assets.app.UploadAssetService;
import com.example.assets.domain.ports.AssetRepositoryPort;
import com.example.assets.domain.usecase.SearchAssetsUseCase;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.infra.persistence.AssetJpaRepository;
import com.example.assets.infra.persistence.AssetRepositoryAdapter;
import com.example.assets.infra.publisher.PublisherOrchestrator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class.
 * Defines beans for use cases and adapters to follow
 * Clean Architecture and dependency inversion.
 */
@Configuration
public class AppConfig {

    @Bean
    public UploadAssetUseCase uploadAssetUseCase(
            AssetRepositoryPort repo,
            PublisherOrchestrator orchestrator
    ) {
        return new UploadAssetService(repo, orchestrator);
    }

    @Bean
    public SearchAssetsUseCase searchAssetsUseCase(AssetRepositoryPort repo) {
        return new SearchAssetsService(repo);
    }

    @Bean
    public AssetRepositoryPort assetRepositoryPort(AssetJpaRepository jpa) {
        return new AssetRepositoryAdapter(jpa);
    }
}
