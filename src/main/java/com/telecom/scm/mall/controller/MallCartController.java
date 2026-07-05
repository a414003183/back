package com.telecom.scm.mall.controller;

import java.util.List;

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
@RequestMapping("/api/mall/cart")
public class MallCartController {

    private final MallCartService mallCartService;

    public MallCartController(MallCartService mallCartService) {
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

    @PostMapping("/checkout")
    public ApiResponse<OrderCreateResponse> checkout(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody MallCheckoutRequest request) {
        return ApiResponse.success(mallCartService.checkout(user.username(), request));
    }

    @PostMapping("/direct-buy")
    public ApiResponse<OrderCreateResponse> directBuy(
            @CurrentUser AuthenticatedUser user, @Valid @RequestBody DirectBuyRequest request) {
        return ApiResponse.success(mallCartService.directBuy(user.username(), request));
    }
}
