package com.telecom.scm.file.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.file.dto.response.FileUploadResponse;
import com.telecom.scm.file.service.FileStorageService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.support.CurrentUser;

@RestController
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/api/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OperationAudit(module = "File", businessType = "UPLOAD_FILE")
    public ApiResponse<FileUploadResponse> upload(
            @CurrentUser AuthenticatedUser user,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId) {
        return ApiResponse.success(
                fileStorageService.upload(user.username(), file, bizType, bizId));
    }

    @GetMapping("/api/files/{fileId}")
    public ResponseEntity<byte[]> download(
            @CurrentUser(required = false) AuthenticatedUser user, @PathVariable Long fileId) {
        FileStorageService.DownloadPayload payload = fileStorageService.loadFile(fileId);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            mediaType = MediaType.parseMediaType(payload.contentType());
        } catch (IllegalArgumentException ignored) {
        }

        ContentDisposition contentDisposition =
                isInlineMediaType(mediaType)
                        ? ContentDisposition.inline()
                                .filename(payload.originalName(), StandardCharsets.UTF_8)
                                .build()
                        : ContentDisposition.attachment()
                                .filename(payload.originalName(), StandardCharsets.UTF_8)
                                .build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(payload.content());
    }

    private boolean isInlineMediaType(MediaType mediaType) {
        return mediaType != null && "image".equalsIgnoreCase(mediaType.getType());
    }
}
