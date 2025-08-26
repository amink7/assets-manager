package com.example.assets.web;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.model.AssetStatus;
import com.example.assets.domain.model.SortDirection;
import com.example.assets.domain.usecase.SearchAssetsUseCase;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.domain.usecase.FindAssetUseCase;
import com.example.assets.web.dto.AssetFileUploadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssetOUTController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UploadAssetUseCase uploadUC;

    @MockitoBean
    SearchAssetsUseCase searchUC;

    @MockitoBean
    FindAssetUseCase findUC;

    @Test
    void upload_shouldReturnAcceptedWithId() throws Exception {
        UUID id = UUID.randomUUID();
        when(uploadUC.execute(anyString(), anyString(), any())).thenReturn(id);

        AssetFileUploadRequest req = new AssetFileUploadRequest();
        req.setFilename("file.txt");
        req.setContentType("text/plain");
        req.setEncodedFile(Base64.getEncoder().encodeToString("data".getBytes()));

        mockMvc.perform(post("/api/mgmt/1/assets/actions/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void search_shouldReturnAssets() throws Exception {
        Asset asset = new Asset(UUID.randomUUID(), "file.txt", "text/plain", "url", 4L, Instant.EPOCH, null);
        when(searchUC.execute(any(), any(), any(), any(), any())).thenReturn(List.of(asset));

        Instant start = Instant.parse("2020-01-01T00:00:00Z");
        Instant end = Instant.parse("2020-01-31T23:59:59Z");

        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateStart", start.toString())
                        .param("uploadDateEnd", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(asset.id().toString()))
                .andExpect(jsonPath("$[0].filename").value("file.txt"));
    }

    @Test
    void search_shouldTruncateAndForwardQueryParameters() throws Exception {
        Instant start = Instant.parse("2020-01-01T00:00:00.123456Z");
        Instant end = Instant.parse("2020-01-31T23:59:59.987654Z");
        when(searchUC.execute(any(), any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateStart", start.toString())
                        .param("uploadDateEnd", end.toString())
                        .param("filename", "file.*")
                        .param("filetype", "text/plain")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk());

        verify(searchUC).execute(
                start.truncatedTo(ChronoUnit.MILLIS),
                end.truncatedTo(ChronoUnit.MILLIS),
                "file.*",
                "text/plain",
                SortDirection.DESC);
    }

    @Test
    void search_shouldAllowMissingEndDate() throws Exception {
        Instant start = Instant.parse("2020-01-01T00:00:00.123456Z");
        when(searchUC.execute(any(), any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateStart", start.toString()))
                .andExpect(status().isOk());

        verify(searchUC).execute(
                start.truncatedTo(ChronoUnit.MILLIS),
                null,
                null,
                null,
                SortDirection.DESC);
    }

    @Test
    void search_shouldAllowMissingStartDate() throws Exception {
        Instant end = Instant.parse("2020-01-31T23:59:59.987654Z");
        when(searchUC.execute(any(), any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateEnd", end.toString()))
                .andExpect(status().isOk());

        verify(searchUC).execute(
                null,
                end.truncatedTo(ChronoUnit.MILLIS),
                null,
                null,
                SortDirection.DESC);
    }

    @Test
    void search_shouldReturnBadRequestForInvalidSortDirection() throws Exception {
        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("sortDirection", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Sort direction must be ASC or DESC"));
    }

    @Test
    void search_shouldReturnBadRequestForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateStart", "2024-13-01")
                        .param("uploadDateEnd", "2024-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Fecha mal formateada"));
    }

    @Test
    void search_shouldReturnBadRequestForEmptyFilename() throws Exception {
        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("filename", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_shouldReturnBadRequestForEmptyFiletype() throws Exception {
        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("filetype", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_shouldReturnBadRequestForInvalidDateRange() throws Exception {
        Instant start = Instant.parse("2020-02-01T00:00:00Z");
        Instant end = Instant.parse("2020-01-01T00:00:00Z");

        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateStart", start.toString())
                        .param("uploadDateEnd", end.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid upload date range"));
    }


    @Test
    void getById_shouldReturnAsset() throws Exception {
        UUID id = UUID.randomUUID();
        Asset asset = new Asset(id, "file.txt", "text/plain", "url", 4L, Instant.EPOCH, AssetStatus.PUBLISHED);
        when(findUC.execute(id)).thenReturn(java.util.Optional.of(asset));

        mockMvc.perform(get("/api/mgmt/1/assets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.url").value("url"));
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(findUC.execute(id)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/mgmt/1/assets/" + id))
                .andExpect(status().isNotFound());
    }
}