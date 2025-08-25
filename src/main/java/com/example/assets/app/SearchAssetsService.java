package com.example.assets.app;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.ports.AssetRepositoryPort;
import com.example.assets.domain.usecase.SearchAssetsUseCase;

import java.time.Instant;
import java.util.List;

/**
 * Application service for searching assets.
 * Delegates the filtering logic to the AssetRepositoryPort.
 */
public class SearchAssetsService implements SearchAssetsUseCase {

    private final AssetRepositoryPort repo;

    public SearchAssetsService(AssetRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public List<Asset> execute(
            Instant start,
            Instant end,
            String filenameRegex,
            String filetype,
            boolean asc
    ) {
        return repo.search(start, end, filenameRegex, filetype, asc);
    }
}
