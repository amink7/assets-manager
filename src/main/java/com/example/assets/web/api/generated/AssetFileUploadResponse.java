package com.example.assets.web.api.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Response after initiating an asset upload
 */

@Schema(name = "AssetFileUploadResponse", description = "Response after initiating an asset upload")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-26T02:03:28.884163600+02:00[Europe/Madrid]", comments = "Generator version: 7.10.0")
public class AssetFileUploadResponse {

  private UUID id;

  public AssetFileUploadResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AssetFileUploadResponse(UUID id) {
    this.id = id;
  }

  public AssetFileUploadResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the uploaded file. Use this ID to check upload status via GET /assets/{id}
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", example = "550e8400-e29b-41d4-a716-446655440000", description = "Unique identifier for the uploaded file. Use this ID to check upload status via GET /assets/{id}", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssetFileUploadResponse assetFileUploadResponse = (AssetFileUploadResponse) o;
    return Objects.equals(this.id, assetFileUploadResponse.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssetFileUploadResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

