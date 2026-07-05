package com.telecom.scm.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文件上传响应")
public record FileUploadResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "Name") String originalName,
        @Schema(description = "Type") String contentType,
        @Schema(description = "文件大小") long fileSize,
        @Schema(description = "Url") String downloadUrl) {}
