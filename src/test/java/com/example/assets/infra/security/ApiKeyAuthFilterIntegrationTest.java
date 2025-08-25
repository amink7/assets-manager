package com.example.assets.infra.security;

import com.example.assets.domain.usecase.SearchAssetsUseCase;
import com.example.assets.domain.usecase.UploadAssetUseCase;
import com.example.assets.web.AssetController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetController.class)
@Import({ApiKeyAuthFilter.class, SecurityConfig.class})
@TestPropertySource(properties = "security.api.key=test-key")
class ApiKeyAuthFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UploadAssetUseCase uploadUC;

    @MockitoBean
    private SearchAssetsUseCase searchUC;

    @Test
    void requestWithoutApiKeyIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/mgmt/1/assets/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithValidApiKeyIsAuthorized() throws Exception {
        when(searchUC.execute(any(), any(), any(), any(), anyBoolean())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/mgmt/1/assets/").header("X-API-KEY", "test-key"))
                .andExpect(status().isOk());
    }
}
