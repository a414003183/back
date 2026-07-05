package com.telecom.scm.member.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.member.dto.response.MetricCardResponse;
import com.telecom.scm.member.service.MemberDashboardService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member")
public class MemberDashboardController {

    private final MemberDashboardService memberDashboardService;
    private final PermissionGuard permissionGuard;

    public MemberDashboardController(
            MemberDashboardService memberDashboardService, PermissionGuard permissionGuard) {
        this.memberDashboardService = memberDashboardService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/customer/dashboard")
    public ApiResponse<List<MetricCardResponse>> customerDashboard(
            @CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "customer:dashboard:view");
        return ApiResponse.success(memberDashboardService.customerMetrics(user.username()));
    }

    @GetMapping("/merchant/dashboard")
    public ApiResponse<List<MetricCardResponse>> merchantDashboard(
            @CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "merchant:dashboard:view");
        return ApiResponse.success(memberDashboardService.merchantMetrics(user.username()));
    }

    @GetMapping("/supplier/dashboard")
    public ApiResponse<List<MetricCardResponse>> supplierDashboard(
            @CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "supplier:dashboard:view");
        return ApiResponse.success(memberDashboardService.supplierMetrics(user.username()));
    }
}
