package com.telecom.scm.member.controller;

import jakarta.validation.constraints.NotNull;

public record HandleSupplierAuthorizationRequest(
        @NotNull(message = "status is required") String status, String remark) {}
