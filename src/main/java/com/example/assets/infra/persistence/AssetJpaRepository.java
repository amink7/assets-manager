package com.example.assets.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * JPA repository for AssetEntity.
 * Extends JpaRepository for CRUD operations and
 * JpaSpecificationExecutor for dynamic filtering.
 */
public interface AssetJpaRepository
        extends JpaRepository<AssetEntity, UUID>, JpaSpecificationExecutor<AssetEntity> {
}
