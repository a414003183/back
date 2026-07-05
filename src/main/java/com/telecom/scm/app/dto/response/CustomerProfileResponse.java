package com.telecom.scm.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "客户资料响应")
public record CustomerProfileResponse(
        @Schema(description = "客户 ID") Long customerId,
        @Schema(description = "公司名称") String companyName,
        @Schema(description = "联系人") String contactName,
        @Schema(description = "联系人电话") String contactPhone,
        @Schema(description = "Level") String memberLevel,
        @Schema(description = "用户名") String username,
        @Schema(description = "默认地址") CustomerAddressResponse defaultAddress) {}
