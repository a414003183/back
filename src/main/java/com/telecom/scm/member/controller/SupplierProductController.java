package com.telecom.scm.member.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.SaveSupplierProductRequest;
import com.telecom.scm.member.dto.response.SupplierProductOptionsResponse;
import com.telecom.scm.member.mapper.row.SupplierProductRow;
import com.telecom.scm.member.service.SupplierProductService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member/supplier")
public class SupplierProductController {

    private final SupplierProductService supplierProductService;
    private final PermissionGuard permissionGuard;

    public SupplierProductController(
            SupplierProductService supplierProductService, PermissionGuard permissionGuard) {
        this.supplierProductService = supplierProductService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/products")
    public ApiResponse<PageResult<SupplierProductRow>> products(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "supplier:product:view");
        return ApiResponse.success(
                supplierProductService.products(user.username(), page, pageSize));
    }

    @GetMapping("/product-options")
    public ApiResponse<SupplierProductOptionsResponse> options(
            @CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "supplier:product:view");
        return ApiResponse.success(supplierProductService.options());
    }

    @PostMapping("/products")
    @OperationAudit(module = "SupplierProduct", businessType = "CREATE_PRODUCT")
    public ApiResponse<SupplierProductRow> createProduct(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveSupplierProductRequest request) {
        permissionGuard.require(user, "supplier:product:view");
        return ApiResponse.success(supplierProductService.createProduct(user.username(), request));
    }

    @PutMapping("/products/{skuId}")
    @OperationAudit(module = "SupplierProduct", businessType = "UPDATE_PRODUCT")
    public ApiResponse<SupplierProductRow> updateProduct(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long skuId,
            @Valid @RequestBody SaveSupplierProductRequest request) {
        permissionGuard.require(user, "supplier:product:view");
        return ApiResponse.success(
                supplierProductService.updateProduct(user.username(), skuId, request));
    }

    @DeleteMapping("/products/{skuId}")
    @OperationAudit(module = "SupplierProduct", businessType = "DELETE_PRODUCT")
    public ApiResponse<Void> deleteProduct(
            @CurrentUser AuthenticatedUser user, @PathVariable Long skuId) {
        permissionGuard.require(user, "supplier:product:view");
        supplierProductService.deleteProduct(user.username(), skuId);
        return ApiResponse.success(null);
    }
}
