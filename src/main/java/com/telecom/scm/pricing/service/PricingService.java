package com.telecom.scm.pricing.service;

import java.util.List;

import com.telecom.scm.pricing.dto.request.SaveCustomerPriceRuleRequest;
import com.telecom.scm.pricing.dto.request.SaveGoodsAuthRuleRequest;
import com.telecom.scm.pricing.dto.request.SaveLevelDiscountRuleRequest;
import com.telecom.scm.pricing.dto.response.PricingOptionsResponse;
import com.telecom.scm.pricing.dto.response.PricingSaveResponse;
import com.telecom.scm.pricing.mapper.row.CustomerPriceRuleRow;
import com.telecom.scm.pricing.mapper.row.GoodsAuthRuleRow;
import com.telecom.scm.pricing.mapper.row.LevelDiscountRuleRow;

public interface PricingService {

    List<GoodsAuthRuleRow> goodsAuthRules(String username);

    PricingSaveResponse saveGoodsAuthRule(String username, SaveGoodsAuthRuleRequest request);

    List<LevelDiscountRuleRow> levelDiscountRules(String username);

    PricingSaveResponse saveLevelDiscountRule(
            String username, SaveLevelDiscountRuleRequest request);

    List<CustomerPriceRuleRow> customerPriceRules(String username);

    PricingSaveResponse saveCustomerPriceRule(
            String username, SaveCustomerPriceRuleRequest request);

    PricingOptionsResponse options(String username);
}
