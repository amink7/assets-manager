package com.example.assets.app;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.ports.AssetRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FindAssetServiceTest {

    @Test
    void execute_shouldReturnAssetWhenFound() {
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        FindAssetService service = new FindAssetService(repo);

        UUID id = UUID.randomUUID();
        Asset asset = new Asset(id, "file.pdf", "application/pdf", null, 123L, Instant.now(), null);
        when(repo.findById(id)).thenReturn(Optional.of(asset));

        Optional<Asset> result = service.execute(id);

        assertThat(result).contains(asset);
        verify(repo).findById(id);
    }

    @Test
    void execute_shouldReturnEmptyWhenAssetNotFound() {
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        FindAssetService service = new FindAssetService(repo);

        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        Optional<Asset> result = service.execute(id);

        assertThat(result).isEmpty();
        verify(repo).findById(id);
    }
}