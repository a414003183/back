package com.telecom.scm.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单合同响应")
public class OrderContractResponse {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "Name")
    private String originalName;

    @Schema(description = "Type")
    private String contentType;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "Time")
    private String uploadTime;

    @Schema(description = "Url")
    private String downloadUrl;

    public OrderContractResponse() {}

    public OrderContractResponse(
            String id,
            String originalName,
            String contentType,
            Long fileSize,
            String uploadTime,
            String downloadUrl) {
        this.id = id;
        this.originalName = originalName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
