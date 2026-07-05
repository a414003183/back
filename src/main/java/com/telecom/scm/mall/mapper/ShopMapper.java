package com.telecom.scm.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.telecom.scm.mall.dto.response.ShopResponse;

@Mapper
public interface ShopMapper {

    ShopResponse selectShopById(@Param("merchantId") Long merchantId);
}
