package com.telecom.scm.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registration申请响应")
public record RegistrationApplicationResponse(
        @Schema(description = "ID") String id,
        @Schema(description = "用户名") String username,
        @Schema(description = "身份类型") String identityType,
        @Schema(description = "显示名称") String displayName,
        @Schema(description = "电话") String phone,
        @Schema(description = "邮箱") String email,
        @Schema(description = "状态") String status,
        @Schema(description = "At") String createdAt) {}
