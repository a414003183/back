package com.telecom.scm.admin.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "分配用户Menus请求")
public record AssignUserMenusRequest(
        @Schema(description = "角色 ID") Long roleId,
        @Schema(description = "Ids") List<Long> menuIds) {}
