package com.telecom.scm.member.controller;

import jakarta.validation.constraints.NotNull;

public record HandleSupplierRelationRequest(
        @NotNull(message = "status is required") String status, String remark) {}
