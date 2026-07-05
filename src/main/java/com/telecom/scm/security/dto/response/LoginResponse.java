package com.telecom.scm.security.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录响应")
public record LoginResponse(
        @Schema(description = "令牌") String token,
        @Schema(description = "Type") String tokenType,
        @Schema(description = "用户名") String username,
        @Schema(description = "角色") String role,
        @Schema(description = "身份类型") String identityType,
        @Schema(description = "显示名称") String displayName,
        @Schema(description = "路由") String route,
        @Schema(description = "权限列表") List<String> permissions,
        @Schema(description = "菜单列表") List<CurrentUserMenuItem> menus,
        @Schema(description = "identities") List<UserIdentityOption> identities) {}
