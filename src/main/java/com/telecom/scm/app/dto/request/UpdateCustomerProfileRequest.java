package com.telecom.scm.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "更新客户资料请求")
public record UpdateCustomerProfileRequest(
        @Schema(description = "联系人") String contactName,
        @Schema(description = "联系人电话") String contactPhone) {}
