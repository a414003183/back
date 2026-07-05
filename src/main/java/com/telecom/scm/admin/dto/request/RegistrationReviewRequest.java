package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registration审核请求")
public record RegistrationReviewRequest(
        @NotBlank(message = "action is required") @Schema(description = "action") String action) {}
