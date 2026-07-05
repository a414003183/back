package com.telecom.scm.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.mapper.row.SupplyStatusRow;
import com.telecom.scm.member.service.SupplierSupplyStatusService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member/supplier")
public class SupplierSupplyStatusController {

    private final SupplierSupplyStatusService supplyStatusService;

    public SupplierSupplyStatusController(SupplierSupplyStatusService supplyStatusService) {
        this.supplyStatusService = supplyStatusService;
    }

    @GetMapping("/supply-status")
    public ApiResponse<PageResult<SupplyStatusRow>> supplyStatus(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(
                supplyStatusService.getSupplyStatus(user.username(), page, pageSize));
    }
}
