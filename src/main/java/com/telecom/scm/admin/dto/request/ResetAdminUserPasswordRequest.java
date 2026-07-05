package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "重置管理员用户密码请求")
public record ResetAdminUserPasswordRequest(
        @NotBlank(message = "password is required") @Schema(description = "密码") String password) {}
