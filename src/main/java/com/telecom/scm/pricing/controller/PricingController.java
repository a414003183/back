package com.telecom.scm.pricing.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.pricing.dto.request.SaveCustomerPriceRuleRequest;
import com.telecom.scm.pricing.dto.request.SaveGoodsAuthRuleRequest;
import com.telecom.scm.pricing.dto.request.SaveLevelDiscountRuleRequest;
import com.telecom.scm.pricing.dto.response.PricingOptionsResponse;
import com.telecom.scm.pricing.dto.response.PricingSaveResponse;
import com.telecom.scm.pricing.mapper.row.CustomerPriceRuleRow;
import com.telecom.scm.pricing.mapper.row.GoodsAuthRuleRow;
import com.telecom.scm.pricing.mapper.row.LevelDiscountRuleRow;
import com.telecom.scm.pricing.service.PricingService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
public class PricingController {

    private final PricingService pricingService;
    private final PermissionGuard permissionGuard;

    public PricingController(PricingService pricingService, PermissionGuard permissionGuard) {
        this.pricingService = pricingService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/api/member/merchant/goods/auth")
    public ApiResponse<List<GoodsAuthRuleRow>> goodsAuthRules(@CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "merchant:pricing:level:view");
        return ApiResponse.success(pricingService.goodsAuthRules(user.username()));
    }

    @PostMapping("/api/member/merchant/goods/auth")
    @OperationAudit(module = "Pricing", businessType = "SAVE_GOODS_AUTH_RULE")
    public ApiResponse<PricingSaveResponse> saveGoodsAuthRule(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveGoodsAuthRuleRequest request) {
        permissionGuard.require(user, "merchant:pricing:level:view");
        return ApiResponse.success(pricingService.saveGoodsAuthRule(user.username(), request));
    }

    @GetMapping("/api/member/merchant/pricing/level")
    public ApiResponse<List<LevelDiscountRuleRow>> levelDiscountRules(
            @CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "merchant:pricing:level:view");
        return ApiResponse.success(pricingService.levelDiscountRules(user.username()));
    }

    @PostMapping("/api/member/merchant/pricing/level")
    @OperationAudit(module = "Pricing", businessType = "SAVE_LEVEL_DISCOUNT_RULE")
    public ApiResponse<PricingSaveResponse> saveLevelDiscountRule(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveLevelDiscountRuleRequest request) {
        permissionGuard.require(user, "merchant:pricing:level:view");
        return ApiResponse.success(pricingService.saveLevelDiscountRule(user.username(), request));
    }

    @GetMapping("/api/member/merchant/pricing/customer")
    public ApiResponse<List<CustomerPriceRuleRow>> customerPriceRules(
            @CurrentUser AuthenticatedUser user) {
        permissionGuard.require(user, "merchant:pricing:customer:view");
        return ApiResponse.success(pricingService.customerPriceRules(user.username()));
    }

    @PostMapping("/api/member/merchant/pricing/customer")
    @OperationAudit(module = "Pricing", businessType = "SAVE_CUSTOMER_PRICE_RULE")
    public ApiResponse<PricingSaveResponse> saveCustomerPriceRule(
            @CurrentUser AuthenticatedUser user,
            @Valid @RequestBody SaveCustomerPriceRuleRequest request) {
        permissionGuard.require(user, "merchant:pricing:customer:view");
        return ApiResponse.success(pricingService.saveCustomerPriceRule(user.username(), request));
    }

    @GetMapping("/api/member/merchant/pricing/options")
    public ApiResponse<PricingOptionsResponse> pricingOptions(@CurrentUser AuthenticatedUser user) {
        permissionGuard.requireAny(
                user, "merchant:pricing:level:view", "merchant:pricing:customer:view");
        return ApiResponse.success(pricingService.options(user.username()));
    }
}
