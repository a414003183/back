package com.telecom.scm.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.file.entity.FileStorageEntity;
import com.telecom.scm.file.mapper.FileStorageMapper;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {

    @Mock private FileStorageMapper fileStorageMapper;

    @TempDir Path tempDir;

    private FileStorageServiceImpl fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl(fileStorageMapper, tempDir.toString());
    }

    @Test
    @DisplayName("正常上传文件应保存到临时目录并返回上传响应")
    void shouldUploadFileSuccessfully() throws Exception {
        // given
        String username = "user";
        String originalFilename = "document.pdf";
        byte[] content = "test content".getBytes();
        MockMultipartFile file =
                new MockMultipartFile("file", originalFilename, "application/pdf", content);

        when(fileStorageMapper.selectUserIdByUsername(username)).thenReturn(42L);
        doAnswer(
                        invocation -> {
                            FileStorageEntity entity = invocation.getArgument(0);
                            entity.setId(999L);
                            return 1;
                        })
                .when(fileStorageMapper)
                .insertFile(any(FileStorageEntity.class));

        // when
        var response = fileStorageService.upload(username, file, "ORDER_CONTRACT", 123L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("999");
        assertThat(response.originalName()).isEqualTo(originalFilename);
        assertThat(response.contentType()).isEqualTo("application/pdf");
        assertThat(response.fileSize()).isEqualTo(content.length);
        assertThat(response.downloadUrl()).isEqualTo("/api/files/999");

        ArgumentCaptor<FileStorageEntity> captor = ArgumentCaptor.forClass(FileStorageEntity.class);
        verify(fileStorageMapper).insertFile(captor.capture());
        FileStorageEntity saved = captor.getValue();
        assertThat(saved.getBizType()).isEqualTo("ORDER_CONTRACT");
        assertThat(saved.getBizId()).isEqualTo(123L);
        assertThat(saved.getUploaderId()).isEqualTo(42L);
        assertThat(saved.getFileSize()).isEqualTo((long) content.length);

        Path storedPath = Path.of(saved.getFilePath()).toAbsolutePath().normalize();
        assertThat(storedPath).startsWith(tempDir.toAbsolutePath().normalize());
        assertThat(Files.exists(storedPath)).isTrue();
    }

    @Test
    @DisplayName("空文件上传时应抛出 BusinessException")
    void shouldThrowBusinessExceptionWhenUploadingEmptyFile() {
        // given
        MockMultipartFile emptyFile =
                new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        // when / then
        assertThatThrownBy(() -> fileStorageService.upload("user", emptyFile, "TEMP", null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("file is required");
    }

    @Test
    @DisplayName("上传文件名包含路径穿越时，最终存储路径应仍限制在 storageRoot 内")
    void shouldNeutralizePathTraversalInFilename() throws Exception {
        // given
        String username = "user";
        String maliciousFilename = "../etc/passwd";
        byte[] content = "malicious".getBytes();
        MockMultipartFile file =
                new MockMultipartFile("file", maliciousFilename, "text/plain", content);

        when(fileStorageMapper.selectUserIdByUsername(username)).thenReturn(42L);
        doAnswer(
                        invocation -> {
                            FileStorageEntity entity = invocation.getArgument(0);
                            entity.setId(888L);
                            return 1;
                        })
                .when(fileStorageMapper)
                .insertFile(any(FileStorageEntity.class));

        // when
        var response = fileStorageService.upload(username, file, "TEMP", null);

        // then
        assertThat(response).isNotNull();

        ArgumentCaptor<FileStorageEntity> captor = ArgumentCaptor.forClass(FileStorageEntity.class);
        verify(fileStorageMapper).insertFile(captor.capture());
        FileStorageEntity saved = captor.getValue();
        assertThat(saved.getOriginalName()).isEqualTo("passwd");

        Path storedPath = Path.of(saved.getFilePath()).toAbsolutePath().normalize();
        assertThat(storedPath).startsWith(tempDir.toAbsolutePath().normalize());
        assertThat(Files.exists(storedPath)).isTrue();
    }
}
