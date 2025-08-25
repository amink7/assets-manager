package com.example.assets.app;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.model.AssetStatus;
import com.example.assets.domain.ports.AssetRepositoryPort;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.infra.publisher.PublisherOrchestrator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UploadAssetServiceTest {

    @Test
    void execute_shouldSavePendingAndTriggerAsyncPublish() {
        // given
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        PublisherOrchestrator orchestrator = mock(PublisherOrchestrator.class);
        UploadAssetUseCase service = new UploadAssetService(repo, orchestrator);

        byte[] data = "hello".getBytes();
        String filename = "a.txt";
        String contentType = "text/plain";

        // simulate repo.save returning the same Asset (optional)
        ArgumentCaptor<Asset> saved = ArgumentCaptor.forClass(Asset.class);
        when(repo.save(saved.capture())).thenAnswer(inv -> inv.getArgument(0));

        // when
        UUID id = service.execute(filename, contentType, data);

        // then
        assertThat(id).isNotNull();

        // 1) An Asset with PENDING status was saved
        Asset firstSave = saved.getValue();
        assertThat(firstSave).isNotNull();
        assertThat(firstSave.status()).isEqualTo(AssetStatus.PENDING);
        assertThat(firstSave.filename()).isEqualTo(filename);
        assertThat(firstSave.contentType()).isEqualTo(contentType);

        // 2) Async orchestrator was invoked
        verify(orchestrator, times(1))
                .publishAsync(eq(firstSave.id()), eq(data), eq(filename), eq(contentType));
    }
}
