package com.telecom.scm.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理员用户响应")
public record AdminUserResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "用户名") String username,
        @Schema(description = "显示名称") String displayName,
        @Schema(description = "角色 ID") String roleId,
        @Schema(description = "Code") String roleCode,
        @Schema(description = "角色") String role,
        @Schema(description = "状态") String status,
        @Schema(description = "电话") String phone,
        @Schema(description = "邮箱") String email,
        @Schema(description = "At") String createdAt) {}
