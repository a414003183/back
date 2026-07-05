package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.UserStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "创建管理员用户请求")
public record CreateAdminUserRequest(
        @NotBlank(message = "username is required") @Schema(description = "用户名") String username,
        @NotBlank(message = "password is required") @Schema(description = "密码") String password,
        @NotBlank(message = "displayName is required") @Schema(description = "显示名称")
                String displayName,
        @Schema(description = "电话") String phone,
        @Schema(description = "邮箱") String email,
        @NotBlank(message = "roleCode is required") @Schema(description = "Code") String roleCode,
        @NotNull(message = "status is required") @Schema(description = "状态")
                UserStatusEnum status) {}
