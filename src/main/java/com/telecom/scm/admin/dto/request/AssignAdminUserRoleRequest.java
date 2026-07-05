package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "分配管理员用户角色请求")
public record AssignAdminUserRoleRequest(
        @NotNull(message = "roleId is required") @Schema(description = "角色 ID") Long roleId) {}
