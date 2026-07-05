package com.telecom.scm.admin.controller;

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

import com.telecom.scm.admin.dto.request.SaveBrandRequest;
import com.telecom.scm.admin.dto.request.SaveCategoryRequest;
import com.telecom.scm.admin.dto.request.StatusUpdateRequest;
import com.telecom.scm.admin.dto.response.MessageResponse;
import com.telecom.scm.admin.mapper.BrandRow;
import com.telecom.scm.admin.mapper.CategoryRow;
import com.telecom.scm.admin.service.AdminProductService;
import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final PermissionGuard permissionGuard;

    public AdminProductController(
            AdminProductService adminProductService, PermissionGuard permissionGuard) {
        this.adminProductService = adminProductService;
        this.permissionGuard = permissionGuard;
    }

    // 品牌管理
    @GetMapping("/brands")
    public ApiResponse<PageResult<BrandRow>> listBrands(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:product:view");
        return ApiResponse.success(adminProductService.listBrands(page, pageSize));
    }

    @PostMapping("/brands")
    @OperationAudit(module = "AdminProduct", businessType = "SAVE_BRAND")
    public ApiResponse<MessageResponse> saveBrand(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody SaveBrandRequest request) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(adminProductService.saveBrand(user.username(), request));
    }

    @PutMapping("/brands/{brandId}")
    @OperationAudit(module = "AdminProduct", businessType = "UPDATE_BRAND")
    public ApiResponse<MessageResponse> updateBrand(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long brandId,
            @Valid @RequestBody SaveBrandRequest request) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(
                adminProductService.updateBrand(user.username(), brandId, request));
    }

    @DeleteMapping("/brands/{brandId}")
    @OperationAudit(module = "AdminProduct", businessType = "DELETE_BRAND")
    public ApiResponse<MessageResponse> deleteBrand(
            @CurrentUser AuthenticatedUser user, @PathVariable Long brandId) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(adminProductService.deleteBrand(brandId));
    }

    @PutMapping("/brands/{brandId}/status")
    @OperationAudit(module = "AdminProduct", businessType = "UPDATE_BRAND_STATUS")
    public ApiResponse<MessageResponse> updateBrandStatus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long brandId,
            @Valid @RequestBody StatusUpdateRequest request) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(
                adminProductService.updateBrandStatus(
                        brandId, AccountStatusEnum.valueOf(request.status())));
    }

    // 分类管理
    @GetMapping("/categories")
    public ApiResponse<PageResult<CategoryRow>> listCategories(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        permissionGuard.require(user, "admin:product:view");
        return ApiResponse.success(adminProductService.listCategories(page, pageSize));
    }

    @PostMapping("/categories")
    @OperationAudit(module = "AdminProduct", businessType = "SAVE_CATEGORY")
    public ApiResponse<MessageResponse> saveCategory(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody SaveCategoryRequest request) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(adminProductService.saveCategory(user.username(), request));
    }

    @PutMapping("/categories/{categoryId}")
    @OperationAudit(module = "AdminProduct", businessType = "UPDATE_CATEGORY")
    public ApiResponse<MessageResponse> updateCategory(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long categoryId,
            @Valid @RequestBody SaveCategoryRequest request) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(
                adminProductService.updateCategory(user.username(), categoryId, request));
    }

    @DeleteMapping("/categories/{categoryId}")
    @OperationAudit(module = "AdminProduct", businessType = "DELETE_CATEGORY")
    public ApiResponse<MessageResponse> deleteCategory(
            @CurrentUser AuthenticatedUser user, @PathVariable Long categoryId) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(adminProductService.deleteCategory(categoryId));
    }

    @PutMapping("/categories/{categoryId}/status")
    @OperationAudit(module = "AdminProduct", businessType = "UPDATE_CATEGORY_STATUS")
    public ApiResponse<MessageResponse> updateCategoryStatus(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long categoryId,
            @Valid @RequestBody StatusUpdateRequest request) {
        permissionGuard.require(user, "admin:product:edit");
        return ApiResponse.success(
                adminProductService.updateCategoryStatus(
                        categoryId, AccountStatusEnum.valueOf(request.status())));
    }
}
