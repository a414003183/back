package com.telecom.scm.mall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.mall.dto.response.ProductDetailResponse;
import com.telecom.scm.mall.dto.response.ProductSummaryResponse;
import com.telecom.scm.mall.service.MallProductService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/mall/products")
public class MallProductController {

    private final MallProductService mallProductService;

    public MallProductController(MallProductService mallProductService) {
        this.mallProductService = mallProductService;
    }

    @GetMapping
    public ApiResponse<PageResult<ProductSummaryResponse>> listProducts(
            @CurrentUser(required = false) AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        return ApiResponse.success(mallProductService.listProducts(user, page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProduct(
            @CurrentUser(required = false) AuthenticatedUser user, @PathVariable Long id) {
        return ApiResponse.success(mallProductService.getProduct(user, id));
    }
}
