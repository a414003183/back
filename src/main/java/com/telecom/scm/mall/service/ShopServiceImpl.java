package com.telecom.scm.mall.service;

import org.springframework.stereotype.Service;

import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.mall.dto.response.ShopResponse;
import com.telecom.scm.mall.mapper.ShopMapper;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;

    public ShopServiceImpl(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    @Override
    public ShopResponse getShop(Long merchantId) {
        ShopResponse shop = shopMapper.selectShopById(merchantId);
        if (shop == null) {
            throw new BusinessException(404, "shop not found");
        }
        return shop;
    }
}
