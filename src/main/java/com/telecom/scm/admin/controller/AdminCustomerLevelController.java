package com.telecom.scm.admin.controller;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.admin.dto.request.SaveCustomerLevelConfigRequest;
import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.pricing.mapper.row.CustomerLevelConfigRow;
import com.telecom.scm.pricing.service.CustomerLevelService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
public class AdminCustomerLevelController {

    private final CustomerLevelService customerLevelService;
    private final PermissionGuard permissionGuard;

    public AdminCustomerLevelController(
            CustomerLevelService customerLevelService, PermissionGuard permissionGuard) {
        this.customerLevelService = customerLevelService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/api/admin/customer-levels")
    public ApiResponse<PageResult<CustomerLevelConfigRow>> customerLevels(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:customer-level:view");
        return ApiResponse.success(customerLevelService.configs(page, pageSize));
    }

    @PostMapping("/api/admin/customer-levels")
    @OperationAudit(module = "Admin", businessType = "SAVE_CUSTOMER_LEVEL_CONFIG")
    public ApiResponse<CustomerLevelConfigRow> saveCustomerLevel(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveCustomerLevelConfigRequest request) {
        permissionGuard.require(user, "admin:customer-level:edit");
        return ApiResponse.success(customerLevelService.saveConfig(user.userId(), request));
    }

    @PutMapping("/api/admin/customer-levels/{levelCode}/status")
    @OperationAudit(module = "Admin", businessType = "UPDATE_CUSTOMER_LEVEL_STATUS")
    public ApiResponse<CustomerLevelConfigRow> updateCustomerLevelStatus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable String levelCode,
            @RequestBody Map<String, String> request) {
        permissionGuard.require(user, "admin:customer-level:edit");
        AccountStatusEnum status = AccountStatusEnum.valueOf(request.get("status"));
        return ApiResponse.success(customerLevelService.updateStatus(levelCode, status));
    }
}
