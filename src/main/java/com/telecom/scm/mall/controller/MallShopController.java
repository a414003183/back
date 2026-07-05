package com.telecom.scm.mall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.mall.dto.response.ProductSummaryResponse;
import com.telecom.scm.mall.dto.response.ShopResponse;
import com.telecom.scm.mall.service.MallProductService;
import com.telecom.scm.mall.service.ShopService;

@RestController
@RequestMapping("/api/mall/shops")
public class MallShopController {

    private final ShopService shopQueryService;
    private final MallProductService productQueryService;

    public MallShopController(
            ShopService shopQueryService, MallProductService productQueryService) {
        this.shopQueryService = shopQueryService;
        this.productQueryService = productQueryService;
    }

    @GetMapping("/{merchantId}")
    public ApiResponse<ShopResponse> getShop(@PathVariable Long merchantId) {
        return ApiResponse.success(shopQueryService.getShop(merchantId));
    }

    @GetMapping("/{merchantId}/products")
    public ApiResponse<PageResult<ProductSummaryResponse>> getShopProducts(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        return ApiResponse.success(
                productQueryService.listProductsByMerchant(merchantId, page, pageSize));
    }
}
