package com.telecom.scm.mall.service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.mall.dto.response.ProductDetailResponse;
import com.telecom.scm.mall.dto.response.ProductSummaryResponse;
import com.telecom.scm.security.model.AuthenticatedUser;

public interface MallProductService {

    PageResult<ProductSummaryResponse> listProducts(AuthenticatedUser user, int page, int pageSize);

    ProductDetailResponse getProduct(AuthenticatedUser user, Long id);

    PageResult<ProductSummaryResponse> listProductsByMerchant(
            Long merchantId, int page, int pageSize);
}
