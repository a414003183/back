package com.telecom.scm.pricing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.pricing.convert.PricingConvert;
import com.telecom.scm.pricing.dto.request.SaveCustomerPriceRuleRequest;
import com.telecom.scm.pricing.dto.request.SaveGoodsAuthRuleRequest;
import com.telecom.scm.pricing.dto.request.SaveLevelDiscountRuleRequest;
import com.telecom.scm.pricing.dto.response.PricingOptionsResponse;
import com.telecom.scm.pricing.dto.response.PricingSaveResponse;
import com.telecom.scm.pricing.entity.CustomerPriceRuleEntity;
import com.telecom.scm.pricing.entity.GoodsAuthRuleEntity;
import com.telecom.scm.pricing.entity.LevelDiscountRuleEntity;
import com.telecom.scm.pricing.mapper.PricingMapper;
import com.telecom.scm.pricing.mapper.row.CustomerPriceRuleRow;
import com.telecom.scm.pricing.mapper.row.GoodsAuthRuleRow;
import com.telecom.scm.pricing.mapper.row.LevelDiscountRuleRow;
import com.telecom.scm.pricing.mapper.row.MerchantContextRow;

@Service
public class PricingServiceImpl implements PricingService {

    private final PricingMapper pricingMapper;
    private final CustomerLevelService customerLevelService;

    public PricingServiceImpl(
            PricingMapper pricingMapper, CustomerLevelService customerLevelService) {
        this.pricingMapper = pricingMapper;
        this.customerLevelService = customerLevelService;
    }

    @Override
    public List<GoodsAuthRuleRow> goodsAuthRules(String username) {
        return pricingMapper.selectGoodsAuthRules(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PricingSaveResponse saveGoodsAuthRule(
            String username, SaveGoodsAuthRuleRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        GoodsAuthRuleEntity param = new GoodsAuthRuleEntity();
        param.setMerchantId(context.getMerchantId());
        param.setOperatorId(context.getUserId());
        param.setAuthType(request.authType().toUpperCase());
        param.setTargetId(request.targetId());
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setRemark(request.remark());
        pricingMapper.upsertGoodsAuthRule(param);
        return PricingSaveResponse.of("goods auth rule saved");
    }

    @Override
    public List<LevelDiscountRuleRow> levelDiscountRules(String username) {
        return pricingMapper.selectLevelDiscountRules(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PricingSaveResponse saveLevelDiscountRule(
            String username, SaveLevelDiscountRuleRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        LevelDiscountRuleEntity param = new LevelDiscountRuleEntity();
        param.setMerchantId(context.getMerchantId());
        param.setOperatorId(context.getUserId());
        param.setMemberLevel(request.memberLevel().toUpperCase());
        param.setTargetType(normalizeText(request.targetType()).toUpperCase());
        param.setTargetId(request.targetId());
        param.setDiscountRate(request.discountRate());
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setRemark(request.remark());
        pricingMapper.upsertLevelDiscountRule(param);
        return PricingSaveResponse.of("level discount rule saved");
    }

    @Override
    public List<CustomerPriceRuleRow> customerPriceRules(String username) {
        return pricingMapper.selectCustomerPriceRules(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PricingSaveResponse saveCustomerPriceRule(
            String username, SaveCustomerPriceRuleRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        CustomerPriceRuleEntity param = new CustomerPriceRuleEntity();
        param.setMerchantId(context.getMerchantId());
        param.setOperatorId(context.getUserId());
        param.setCustomerId(request.customerId());
        param.setSkuId(request.skuId());
        param.setSpecialPrice(request.specialPrice());
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setRemark(request.remark());
        pricingMapper.upsertCustomerPriceRule(param);
        return PricingSaveResponse.of("customer price rule saved");
    }

    @Override
    public PricingOptionsResponse options(String username) {
        return PricingConvert.INSTANCE.toPricingOptionsResponse(
                pricingMapper.selectBrandOptions(),
                pricingMapper.selectCategoryOptions(),
                pricingMapper.selectCustomerOptions(),
                pricingMapper.selectMerchantSkuOptions(username),
                customerLevelService.levelOptions());
    }

    private MerchantContextRow requireMerchantContext(String username) {
        MerchantContextRow context = pricingMapper.selectMerchantContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "merchant account is unavailable");
        }
        return context;
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
