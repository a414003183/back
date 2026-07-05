package com.telecom.scm.admin.dto.request;

import jakarta.validation.constraints.NotNull;

import com.telecom.scm.common.enums.UserStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "更新管理员用户状态请求")
public record UpdateAdminUserStatusRequest(
        @NotNull(message = "status is required") @Schema(description = "状态")
                UserStatusEnum status) {}
