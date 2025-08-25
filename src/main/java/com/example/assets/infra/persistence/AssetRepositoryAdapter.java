package com.example.assets.infra.persistence;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.ports.AssetRepositoryPort;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * Adapter implementation of AssetRepositoryPort that uses JPA.
 */
public class AssetRepositoryAdapter implements AssetRepositoryPort {

    private final AssetJpaRepository jpa;

    public AssetRepositoryAdapter(AssetJpaRepository jpa) {
        this.jpa = jpa;
    }

    private static AssetEntity toEntity(Asset asset) {
        return AssetEntity.builder()
                .id(asset.id())
                .filename(asset.filename())
                .contentType(asset.contentType())
                .url(asset.url())
                .size(asset.size())
                .uploadDate(asset.uploadDate())
                .status(asset.status())
                .build();
    }

    private static Asset toDomain(AssetEntity entity) {
        return new Asset(
                entity.getId(),
                entity.getFilename(),
                entity.getContentType(),
                entity.getUrl(),
                entity.getSize(),
                entity.getUploadDate(),
                entity.getStatus()
        );
    }

    @Override
    public Asset save(Asset asset) {
        return toDomain(jpa.save(toEntity(asset)));
    }

    @Override
    public Optional<Asset> findById(UUID id) {
        return jpa.findById(id).map(AssetRepositoryAdapter::toDomain);
    }

    @Override
    public List<Asset> search(
            Instant start,
            Instant end,
            String filenameRegex,
            String filetype,
            boolean asc
    ) {
        Specification<AssetEntity> spec = Specification.allOf(
                AssetSpecifications.uploadDateStart(start),
                AssetSpecifications.uploadDateEnd(end),
                AssetSpecifications.filenameLike(filenameRegex),
                AssetSpecifications.filetypeEq(filetype)
        );

        var sort = Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, "uploadDate");

        return jpa.findAll(spec, sort)
                .stream()
                .map(AssetRepositoryAdapter::toDomain)
                .collect(toList());
    }

}
