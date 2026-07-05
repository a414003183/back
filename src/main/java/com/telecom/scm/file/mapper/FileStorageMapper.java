package com.telecom.scm.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.file.entity.FileStorageEntity;

@Mapper
public interface FileStorageMapper {

    @Select(
            """
        SELECT id
        FROM sys_user
        WHERE deleted = 0
            AND status = 'ENABLED'
            AND username = #{username}
        LIMIT 1
        """)
    Long selectUserIdByUsername(@Param("username") String username);

    @Insert(
            """
        INSERT INTO file_storage (
            biz_type,
            biz_id,
            original_name,
            storage_name,
            file_path,
            file_ext,
            content_type,
            file_size,
            uploader_id
        ) VALUES (
            #{bizType},
            #{bizId},
            #{originalName},
            #{storageName},
            #{filePath},
            #{fileExt},
            #{contentType},
            #{fileSize},
            #{uploaderId}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFile(FileStorageEntity fileInfo);

    @Select(
            """
        SELECT
            id,
            biz_type,
            biz_id,
            original_name,
            storage_name,
            file_path,
            file_ext,
            content_type,
            file_size,
            uploader_id
        FROM file_storage
        WHERE id = #{fileId}
        LIMIT 1
        """)
    FileStorageRow selectById(@Param("fileId") Long fileId);

    @Update(
            """
        UPDATE file_storage
        SET biz_type = #{bizType},
            biz_id = #{bizId}
        WHERE id = #{fileId}
        """)
    int bindFile(
            @Param("fileId") Long fileId,
            @Param("bizType") String bizType,
            @Param("bizId") Long bizId);
}
