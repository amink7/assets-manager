package com.example.assets.web.api.generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an asset with its metadata and processing status
 */

@Schema(name = "Asset", description = "Represents an asset with its metadata and processing status")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-26T02:03:28.884163600+02:00[Europe/Madrid]", comments = "Generator version: 7.10.0")
public class Asset {

  private UUID id;

  private String filename;

  private String contentType;

  private String url = null;

  private Long size = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime uploadDate = null;

  /**
   * Current processing status of the asset
   */
  public enum StatusEnum {
    PENDING("PENDING"),
    
    PROCESSING("PROCESSING"),
    
    PUBLISHED("PUBLISHED"),
    
    FAILED("FAILED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  public Asset id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique file identifier
   * @return id
   */
  @Valid 
  @Schema(name = "id", example = "550e8400-e29b-41d4-a716-446655440000", description = "Unique file identifier", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Asset filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Name of the asset file
   * @return filename
   */
  
  @Schema(name = "filename", example = "profile-picture.jpg", description = "Name of the asset file", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filename")
  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Asset contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * MIME type of the file
   * @return contentType
   */
  
  @Schema(name = "contentType", example = "image/jpeg", description = "MIME type of the file", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contentType")
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Asset url(String url) {
    this.url = url;
    return this;
  }

  /**
   * The URL of the uploaded/published asset (null if still processing)
   * @return url
   */
  
  @Schema(name = "url", example = "file:///app/uploads/550e8400-e29b-41d4-a716-446655440000.jpg", description = "The URL of the uploaded/published asset (null if still processing)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Asset size(Long size) {
    this.size = size;
    return this;
  }

  /**
   * The actual file size in bytes (null if still processing)
   * @return size
   */
  
  @Schema(name = "size", example = "2048", description = "The actual file size in bytes (null if still processing)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("size")
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Asset uploadDate(OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
    return this;
  }

  /**
   * The date & time the file was uploaded and published (null if still processing)
   * @return uploadDate
   */
  @Valid 
  @Schema(name = "uploadDate", example = "2025-08-21T10:30Z", description = "The date & time the file was uploaded and published (null if still processing)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uploadDate")
  public OffsetDateTime getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
  }

  public Asset status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Current processing status of the asset
   * @return status
   */
  
  @Schema(name = "status", example = "PUBLISHED", description = "Current processing status of the asset", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Asset asset = (Asset) o;
    return Objects.equals(this.id, asset.id) &&
        Objects.equals(this.filename, asset.filename) &&
        Objects.equals(this.contentType, asset.contentType) &&
        Objects.equals(this.url, asset.url) &&
        Objects.equals(this.size, asset.size) &&
        Objects.equals(this.uploadDate, asset.uploadDate) &&
        Objects.equals(this.status, asset.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, filename, contentType, url, size, uploadDate, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Asset {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    uploadDate: ").append(toIndentedString(uploadDate)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

