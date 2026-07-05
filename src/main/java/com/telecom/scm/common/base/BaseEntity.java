package com.telecom.scm.common.base;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通用实体基类。
 *
 * <p>企业级项目通常把所有表都有的公共字段抽象到基类中：
 *
 * <ul>
 *   <li>主键 id
 *   <li>创建人 / 更新人
 *   <li>创建时间 / 更新时间（自动填充）
 *   <li>逻辑删除标志
 *   <li>乐观锁版本号
 *   <li>备注
 * </ul>
 *
 * <p>子类只需要声明业务字段即可，不需要再写 getter/setter。
 */
@Data
public abstract class BaseEntity implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "主键 ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建人 ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @Schema(description = "更新人 ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @Schema(description = "是否删除：0 未删除，1 已删除")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @Schema(description = "乐观锁版本号")
    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @Schema(description = "备注")
    private String remark;
}
