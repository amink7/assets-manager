package com.example.assets.web;

import com.example.assets.web.dto.generated.Asset;
import com.example.assets.web.api.generated.AssetApi;
import com.example.assets.web.dto.generated.AssetFileUploadRequest;
import com.example.assets.web.dto.generated.AssetFileUploadResponse;
import com.example.assets.domain.usecase.SearchAssetsUseCase;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.domain.usecase.FindAssetUseCase;
import com.example.assets.domain.model.SortDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${openapi.assetManager.base-path:}")
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
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching asset {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch asset");
        }
    }

    @Override
    public ResponseEntity<List<Asset>> getAssetsByFilter(
            OffsetDateTime uploadDateStart,
            OffsetDateTime uploadDateEnd,
            String filename,
            String filetype,
            String sortDirection
    ) {
        log.info("Searching assets with uploadDateStart={}, uploadDateEnd={}, filename={}, filetype={}, sortDirection={}",
                uploadDateStart, uploadDateEnd, filename, filetype, sortDirection);

        try {
            validateSearchParameters(filename, filetype);

            Instant start = uploadDateStart != null ? uploadDateStart.toInstant().truncatedTo(ChronoUnit.MILLIS) : null;
            Instant end   = uploadDateEnd   != null ? uploadDateEnd.toInstant().truncatedTo(ChronoUnit.MILLIS)   : null;
            validateDateRange(start, end);

            SortDirection direction = parseSortDirection(sortDirection);

            List<Asset> assets = searchUC.execute(start, end, filename, filetype, direction)
                    .stream()
                    .map(this::toGeneratedDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(assets);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Search failed");
        }
    }

    @Override
    public ResponseEntity<AssetFileUploadResponse> uploadAssetFile(AssetFileUploadRequest req) {
        log.info("Uploading {}", req.getFilename());

        try {
            byte[] data = Base64.getDecoder().decode(req.getEncodedFile());
            UUID id = uploadUC.execute(req.getFilename(), req.getContentType(), data);

            log.info("Upload accepted: {}", id);

            AssetFileUploadResponse response = new AssetFileUploadResponse(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid Base64 encoding in upload request", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Base64 encoding");
        } catch (Exception e) {
            log.error("Unexpected error during upload", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }
    }

    // ===== MÉTODOS HELPER PRIVADOS =====

    private void validateSearchParameters(String filename, String filetype) {
        if (filename != null && filename.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename must not be empty");
        }
        if (filetype != null && filetype.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filetype must not be empty");
        }
    }

    private void validateDateRange(Instant start, Instant end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload date range");
        }
    }

    private SortDirection parseSortDirection(String sortDirection) {
        try {
            return sortDirection != null
                    ? SortDirection.valueOf(sortDirection.toUpperCase())
                    : SortDirection.DESC;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sort direction must be ASC or DESC");
        }
    }

    private Asset toGeneratedDto(com.example.assets.domain.model.Asset domainAsset) {
        Asset dto = new Asset();
        dto.setId(domainAsset.id());
        dto.setFilename(domainAsset.filename());
        dto.setContentType(domainAsset.contentType());
        dto.setUrl(domainAsset.url());
        dto.setSize(domainAsset.size());
        if (domainAsset.uploadDate() != null) {
            dto.setUploadDate(domainAsset.uploadDate().atOffset(ZoneOffset.UTC));
        }
        if (domainAsset.status() != null) {
            dto.setStatus(Asset.StatusEnum.valueOf(domainAsset.status().name()));
        }
        return dto;
    }
}
