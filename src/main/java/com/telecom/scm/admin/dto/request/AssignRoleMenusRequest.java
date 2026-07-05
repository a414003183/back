package com.telecom.scm.admin.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "分配角色Menus请求")
public record AssignRoleMenusRequest(
        @NotNull(message = "menuIds is required") @Schema(description = "Ids")
                List<Long> menuIds) {}
