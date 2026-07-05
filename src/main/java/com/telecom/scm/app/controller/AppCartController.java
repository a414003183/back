package com.telecom.scm.app.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.mall.dto.request.AddCartItemRequest;
import com.telecom.scm.mall.dto.request.DirectBuyRequest;
import com.telecom.scm.mall.dto.request.MallCheckoutRequest;
import com.telecom.scm.mall.dto.request.UpdateCartItemRequest;
import com.telecom.scm.mall.dto.response.MallCartItemResponse;
import com.telecom.scm.mall.service.MallCartService;
import com.telecom.scm.order.dto.response.OrderCreateResponse;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/app/mall/cart")
public class AppCartController {

    private final MallCartService mallCartService;

    public AppCartController(MallCartService mallCartService) {
        this.mallCartService = mallCartService;
    }

    @GetMapping
    public ApiResponse<PageResult<MallCartItemResponse>> currentCart(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        return ApiResponse.success(mallCartService.currentCart(user.username(), page, pageSize));
    }

    @PostMapping
    public ApiResponse<List<MallCartItemResponse>> addCartItem(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.success(mallCartService.addCartItem(user.username(), request));
    }

    @PutMapping("/{merchantGoodsId}")
    public ApiResponse<List<MallCartItemResponse>> updateCartItem(
            @CurrentUser AuthenticatedUser user,
            @PathVariable Long merchantGoodsId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success(
                mallCartService.updateCartItem(user.username(), merchantGoodsId, request));
    }

    @DeleteMapping("/{merchantGoodsId}")
    public ApiResponse<Void> removeCartItem(
            @CurrentUser AuthenticatedUser user, @PathVariable Long merchantGoodsId) {
        mallCartService.removeCartItem(user.username(), merchantGoodsId);
        return ApiResponse.success();
    }

    @PostMapping("/direct-buy")
    public ApiResponse<OrderCreateResponse> directBuy(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody DirectBuyRequest request) {
        return ApiResponse.success(mallCartService.directBuy(user.username(), request));
    }

    @PostMapping("/checkout")
    public ApiResponse<OrderCreateResponse> checkout(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody MallCheckoutRequest request) {
        return ApiResponse.success(mallCartService.checkout(user.username(), request));
    }
}
