package com.telecom.scm.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MallProductMapper {

    List<ProductRow> selectProductRows(@Param("offset") int offset, @Param("limit") int limit);

    long countProductRows();

    ProductRow selectProductRowById(@Param("id") Long id);

    List<ProductRow> selectProductRowsByMerchantId(
            @Param("merchantId") Long merchantId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countProductRowsByMerchantId(@Param("merchantId") Long merchantId);
}
