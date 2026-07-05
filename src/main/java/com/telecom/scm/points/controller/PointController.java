package com.telecom.scm.points.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.points.service.PointLedgerService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
public class PointController {

    private final PointLedgerService pointLedgerService;
    private final PermissionGuard permissionGuard;

    public PointController(PointLedgerService pointLedgerService, PermissionGuard permissionGuard) {
        this.pointLedgerService = pointLedgerService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/api/member/customer/points")
    public ApiResponse<Map<String, Object>> customerPoints(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "customer:point:view");
        return ApiResponse.success(pointLedgerService.customerPoints(user.username()));
    }

    @GetMapping("/api/member/customer/points/records")
    public ApiResponse<Map<String, Object>> customerPointRecords(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        permissionGuard.require(user, "customer:point:view");
        return ApiResponse.success(
                pointLedgerService.customerPointRecords(
                        user.username(), type, startDate, endDate, page, pageSize));
    }

    @GetMapping("/api/member/customer/referral")
    public ApiResponse<Map<String, Object>> customerReferral(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "customer:referral:view");
        return ApiResponse.success(pointLedgerService.customerReferral(user.username()));
    }
}
