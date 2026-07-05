package com.telecom.scm.member.service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.SaveMerchantGoodsRequest;
import com.telecom.scm.member.dto.request.SaveMerchantProductRequest;
import com.telecom.scm.member.dto.response.MerchantGoodsOptionsResponse;
import com.telecom.scm.member.mapper.row.MerchantGoodsRow;

public interface MerchantGoodsService {
    PageResult<MerchantGoodsRow> goods(
            String username, boolean includeCostFields, int page, int pageSize);

    MerchantGoodsOptionsResponse options();

    MerchantGoodsRow createProduct(String username, SaveMerchantProductRequest request);

    MerchantGoodsRow getGood(String username, Long merchantGoodsId);

    MerchantGoodsRow updateGood(
            String username, Long merchantGoodsId, SaveMerchantGoodsRequest request);

    void deleteGood(String username, Long merchantGoodsId);
}
