package com.example.assets.web;

import com.example.assets.domain.model.Asset;
import com.example.assets.domain.usecase.SearchAssetsUseCase;
import com.example.assets.domain.usecase.UploadAssetUseCase;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssetController.class)
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
        when(searchUC.execute(any(), any(), any(), any(), anyBoolean())).thenReturn(List.of(asset));

        mockMvc.perform(get("/api/mgmt/1/assets/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(asset.id().toString()))
                .andExpect(jsonPath("$[0].filename").value("file.txt"));
    }

    @Test
    void search_shouldForwardQueryParameters() throws Exception {
        Instant start = Instant.parse("2020-01-01T00:00:00Z");
        Instant end = Instant.parse("2020-01-31T23:59:59Z");
        when(searchUC.execute(any(), any(), any(), any(), anyBoolean())).thenReturn(List.of());

        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateStart", start.toString())
                        .param("uploadDateEnd", end.toString())
                        .param("filename", "file.*")
                        .param("filetype", "text/plain")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk());

        verify(searchUC).execute(start, end, "file.*", "text/plain", false);
    }
}