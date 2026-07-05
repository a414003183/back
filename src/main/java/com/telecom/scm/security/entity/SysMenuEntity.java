package com.telecom.scm.security.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** sys_menu 实体。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenuEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "parent id")
    private Long parentId;

    @Schema(description = "menu name")
    private String menuName;

    @Schema(description = "menu type")
    private String menuType;

    @Schema(description = "path")
    private String path;

    @Schema(description = "component")
    private String component;

    @Schema(description = "permission code")
    private String permissionCode;

    @Schema(description = "icon")
    private String icon;

    @Schema(description = "sort no")
    private Integer sortNo;

    @Schema(description = "visible")
    private Integer visible;

    @Schema(description = "状态")
    private AccountStatusEnum status;

    @Schema(description = "operator id")
    private Long operatorId;
}
