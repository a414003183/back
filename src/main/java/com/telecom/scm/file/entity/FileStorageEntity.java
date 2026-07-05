package com.telecom.scm.file.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 文件存储记录实体，用于写入 file_storage 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_storage")
public class FileStorageEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务ID")
    private Long bizId;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "存储文件名")
    private String storageName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件扩展名")
    private String fileExt;

    @Schema(description = "内容类型")
    private String contentType;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "上传人ID")
    private Long uploaderId;
}
