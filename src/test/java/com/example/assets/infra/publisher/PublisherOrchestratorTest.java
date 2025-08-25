package com.example.assets.infra.publisher;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.model.AssetStatus;
import com.example.assets.domain.ports.AssetPublisherPort;
import com.example.assets.domain.ports.AssetRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PublisherOrchestratorTest {

    @Test
    void publishAsync_shouldSaveProcessingAndPublishedOnSuccess() {
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        AssetPublisherPort publisher = mock(AssetPublisherPort.class);
        PublisherOrchestrator orchestrator = new PublisherOrchestrator(repo, publisher);

        UUID id = UUID.randomUUID();
        Asset existing = new Asset(id, "a.txt", "text/plain", null, null, Instant.EPOCH, AssetStatus.PENDING);
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(publisher.publish(any(), any(), any()))
                .thenReturn(new AssetPublisherPort.PublishedInfo("http://url", 5L));

        orchestrator.publishAsync(id, "data".getBytes(), "a.txt", "text/plain");

        ArgumentCaptor<Asset> saved = ArgumentCaptor.forClass(Asset.class);
        verify(repo, times(2)).save(saved.capture());
        List<Asset> states = saved.getAllValues();
        assertThat(states.get(0).status()).isEqualTo(AssetStatus.PROCESSING);
        Asset published = states.get(1);
        assertThat(published.status()).isEqualTo(AssetStatus.PUBLISHED);
        assertThat(published.url()).isEqualTo("http://url");
        assertThat(published.size()).isEqualTo(5L);
    }

    @Test
    void publishAsync_shouldSaveFailedWhenPublisherThrows() {
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        AssetPublisherPort publisher = mock(AssetPublisherPort.class);
        PublisherOrchestrator orchestrator = new PublisherOrchestrator(repo, publisher);

        UUID id = UUID.randomUUID();
        Asset existing = new Asset(id, "a.txt", "text/plain", null, null, Instant.EPOCH, AssetStatus.PENDING);
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(publisher.publish(any(), any(), any())).thenThrow(new RuntimeException("boom"));

        orchestrator.publishAsync(id, "data".getBytes(), "a.txt", "text/plain");

        ArgumentCaptor<Asset> saved = ArgumentCaptor.forClass(Asset.class);
        verify(repo, times(2)).save(saved.capture());
        List<Asset> states = saved.getAllValues();
        assertThat(states.get(0).status()).isEqualTo(AssetStatus.PROCESSING);
        assertThat(states.get(1).status()).isEqualTo(AssetStatus.FAILED);
    }

    @Test
    void publishAsync_shouldDoNothingWhenAssetNotFound() {
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        AssetPublisherPort publisher = mock(AssetPublisherPort.class);
        PublisherOrchestrator orchestrator = new PublisherOrchestrator(repo, publisher);

        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        orchestrator.publishAsync(id, "data".getBytes(), "a.txt", "text/plain");

        verify(repo, never()).save(any());
        verifyNoInteractions(publisher);
    }
}