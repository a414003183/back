package com.telecom.scm.file.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.file.convert.FileStorageConvert;
import com.telecom.scm.file.dto.response.FileUploadResponse;
import com.telecom.scm.file.entity.FileStorageEntity;
import com.telecom.scm.file.mapper.FileStorageMapper;
import com.telecom.scm.file.mapper.FileStorageRow;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

    private final FileStorageMapper fileStorageMapper;
    private final Path storageRoot;

    public FileStorageServiceImpl(
            FileStorageMapper fileStorageMapper,
            @Value("${app.file.storage-dir:../uploads}") String storageDir) {
        this.fileStorageMapper = fileStorageMapper;
        this.storageRoot = Paths.get(storageDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "failed to initialize file storage directory", exception);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse upload(
            String username, MultipartFile file, String bizType, Long bizId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(400, "file size cannot exceed 20MB");
        }

        Long uploaderId = fileStorageMapper.selectUserIdByUsername(username);
        if (uploaderId == null) {
            throw new BusinessException(403, "current user cannot upload files");
        }

        String originalName = extractOriginalName(file.getOriginalFilename());
        String fileExt = StringUtils.getFilenameExtension(originalName);
        String storageName =
                UUID.randomUUID().toString().replace("-", "")
                        + (StringUtils.hasText(fileExt)
                                ? "." + fileExt.toLowerCase(Locale.ROOT)
                                : "");
        Path targetPath = storageRoot.resolve(storageName).normalize();
        if (!targetPath.startsWith(storageRoot)) {
            throw new BusinessException(400, "invalid target file path");
        }

        try {
            file.transferTo(targetPath.toFile());
        } catch (IOException exception) {
            throw new BusinessException(500, "failed to save uploaded file");
        }

        FileStorageEntity fileInfo = new FileStorageEntity();
        fileInfo.setBizType(normalizeBizType(bizType));
        fileInfo.setBizId(bizId);
        fileInfo.setOriginalName(originalName);
        fileInfo.setStorageName(storageName);
        fileInfo.setFilePath(targetPath.toString());
        fileInfo.setFileExt(fileExt);
        fileInfo.setContentType(
                StringUtils.hasText(file.getContentType())
                        ? file.getContentType()
                        : "application/octet-stream");
        fileInfo.setFileSize(file.getSize());
        fileInfo.setUploaderId(uploaderId);
        fileStorageMapper.insertFile(fileInfo);

        return FileStorageConvert.INSTANCE.toFileUploadResponse(fileInfo);
    }

    @Override
    public Long requireUserId(String username) {
        Long userId = fileStorageMapper.selectUserIdByUsername(username);
        if (userId == null) {
            throw new BusinessException(403, "current user cannot operate files");
        }
        return userId;
    }

    @Override
    public void bindFileIfPresent(Long fileId, String bizType, Long bizId, Long uploaderId) {
        if (fileId == null) {
            return;
        }

        FileStorageRow file = requireFile(fileId);
        if (uploaderId != null
                && file.getUploaderId() != null
                && !uploaderId.equals(file.getUploaderId())) {
            throw new BusinessException(403, "current user cannot bind this file");
        }
        fileStorageMapper.bindFile(fileId, normalizeBizType(bizType), bizId);
    }

    @Override
    public DownloadPayload loadFile(Long fileId) {
        FileStorageRow file = requireFile(fileId);
        Path filePath = resolveFilePath(file);
        try {
            if (!Files.exists(filePath)) {
                throw new BusinessException(404, "file does not exist");
            }
            return new DownloadPayload(
                    file.getOriginalName(),
                    StringUtils.hasText(file.getContentType())
                            ? file.getContentType()
                            : "application/octet-stream",
                    Files.readAllBytes(filePath));
        } catch (IOException exception) {
            throw new BusinessException(500, "failed to read file");
        }
    }

    private Path resolveFilePath(FileStorageRow file) {
        Path persistedPath = Paths.get(file.getFilePath()).toAbsolutePath().normalize();
        if (Files.exists(persistedPath)) {
            return persistedPath;
        }
        Path fallbackPath = storageRoot.resolve(file.getStorageName()).normalize();
        if (!fallbackPath.startsWith(storageRoot)) {
            throw new BusinessException(400, "invalid target file path");
        }
        return fallbackPath;
    }

    private FileStorageRow requireFile(Long fileId) {
        FileStorageRow file = fileStorageMapper.selectById(fileId);
        if (file == null) {
            throw new BusinessException(404, "file record not found");
        }
        return file;
    }

    private String extractOriginalName(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "attachment.bin";
        }

        String normalized = originalFilename.replace("\\", "/");
        int slashIndex = normalized.lastIndexOf('/');
        String fileName = slashIndex >= 0 ? normalized.substring(slashIndex + 1) : normalized;
        return StringUtils.hasText(fileName) ? fileName : "attachment.bin";
    }

    private String normalizeBizType(String bizType) {
        return StringUtils.hasText(bizType) ? bizType.trim().toUpperCase(Locale.ROOT) : "TEMP";
    }
}
