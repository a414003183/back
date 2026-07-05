package com.telecom.scm.app.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.telecom.scm.app.dto.response.MallCategoryResponse;
import com.telecom.scm.app.dto.response.MallHomeResponse;
import com.telecom.scm.app.dto.response.MallProductListResponse;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.mall.dto.response.ProductDetailResponse;
import com.telecom.scm.mall.dto.response.ProductSummaryResponse;
import com.telecom.scm.mall.service.MallProductService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/app/mall")
public class AppMallController {

    private final MallProductService productQueryService;

    public AppMallController(MallProductService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping("/home")
    public ApiResponse<MallHomeResponse> home() {
        List<ProductSummaryResponse> allProducts = fetchAllProducts();

        List<ProductSummaryResponse> featured = allProducts.stream().limit(8).toList();

        List<ProductSummaryResponse> memberDeals =
                allProducts.stream().filter(p -> p.currentLevel() != null).limit(4).toList();

        List<ProductSummaryResponse> newArrivals =
                allProducts.stream().skip(Math.max(0, allProducts.size() - 4)).toList();

        List<MallCategoryResponse> categories = buildCategories(allProducts);

        return ApiResponse.success(
                new MallHomeResponse(
                        categories,
                        featured,
                        memberDeals.isEmpty() ? featured : memberDeals,
                        newArrivals));
    }

    @GetMapping("/categories")
    public ApiResponse<List<MallCategoryResponse>> categories() {
        List<ProductSummaryResponse> allProducts = fetchAllProducts();
        return ApiResponse.success(buildCategories(allProducts));
    }

    @GetMapping("/products")
    public ApiResponse<MallProductListResponse> listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<ProductSummaryResponse> base = fetchAllProducts();

        List<ProductSummaryResponse> filtered = base;

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.toLowerCase();
            final List<ProductSummaryResponse> f = filtered;
            filtered =
                    f.stream()
                            .filter(p -> p.name() != null && p.name().toLowerCase().contains(kw))
                            .toList();
        }

        if (categoryId != null) {
            Set<String> seenCategories = new LinkedHashSet<>();
            long idx = 0;
            String tc = null;
            for (ProductSummaryResponse p : filtered) {
                if (p.category() != null && seenCategories.add(p.category())) {
                    if (idx == categoryId) {
                        tc = p.category();
                        break;
                    }
                    idx++;
                }
            }
            if (tc != null) {
                final String targetCat = tc;
                final List<ProductSummaryResponse> f = filtered;
                filtered = f.stream().filter(p -> targetCat.equals(p.category())).toList();
            }
        }

        List<ProductSummaryResponse> sorted;
        if ("price_asc".equals(sortBy)) {
            sorted =
                    filtered.stream()
                            .sorted(Comparator.comparingDouble(ProductSummaryResponse::price))
                            .toList();
        } else if ("price_desc".equals(sortBy)) {
            sorted =
                    filtered.stream()
                            .sorted(
                                    Comparator.comparingDouble(ProductSummaryResponse::price)
                                            .reversed())
                            .toList();
        } else {
            sorted = filtered;
        }

        int total = sorted.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<ProductSummaryResponse> pageData =
                start < total ? sorted.subList(start, end) : List.of();

        return ApiResponse.success(new MallProductListResponse(pageData, total, page, pageSize));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<ProductDetailResponse> getProduct(
            @CurrentUser(required = false) AuthenticatedUser user, @PathVariable Long id) {
        return ApiResponse.success(productQueryService.getProduct(user, id));
    }

    private List<ProductSummaryResponse> fetchAllProducts() {
        List<ProductSummaryResponse> all = new ArrayList<>();
        int page = 1;
        while (true) {
            PageResult<ProductSummaryResponse> result =
                    productQueryService.listProducts(null, page, 200);
            all.addAll(result.list());
            if (all.size() >= result.total()) {
                break;
            }
            page++;
        }
        return all;
    }

    private List<MallCategoryResponse> buildCategories(List<ProductSummaryResponse> products) {
        Set<String> seenCategories = new LinkedHashSet<>();
        List<MallCategoryResponse> categories = new ArrayList<>();
        long catIndex = 0;
        for (ProductSummaryResponse p : products) {
            if (p.category() != null && seenCategories.add(p.category())) {
                categories.add(new MallCategoryResponse(catIndex++, p.category(), 0L));
            }
        }
        return categories;
    }
}
