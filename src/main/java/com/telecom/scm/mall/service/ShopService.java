package com.telecom.scm.mall.service;

import com.telecom.scm.mall.dto.response.ShopResponse;

public interface ShopService {

    ShopResponse getShop(Long merchantId);
}
