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
import com.telecom.scm.member.dto.request.SaveMerchantGoodsRequest;
import com.telecom.scm.member.dto.request.SaveMerchantProductRequest;
import com.telecom.scm.member.dto.response.MerchantGoodsOptionsResponse;
import com.telecom.scm.member.mapper.row.MerchantGoodsRow;
import com.telecom.scm.member.service.MerchantGoodsService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/member/merchant/goods")
public class MerchantGoodsController {

    private final MerchantGoodsService merchantGoodsService;
    private final PermissionGuard permissionGuard;

    public MerchantGoodsController(
            MerchantGoodsService merchantGoodsService, PermissionGuard permissionGuard) {
        this.merchantGoodsService = merchantGoodsService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping
    public ApiResponse<PageResult<MerchantGoodsRow>> goods(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        permissionGuard.require(user, "merchant:goods:view");
        return ApiResponse.success(
                merchantGoodsService.goods(
                        user.username(), user.hasPermission("merchant:cost:view"), page, pageSize));
    }

    @GetMapping("/options")
    public ApiResponse<MerchantGoodsOptionsResponse> options(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "merchant:goods:view");
        return ApiResponse.success(merchantGoodsService.options());
    }

    @PostMapping
    @OperationAudit(module = "MerchantGoods", businessType = "CREATE_MERCHANT_PRODUCT")
    public ApiResponse<MerchantGoodsRow> createProduct(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveMerchantProductRequest request) {
        permissionGuard.require(user, "merchant:goods:edit");
        return ApiResponse.success(merchantGoodsService.createProduct(user.username(), request));
    }

    @GetMapping("/{merchantGoodsId}")
    public ApiResponse<MerchantGoodsRow> getGood(
            @CurrentUser AuthenticatedUser user, @PathVariable Long merchantGoodsId) {
        permissionGuard.require(user, "merchant:goods:view");
        return ApiResponse.success(merchantGoodsService.getGood(user.username(), merchantGoodsId));
    }

    @PutMapping("/{merchantGoodsId}")
    @OperationAudit(module = "MerchantGoods", businessType = "UPDATE_MERCHANT_GOODS")
    public ApiResponse<MerchantGoodsRow> updateGood(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long merchantGoodsId,
            @Valid @RequestBody SaveMerchantGoodsRequest request) {
        permissionGuard.require(user, "merchant:goods:edit");
        return ApiResponse.success(
                merchantGoodsService.updateGood(user.username(), merchantGoodsId, request));
    }

    @DeleteMapping("/{merchantGoodsId}")
    @OperationAudit(module = "MerchantGoods", businessType = "DELETE_MERCHANT_GOODS")
    public ApiResponse<Void> deleteGood(
            @CurrentUser AuthenticatedUser user, @PathVariable Long merchantGoodsId) {
        permissionGuard.require(user, "merchant:goods:edit");
        merchantGoodsService.deleteGood(user.username(), merchantGoodsId);
        return ApiResponse.success(null);
    }
}
