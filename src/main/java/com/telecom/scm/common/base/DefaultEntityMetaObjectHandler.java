package com.telecom.scm.common.base;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * MyBatis-Plus 字段自动填充处理器。
 *
 * <p>配合 {@link BaseEntity} 中的 {@code @TableField(fill = ...)} 实现：
 *
 * <ul>
 *   <li>插入时自动填充：createdTime、updatedTime、deleted、version、createdBy
 *   <li>更新时自动填充：updatedTime、updatedBy
 * </ul>
 */
@Component
public class DefaultEntityMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createdTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "deleted", Integer.class, 0);
        strictInsertFill(metaObject, "version", Integer.class, 0);

        Long userId = CurrentUserHolder.getUserId();
        if (userId != null) {
            strictInsertFill(metaObject, "createdBy", Long.class, userId);
            strictInsertFill(metaObject, "updatedBy", Long.class, userId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());

        Long userId = CurrentUserHolder.getUserId();
        if (userId != null) {
            strictUpdateFill(metaObject, "updatedBy", Long.class, userId);
        }
    }
}
