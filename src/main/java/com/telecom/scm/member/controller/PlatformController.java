package com.telecom.scm.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.mapper.row.PlatformSupplierRow;
import com.telecom.scm.member.service.SupplyCollaborationService;

@RestController
@RequestMapping("/api/platform")
public class PlatformController {

    private final SupplyCollaborationService supplyCollaborationService;

    public PlatformController(SupplyCollaborationService supplyCollaborationService) {
        this.supplyCollaborationService = supplyCollaborationService;
    }

    @GetMapping("/suppliers")
    public ApiResponse<PageResult<PlatformSupplierRow>> platformSuppliers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(
                supplyCollaborationService.platformSuppliers(keyword, page, pageSize));
    }
}
