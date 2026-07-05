package com.telecom.scm.mall.service;

import java.util.List;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.mall.dto.request.AddCartItemRequest;
import com.telecom.scm.mall.dto.request.DirectBuyRequest;
import com.telecom.scm.mall.dto.request.MallCheckoutRequest;
import com.telecom.scm.mall.dto.request.UpdateCartItemRequest;
import com.telecom.scm.mall.dto.response.MallCartItemResponse;
import com.telecom.scm.order.dto.response.OrderCreateResponse;

public interface MallCartService {

    PageResult<MallCartItemResponse> currentCart(String username, int page, int pageSize);

    List<MallCartItemResponse> addCartItem(String username, AddCartItemRequest request);

    List<MallCartItemResponse> updateCartItem(
            String username, Long merchantGoodsId, UpdateCartItemRequest request);

    void removeCartItem(String username, Long merchantGoodsId);

    OrderCreateResponse checkout(String username, MallCheckoutRequest request);

    OrderCreateResponse directBuy(String username, DirectBuyRequest request);
}
