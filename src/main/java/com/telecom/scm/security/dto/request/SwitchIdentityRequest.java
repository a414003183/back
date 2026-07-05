package com.telecom.scm.security.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "切换Identity请求")
public record SwitchIdentityRequest(
        @NotBlank(message = "identityType is required") @Schema(description = "身份类型")
                String identityType) {}
