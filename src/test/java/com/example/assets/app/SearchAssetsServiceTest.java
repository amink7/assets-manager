package com.example.assets.app;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.ports.AssetRepositoryPort;
import com.example.assets.domain.model.SortDirection;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchAssetsServiceTest {

    @Test
    void execute_shouldDelegateFiltersAndSortingToRepository() {
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        SearchAssetsService service = new SearchAssetsService(repo);

        Instant start = Instant.parse("2020-01-01T00:00:00Z");
        Instant end = Instant.parse("2020-12-31T23:59:59Z");
        String filenameRegex = "file.*";
        String filetype = "image/png";
        SortDirection direction = SortDirection.ASC;

        Asset asset = new Asset(UUID.randomUUID(), "file1.png", filetype, null, 10L, start, null);
        when(repo.search(start, end, filenameRegex, filetype, direction)).thenReturn(List.of(asset));

        List<Asset> result = service.execute(start, end, filenameRegex, filetype, direction);

        assertThat(result).containsExactly(asset);
        verify(repo).search(start, end, filenameRegex, filetype, direction);
    }

    @Test
    void execute_shouldAllowNullFiltersAndDescSort() {
        AssetRepositoryPort repo = mock(AssetRepositoryPort.class);
        SearchAssetsService service = new SearchAssetsService(repo);

        List<Asset> expected = List.of();
        when(repo.search(null, null, null, null, SortDirection.DESC)).thenReturn(expected);

        List<Asset> result = service.execute(null, null, null, null, SortDirection.DESC);

        assertThat(result).isSameAs(expected);
        verify(repo).search(null, null, null, null, SortDirection.DESC);
    }
}