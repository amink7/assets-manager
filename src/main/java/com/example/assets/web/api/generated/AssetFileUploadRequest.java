package com.example.assets.web.api.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Arrays;
import java.util.Objects;

/**
 * Request payload for uploading an asset file
 */

@Schema(name = "AssetFileUploadRequest", description = "Request payload for uploading an asset file")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-26T02:03:28.884163600+02:00[Europe/Madrid]", comments = "Generator version: 7.10.0")
public class AssetFileUploadRequest {

  private String filename;

  private byte[] encodedFile;

  private String contentType;

  public AssetFileUploadRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AssetFileUploadRequest(String filename, byte[] encodedFile, String contentType) {
    this.filename = filename;
    this.encodedFile = encodedFile;
    this.contentType = contentType;
  }

  public AssetFileUploadRequest filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * The filename associated to the asset being uploaded
   * @return filename
   */
  @NotNull @Size(min = 1, max = 255) 
  @Schema(name = "filename", example = "img.png", description = "The filename associated to the asset being uploaded", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("filename")
  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public AssetFileUploadRequest encodedFile(byte[] encodedFile) {
    this.encodedFile = encodedFile;
    return this;
  }

  /**
   * The actual file content encoded in Base64 format. Maximum size after decoding: 50MB
   * @return encodedFile
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "encodedFile", example = "[B@6fb22ae3", description = "The actual file content encoded in Base64 format. Maximum size after decoding: 50MB", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("encodedFile")
  public byte[] getEncodedFile() {
    return encodedFile;
  }

  public void setEncodedFile(byte[] encodedFile) {
    this.encodedFile = encodedFile;
  }

  public AssetFileUploadRequest contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * The MIME type according to IANA definition
   * @return contentType
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "contentType", example = "image/png", description = "The MIME type according to IANA definition", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contentType")
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssetFileUploadRequest assetFileUploadRequest = (AssetFileUploadRequest) o;
    return Objects.equals(this.filename, assetFileUploadRequest.filename) &&
        Arrays.equals(this.encodedFile, assetFileUploadRequest.encodedFile) &&
        Objects.equals(this.contentType, assetFileUploadRequest.contentType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename, Arrays.hashCode(encodedFile), contentType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssetFileUploadRequest {\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    encodedFile: ").append(toIndentedString(encodedFile)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
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

