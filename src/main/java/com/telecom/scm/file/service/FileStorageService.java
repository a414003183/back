package com.telecom.scm.file.service;

import org.springframework.web.multipart.MultipartFile;

import com.telecom.scm.file.dto.response.FileUploadResponse;

public interface FileStorageService {

    FileUploadResponse upload(String username, MultipartFile file, String bizType, Long bizId);

    Long requireUserId(String username);

    void bindFileIfPresent(Long fileId, String bizType, Long bizId, Long uploaderId);

    DownloadPayload loadFile(Long fileId);

    record DownloadPayload(String originalName, String contentType, byte[] content) {}
}
