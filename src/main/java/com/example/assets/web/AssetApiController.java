package com.example.assets.web;

import com.example.assets.web.api.generated.AssetApi;
import com.example.assets.web.dto.generated.*;
import com.example.assets.domain.usecase.SearchAssetsUseCase;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.domain.usecase.FindAssetUseCase;
import com.example.assets.domain.model.SortDirection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller implementando completamente la interfaz generada desde OpenAPI.
 * Este controlador reemplaza al AssetController original siguiendo el enfoque API-First.
 */
@RestController
public class AssetApiController implements AssetApi {

    private static final Logger log = LoggerFactory.getLogger(AssetApiController.class);

    private final UploadAssetUseCase uploadUC;
    private final SearchAssetsUseCase searchUC;
    private final FindAssetUseCase findUC;

    public AssetApiController(
            UploadAssetUseCase uploadUC,
            SearchAssetsUseCase searchUC,
            FindAssetUseCase findUC
    ) {
        this.uploadUC = uploadUC;
        this.searchUC = searchUC;
        this.findUC = findUC;
    }

    @Override
    public ResponseEntity<Asset> getAssetById(UUID id) {
        log.info("Fetching asset {}", id);

        try {
            return findUC.execute(id)
                    .map(this::toGeneratedDto)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> {
                        log.warn("Asset {} not found", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found");
                    });
        } catch (ResponseStatusException e) {
            throw e; // Re-throw ResponseStatusException as is
        } catch (Exception e) {
            log.error("Unexpected error fetching asset {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch asset");
        }
    }

    @Override
    public ResponseEntity<List<Asset>> getAssetsByFilter(OffsetDateTime uploadDateStart, OffsetDateTime uploadDateEnd, String filename, String filetype, String sortDirection) {
        log.info("Searching assets with uploadDateStart={}, uploadDateEnd={}, filename={}, filetype={}, sortDirection={}",
                uploadDateStart, uploadDateEnd, filename, filetype, sortDirection);

        try {
            // Validaciones
            validateSearchParameters(filename, filetype);

            // Conversión de fechas
            Instant start = uploadDateStart != null ? uploadDateStart.toInstant().truncatedTo(ChronoUnit.MILLIS) : null;
            Instant end = uploadDateEnd != null ? uploadDateEnd.toInstant().truncatedTo(ChronoUnit.MILLIS) : null;
            validateDateRange(start, end);

            // Conversión de sortDirection
            SortDirection direction = parseSortDirection(sortDirection);

            // Ejecutar búsqueda
            List<Asset> assets = searchUC.execute(start, end, filename, filetype, direction)
                    .stream()
                    .map(this::toGeneratedDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(assets);
        } catch (ResponseStatusException e) {
            throw e; // Re-throw ResponseStatusException as is
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Search failed");
        }
    }

    @Override
    public ResponseEntity<AssetFileUploadResponse> uploadAssetFile(AssetFileUploadRequest req) {
        log.info("Uploading {}", req.getFilename());

        try {
            // Decodificar el Base64 string a bytes
            byte[] data = Base64.getDecoder().decode(req.getEncodedFile());
            UUID id = uploadUC.execute(req.getFilename(), req.getContentType(), data);

            log.info("Upload accepted: {}", id);

            AssetFileUploadResponse response = new AssetFileUploadResponse();
            response.setId(id.toString());

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid Base64 encoding in upload request", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Base64 encoding");
        } catch (Exception e) {
            log.error("Unexpected error during upload", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }
    }

    @Override
    public ResponseEntity<List<Asset>> getAssetsByFilter(
            String uploadDateStart,
            String uploadDateEnd,
            String filename,
            String filetype,
            String sortDirection
    ) {
        log.info("Searching assets with uploadDateStart={}, uploadDateEnd={}, filename={}, filetype={}, sortDirection={}",
                uploadDateStart, uploadDateEnd, filename, filetype, sortDirection);

        try {
            // Validaciones
            validateSearchParameters(filename, filetype);

            // Conversión de fechas
            Instant start = parseInstant(uploadDateStart);
            Instant end = parseInstant(uploadDateEnd);
            validateDateRange(start, end);

            // Conversión de sortDirection
            SortDirection direction = parseSortDirection(sortDirection);

            // Ejecutar búsqueda
            List<Asset> assets = searchUC.execute(start, end, filename, filetype, direction)
                    .stream()
                    .map(this::toGeneratedDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(assets);
        } catch (ResponseStatusException e) {
            throw e; // Re-throw ResponseStatusException as is
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Search failed");
        }
    }

    @Override
    public ResponseEntity<Asset> getAssetById(String id) {
        log.info("Fetching asset {}", id);

        try {
            UUID uuid = UUID.fromString(id);
            return findUC.execute(uuid)
                    .map(this::toGeneratedDto)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> {
                        log.warn("Asset {} not found", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found");
                    });
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        } catch (ResponseStatusException e) {
            throw e; // Re-throw ResponseStatusException as is
        } catch (Exception e) {
            log.error("Unexpected error fetching asset {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch asset");
        }
    }

    // Implementar cualquier método adicional que pueda haberse generado
    // Si aparece algún otro método en la interfaz, implementarlo aquí

    // ===== MÉTODOS HELPER PRIVADOS =====

    private void validateSearchParameters(String filename, String filetype) {
        if (filename != null && filename.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename must not be empty");
        }
        if (filetype != null && filetype.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filetype must not be empty");
        }
    }

    private Instant parseInstant(String dateString) {
        if (dateString == null) return null;
        try {
            return Instant.parse(dateString).truncatedTo(ChronoUnit.MILLIS);
        } catch (Exception e) {
            log.warn("Invalid date format: {}", dateString);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
        }
    }

    private void validateDateRange(Instant start, Instant end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload date range");
        }
    }

    private SortDirection parseSortDirection(String sortDirection) {
        try {
            return sortDirection != null ?
                    SortDirection.valueOf(sortDirection.toUpperCase()) : SortDirection.DESC;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sort direction must be ASC or DESC");
        }
    }

    /**
     * Convierte un Asset del dominio al DTO generado
     */
    private Asset toGeneratedDto(com.example.assets.domain.model.Asset domainAsset) {
        Asset dto = new Asset();
        dto.setId(domainAsset.id().toString());
        dto.setFilename(domainAsset.filename());
        dto.setContentType(domainAsset.contentType());
        dto.setUrl(domainAsset.url());

        // Conversión de size (de Long a Integer)
        if (domainAsset.size() != null) {
            dto.setSize(domainAsset.size().intValue());
        }

        // Conversión de fecha
        if (domainAsset.uploadDate() != null) {
            dto.setUploadDate(domainAsset.uploadDate().toString());
        }

        // Conversión de status (si el enum se generó correctamente)
        if (domainAsset.status() != null) {
            dto.setStatus(domainAsset.status().name());
        }

        return dto;
    }
}