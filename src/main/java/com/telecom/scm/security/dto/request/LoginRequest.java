package com.telecom.scm.security.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录请求")
public record LoginRequest(
        @NotBlank(message = "username is required") @Schema(description = "用户名") String username,
        @NotBlank(message = "password is required") @Schema(description = "密码") String password) {}
