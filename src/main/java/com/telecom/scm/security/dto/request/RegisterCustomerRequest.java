package com.telecom.scm.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登记客户请求")
public record RegisterCustomerRequest(
        @NotBlank(message = "username is required")
                @Size(max = 50, message = "username must be at most 50 characters")
                @Schema(description = "用户名")
                String username,
        @NotBlank(message = "password is required")
                @Size(min = 6, max = 50, message = "password must be between 6 and 50 characters")
                @Schema(description = "密码")
                String password,
        @Size(max = 150, message = "companyName must be at most 150 characters")
                @Schema(description = "公司名称")
                String companyName,
        @NotBlank(message = "contactName is required")
                @Size(max = 50, message = "contactName must be at most 50 characters")
                @Schema(description = "联系人")
                String contactName,
        @NotBlank(message = "contactPhone is required")
                @Size(max = 20, message = "contactPhone must be at most 20 characters")
                @Schema(description = "联系人电话")
                String contactPhone,
        @Email(message = "email is invalid")
                @Size(max = 100, message = "email must be at most 100 characters")
                @Schema(description = "邮箱")
                String email,
        @Schema(description = "邀请码") String inviteCode) {}
