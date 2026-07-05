package com.telecom.scm.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "当前用户菜单项")
public record CurrentUserMenuItem(
        @Schema(description = "ID") Long id,
        @Schema(description = "父级 ID") Long parentId,
        @Schema(description = "Name") String menuName,
        @Schema(description = "路径") String path,
        @Schema(description = "component") String component,
        @Schema(description = "图标") String icon,
        @Schema(description = "Code") String permissionCode,
        @Schema(description = "No") Integer sortNo,
        @Schema(description = "是否可见") boolean visible) {}
