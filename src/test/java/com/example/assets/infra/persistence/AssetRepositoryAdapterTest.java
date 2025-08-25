package com.example.assets.infra.persistence;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.model.AssetStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(AssetRepositoryAdapter.class)
class AssetRepositoryAdapterTest {

    @Autowired
    AssetRepositoryAdapter adapter;

    @Autowired
    AssetJpaRepository jpa;

    @Test
    void save_shouldPersistAndMapAllFields() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Asset asset = new Asset(UUID.randomUUID(), "file.txt", "text/plain", "http://url", 123L, now, AssetStatus.PUBLISHED);

        Asset saved = adapter.save(asset);

        assertEquals(asset, saved);

        AssetEntity entity = jpa.findById(asset.id()).orElseThrow();
        assertEquals(asset.id(), entity.getId());
        assertEquals(asset.filename(), entity.getFilename());
        assertEquals(asset.contentType(), entity.getContentType());
        assertEquals(asset.url(), entity.getUrl());
        assertEquals(asset.size(), entity.getSize());
        assertEquals(asset.uploadDate(), entity.getUploadDate());
        assertEquals(asset.status(), entity.getStatus());
    }

    @Test
    void findById_shouldReturnMappedAsset() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AssetEntity entity = AssetEntity.builder()
                .id(UUID.randomUUID())
                .filename("file.txt")
                .contentType("text/plain")
                .url("http://url")
                .size(10L)
                .uploadDate(now)
                .status(AssetStatus.PUBLISHED)
                .build();
        jpa.save(entity);

        Optional<Asset> result = adapter.findById(entity.getId());
        assertTrue(result.isPresent());
        Asset asset = result.get();
        assertEquals(entity.getId(), asset.id());
        assertEquals(entity.getFilename(), asset.filename());
        assertEquals(entity.getContentType(), asset.contentType());
        assertEquals(entity.getUrl(), asset.url());
        assertEquals(entity.getSize(), asset.size());
        assertEquals(entity.getUploadDate(), asset.uploadDate());
        assertEquals(entity.getStatus(), asset.status());
    }

    @Test
    void search_shouldApplyFilters() {
        Instant jan15 = Instant.parse("2020-01-15T00:00:00Z");
        Instant jan20 = Instant.parse("2020-01-20T00:00:00Z");
        Instant feb05 = Instant.parse("2020-02-05T00:00:00Z");

        AssetEntity a1 = AssetEntity.builder()
                .id(UUID.randomUUID())
                .filename("report1.txt")
                .contentType("text/plain")
                .url("url1")
                .size(1L)
                .uploadDate(jan15)
                .status(AssetStatus.PUBLISHED)
                .build();
        AssetEntity a2 = AssetEntity.builder()
                .id(UUID.randomUUID())
                .filename("image.jpg")
                .contentType("image/jpeg")
                .url("url2")
                .size(2L)
                .uploadDate(jan20)
                .status(AssetStatus.PUBLISHED)
                .build();
        AssetEntity a3 = AssetEntity.builder()
                .id(UUID.randomUUID())
                .filename("report2.txt")
                .contentType("text/plain")
                .url("url3")
                .size(3L)
                .uploadDate(feb05)
                .status(AssetStatus.PUBLISHED)
                .build();
        jpa.saveAll(List.of(a1, a2, a3));

        Instant start = Instant.parse("2020-01-01T00:00:00Z");
        Instant end = Instant.parse("2020-01-31T23:59:59Z");
        List<Asset> result = adapter.search(start, end, "report*", "text/plain", true);

        assertEquals(1, result.size());
        Asset asset = result.get(0);
        assertEquals(a1.getId(), asset.id());
    }

    @Test
    void search_shouldSortByUploadDate() {
        AssetEntity oldEntity = AssetEntity.builder()
                .id(UUID.randomUUID())
                .filename("old.txt")
                .contentType("text/plain")
                .url("url")
                .size(1L)
                .uploadDate(Instant.parse("2020-01-01T00:00:00Z"))
                .status(AssetStatus.PUBLISHED)
                .build();
        AssetEntity newEntity = AssetEntity.builder()
                .id(UUID.randomUUID())
                .filename("new.txt")
                .contentType("text/plain")
                .url("url")
                .size(1L)
                .uploadDate(Instant.parse("2020-02-01T00:00:00Z"))
                .status(AssetStatus.PUBLISHED)
                .build();
        jpa.saveAll(List.of(oldEntity, newEntity));

        List<Asset> asc = adapter.search(null, null, null, null, true);
        assertEquals(List.of(oldEntity.getId(), newEntity.getId()),
                asc.stream().map(Asset::id).toList());

        List<Asset> desc = adapter.search(null, null, null, null, false);
        assertEquals(List.of(newEntity.getId(), oldEntity.getId()),
                desc.stream().map(Asset::id).toList());
    }
}