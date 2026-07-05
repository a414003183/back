package com.telecom.scm.file.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.file.dto.response.FileUploadResponse;
import com.telecom.scm.file.entity.FileStorageEntity;

/** 文件存储领域对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileStorageConvert {

    FileStorageConvert INSTANCE = Mappers.getMapper(FileStorageConvert.class);

    /**
     * 将文件存储实体转换为上传响应。
     *
     * <p>注意：实体中的 Long 需要显式映射到 DTO 的 String，fileSize 需要映射到 primitive long。
     */
    @Mapping(target = "id", expression = "java(String.valueOf(entity.getId()))")
    @Mapping(
            target = "fileSize",
            expression =
                    "java(entity.getFileSize() == null ? 0L : entity.getFileSize().longValue())")
    @Mapping(target = "downloadUrl", expression = "java(\"/api/files/\" + entity.getId())")
    FileUploadResponse toFileUploadResponse(FileStorageEntity entity);
}
