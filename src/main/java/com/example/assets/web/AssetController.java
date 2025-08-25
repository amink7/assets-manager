package com.example.assets.web;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.usecase.SearchAssetsUseCase;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.domain.usecase.FindAssetUseCase;
import com.example.assets.web.dto.AssetDto;
import com.example.assets.web.dto.AssetFileUploadRequest;
import com.example.assets.web.dto.AssetFileUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * REST controller for managing assets.
 */
@RestController
@RequestMapping(path = "/api/mgmt/1/assets", produces = "application/json")
@Tag(name = "Assets", description = "Assets management (async upload and search)")
public class AssetController {

    private static final Logger log =
            LoggerFactory.getLogger(AssetController.class);

    private final UploadAssetUseCase uploadUC;
    private final SearchAssetsUseCase searchUC;
    private final FindAssetUseCase findUC;

    public AssetController(UploadAssetUseCase uploadUC, SearchAssetsUseCase searchUC, FindAssetUseCase findUC) {
        this.uploadUC = uploadUC;
        this.searchUC = searchUC;
        this.findUC = findUC;
    }

    /**
     * Uploads a new asset (async publishing).
     */
    @PostMapping(value = "/actions/upload", consumes = "application/json")
    @Operation(
            summary = "Upload a file (asynchronous)",
            description = "Accepts a Base64-encoded file. Returns 202 Accepted with the generated ID and continues publishing in the background.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "File upload request payload",
                    content = @Content(schema = @Schema(implementation = AssetFileUploadRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "Accepted; background processing started",
                            content = @Content(schema = @Schema(implementation = AssetFileUploadResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
                    @ApiResponse(responseCode = "415", description = "Unsupported media type", content = @Content)
            }
    )
    public ResponseEntity<AssetFileUploadResponse> upload(
            @Valid @RequestBody AssetFileUploadRequest req
    ) {
        log.info("Uploading {}", req.getFilename());
        byte[] data = Base64.getDecoder().decode(req.getEncodedFile());
        var id = uploadUC.execute(req.getFilename(), req.getContentType(), data);
        log.info("Upload accepted: {}", id);
        return ResponseEntity.accepted().body(new AssetFileUploadResponse(id));
    }

    /**
     * Searches for assets with optional filters.
     */
    @GetMapping("/")
    @Operation(
            summary = "Search assets",
            description = "Returns a list of assets filterable by upload date range, filename pattern ('*' wildcard), and MIME type. Sorted by upload date.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of assets",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AssetDto.class)))
                    )
            }
    )
    public List<AssetDto> search(
            @Parameter(description = "Start of upload date range (UTC, ISO-8601)", example = "2025-08-21T00:00:00Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant uploadDateStart,

            @Parameter(description = "End of upload date range (UTC, ISO-8601)", example = "2025-08-21T23:59:59Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant uploadDateEnd,

            @Parameter(description = "Filename pattern (use '*' as wildcard)", example = "doc*")
            @RequestParam(required = false) String filename,

            @Parameter(description = "MIME type", example = "application/pdf")
            @RequestParam(required = false) String filetype,

            @Parameter(
                    description = "Sort direction by upload date",
                    schema = @Schema(allowableValues = {"ASC", "DESC"}, example = "DESC")
            )
            @RequestParam(required = false, defaultValue = "DESC") SortDirection sortDirection
    ) {
        log.info("Searching assets with uploadDateStart={}, uploadDateEnd={}, filename={}, filetype={}, sortDirection={}",
                uploadDateStart, uploadDateEnd, filename, filetype, sortDirection);
        if (sortDirection == null) {
            log.warn("Invalid sort direction: null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sort direction must be ASC or DESC");
        }
        if (filename != null && filename.isBlank()) {
            log.warn("Filename must not be empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename must not be empty");
        }
        if (filetype != null && filetype.isBlank()) {
            log.warn("Filetype must not be empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filetype must not be empty");
        }
        if (uploadDateStart != null && uploadDateEnd != null && uploadDateStart.isAfter(uploadDateEnd)) {
            log.warn("Invalid upload date range: {} to {}", uploadDateStart, uploadDateEnd);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload date range");
        }

        Instant start = uploadDateStart == null ? null : uploadDateStart.truncatedTo(ChronoUnit.MILLIS);
        Instant end = uploadDateEnd == null ? null : uploadDateEnd.truncatedTo(ChronoUnit.MILLIS);

        boolean asc = switch (sortDirection) {
            case ASC -> true;
            case DESC -> false;
            default -> {
                log.warn("Unsupported sort direction: {}", sortDirection);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Sort direction must be ASC or DESC");
            }
        };
        return searchUC.execute(start, end, filename, filetype, asc)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single asset by its ID.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get asset by ID",
            description = "Returns the asset status and URL if found",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asset found", content = @Content(schema = @Schema(implementation = AssetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Asset not found", content = @Content)
            }
    )
    public AssetDto getById(@PathVariable UUID id) {
        log.info("Fetching asset {}", id);
        return findUC.execute(id)
                .map(this::toDto)
                .orElseThrow(() -> {
                    log.warn("Asset {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
    }


    private AssetDto toDto(Asset asset) {
        return AssetDto.builder()
                .id(asset.id())
                .filename(asset.filename())
                .contentType(asset.contentType())
                .url(asset.url())
                .size(asset.size())
                .uploadDate(asset.uploadDate())
                .status(asset.status())
                .build();
    }
}
