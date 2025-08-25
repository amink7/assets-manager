package com.example.assets.infra.persistence;

import com.example.assets.domain.model.AssetStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity that represents an Asset persisted in the database.
 */
@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetEntity {

    @Id
    private UUID id;

    private String filename;

    private String contentType;

    private String url;

    private Long size;

    private Instant uploadDate;

    @Enumerated(EnumType.STRING)
    private AssetStatus status;
}
