package com.telecom.scm.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户Identity选项")
public record UserIdentityOption(
        @Schema(description = "身份类型") String identityType,
        @Schema(description = "显示名称") String displayName,
        @Schema(description = "状态") String status,
        @Schema(description = "Identity") boolean defaultIdentity,
        @Schema(description = "是否启用") boolean active,
        @Schema(description = "路由") String route) {}
