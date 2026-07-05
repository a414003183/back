package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "状态更新请求")
public record StatusUpdateRequest(@NotBlank @Schema(description = "状态") String status) {}
