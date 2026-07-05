package com.telecom.scm.mall.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.mall.convert.MallConvert;
import com.telecom.scm.mall.dto.response.ProductDetailResponse;
import com.telecom.scm.mall.dto.response.ProductSummaryResponse;
import com.telecom.scm.mall.mapper.MallProductMapper;
import com.telecom.scm.mall.mapper.ProductRow;
import com.telecom.scm.member.mapper.MemberWorkspaceMapper;
import com.telecom.scm.member.mapper.row.CustomerContextRow;
import com.telecom.scm.pricing.mapper.CustomerLevelMapper;
import com.telecom.scm.pricing.mapper.PricingMapper;
import com.telecom.scm.pricing.mapper.row.CustomerLevelConfigRow;
import com.telecom.scm.security.model.AuthenticatedUser;

@Service
public class MallProductServiceImpl implements MallProductService {

    private static final Logger log = LoggerFactory.getLogger(MallProductServiceImpl.class);

    private final MallProductMapper mallProductMapper;
    private final MemberWorkspaceMapper memberWorkspaceMapper;
    private final CustomerLevelMapper customerLevelMapper;
    private final PricingMapper pricingMapper;

    public MallProductServiceImpl(
            MallProductMapper mallProductMapper,
            MemberWorkspaceMapper memberWorkspaceMapper,
            CustomerLevelMapper customerLevelMapper,
            PricingMapper pricingMapper) {
        this.mallProductMapper = mallProductMapper;
        this.memberWorkspaceMapper = memberWorkspaceMapper;
        this.customerLevelMapper = customerLevelMapper;
        this.pricingMapper = pricingMapper;
    }

    @Override
    public PageResult<ProductSummaryResponse> listProducts(
            AuthenticatedUser user, int page, int pageSize) {
        pageSize = Math.min(pageSize, 200);
        int offset = (page - 1) * pageSize;
        long total = mallProductMapper.countProductRows();
        if (total == 0) {
            return PageResult.empty(page, pageSize);
        }
        CustomerPricingInfo pricingInfo = getCustomerPricingInfo(user);
        List<ProductSummaryResponse> list =
                mallProductMapper.selectProductRows(offset, pageSize).stream()
                        .map(row -> toSummary(row, pricingInfo))
                        .toList();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public PageResult<ProductSummaryResponse> listProductsByMerchant(
            Long merchantId, int page, int pageSize) {
        pageSize = Math.min(pageSize, 200);
        int offset = (page - 1) * pageSize;
        long total = mallProductMapper.countProductRowsByMerchantId(merchantId);
        if (total == 0) {
            return PageResult.empty(page, pageSize);
        }
        List<ProductSummaryResponse> list =
                mallProductMapper
                        .selectProductRowsByMerchantId(merchantId, offset, pageSize)
                        .stream()
                        .map(this::toSummaryWithoutPricing)
                        .toList();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public ProductDetailResponse getProduct(AuthenticatedUser user, Long id) {
        log.info("getProduct called with id={}", id);
        ProductRow row = mallProductMapper.selectProductRowById(id);
        if (row == null) {
            throw new BusinessException(404, "product not found");
        }
        CustomerPricingInfo pricingInfo = getCustomerPricingInfo(user);
        return toDetail(row, pricingInfo);
    }

    /** 获取客户价格信息 */
    private CustomerPricingInfo getCustomerPricingInfo(AuthenticatedUser user) {
        CustomerPricingInfo info = new CustomerPricingInfo();
        info.setLevelConfigs(customerLevelMapper.selectAllCustomerLevelConfigs());
        info.setNotLoggedIn(true);

        if (user == null) {
            log.info("用户未登录");
            return info;
        }

        String username = user.username();
        Long memberId = user.memberId();
        log.info("当前登录用户: {}, memberId: {}", username, memberId);

        // 使用 memberId 直接查询客户信息
        if (memberId != null) {
            CustomerContextRow context =
                    memberWorkspaceMapper.selectCustomerContextByMemberId(memberId);
            log.info("根据memberId获取到的客户上下文: {}", context);
            if (context != null && context.getCustomerId() != null) {
                String memberLevel = context.getMemberLevel();
                Double accumulatedAmount = null;
                if (context.getAccumulatedPaidAmount() != null) {
                    accumulatedAmount = context.getAccumulatedPaidAmount().doubleValue();
                }
                log.info("当前客户等级: {}, 累计消费: {}", memberLevel, accumulatedAmount);
                info.setNotLoggedIn(false);
                info.setCurrentLevel(memberLevel);
                info.setAccumulatedAmount(accumulatedAmount);
                if (memberLevel != null && !memberLevel.isEmpty()) {
                    double threshold = getThresholdForLevel(memberLevel, info.getLevelConfigs());
                    boolean reachedThreshold =
                            accumulatedAmount != null && accumulatedAmount >= threshold;
                    log.info("等级 {} 阈值: {}, 达到阈值: {}", memberLevel, threshold, reachedThreshold);
                    info.setReachedThreshold(reachedThreshold);
                }
                return info;
            }
        }

        // 备用：通过username查询
        CustomerContextRow context =
                memberWorkspaceMapper.selectCustomerContextByUsername(username);
        log.info("根据username获取到的客户上下文: {}", context);
        if (context != null && context.getCustomerId() != null) {
            String memberLevel = context.getMemberLevel();
            Double accumulatedAmount = null;
            if (context.getAccumulatedPaidAmount() != null) {
                accumulatedAmount = context.getAccumulatedPaidAmount().doubleValue();
            }
            log.info("当前客户等级: {}, 累计消费: {}", memberLevel, accumulatedAmount);
            info.setNotLoggedIn(false);
            info.setCurrentLevel(memberLevel);
            info.setAccumulatedAmount(accumulatedAmount);
            if (memberLevel != null && !memberLevel.isEmpty()) {
                double threshold = getThresholdForLevel(memberLevel, info.getLevelConfigs());
                boolean reachedThreshold =
                        accumulatedAmount != null && accumulatedAmount >= threshold;
                log.info("等级 {} 阈值: {}, 达到阈值: {}", memberLevel, threshold, reachedThreshold);
                info.setReachedThreshold(reachedThreshold);
            }
        }
        return info;
    }

    /** 获取指定等级的升级阈值 */
    private double getThresholdForLevel(
            String levelCode, List<CustomerLevelConfigRow> levelConfigs) {
        if (levelConfigs == null || levelCode == null) {
            return 0;
        }
        for (CustomerLevelConfigRow level : levelConfigs) {
            if (levelCode.equals(level.getLevelCode())) {
                BigDecimal threshold = level.getUpgradeThresholdAmount();
                return threshold != null ? threshold.doubleValue() : 0;
            }
        }
        return 0;
    }

    /** 获取当前等级的实际价格（按优先级：商品 > 品牌 > 分类） */
    private double getCurrentLevelPrice(
            double basePrice,
            Long merchantId,
            Long skuId,
            Long brandId,
            Long categoryId,
            CustomerPricingInfo pricingInfo) {
        if (pricingInfo.isNotLoggedIn()
                || pricingInfo.getCurrentLevel() == null
                || !pricingInfo.isReachedThreshold()) {
            return basePrice; // 未登录或未达阈值显示原价
        }
        BigDecimal discountRate =
                pricingMapper.selectBestLevelDiscountRate(
                        merchantId, pricingInfo.getCurrentLevel(), skuId, brandId, categoryId);
        if (discountRate == null || discountRate.doubleValue() >= 10.0) {
            return basePrice; // 该商家未配置此等级折扣，显示原价
        }
        return BigDecimal.valueOf(basePrice)
                .multiply(discountRate)
                .divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private ProductDetailResponse toDetail(ProductRow row, CustomerPricingInfo pricingInfo) {
        double basePrice = row.getPrice();
        List<Map<String, Object>> levelPrices =
                buildLevelPrices(
                        row.getMerchantId(),
                        row.getSkuId(),
                        row.getBrandId(),
                        row.getCategoryId(),
                        basePrice);
        double currentPrice =
                getCurrentLevelPrice(
                        basePrice,
                        row.getMerchantId(),
                        row.getSkuId(),
                        row.getBrandId(),
                        row.getCategoryId(),
                        pricingInfo);
        BigDecimal currentDiscountRate =
                pricingInfo.isReachedThreshold()
                        ? pricingMapper.selectBestLevelDiscountRate(
                                row.getMerchantId(),
                                pricingInfo.getCurrentLevel(),
                                row.getSkuId(),
                                row.getBrandId(),
                                row.getCategoryId())
                        : null;

        MallConvert.ProductPriceContext ctx =
                new MallConvert.ProductPriceContext(
                        currentPrice,
                        pricingInfo.getCurrentLevel(),
                        getLevelName(pricingInfo.getCurrentLevel(), pricingInfo.getLevelConfigs()),
                        currentDiscountRate != null ? currentDiscountRate.doubleValue() : null,
                        levelPrices);
        return MallConvert.INSTANCE.toProductDetailResponse(row, ctx);
    }

    private ProductSummaryResponse toSummary(ProductRow row, CustomerPricingInfo pricingInfo) {
        double basePrice = row.getPrice();
        double currentPrice =
                getCurrentLevelPrice(
                        basePrice,
                        row.getMerchantId(),
                        row.getSkuId(),
                        row.getBrandId(),
                        row.getCategoryId(),
                        pricingInfo);
        BigDecimal currentDiscountRate =
                pricingInfo.isReachedThreshold()
                        ? pricingMapper.selectBestLevelDiscountRate(
                                row.getMerchantId(),
                                pricingInfo.getCurrentLevel(),
                                row.getSkuId(),
                                row.getBrandId(),
                                row.getCategoryId())
                        : null;

        MallConvert.ProductPriceContext ctx =
                new MallConvert.ProductPriceContext(
                        currentPrice,
                        pricingInfo.getCurrentLevel(),
                        getLevelName(pricingInfo.getCurrentLevel(), pricingInfo.getLevelConfigs()),
                        currentDiscountRate != null ? currentDiscountRate.doubleValue() : null,
                        buildLevelPrices(
                                row.getMerchantId(),
                                row.getSkuId(),
                                row.getBrandId(),
                                row.getCategoryId(),
                                basePrice));
        return MallConvert.INSTANCE.toProductSummaryResponse(row, ctx);
    }

    private ProductSummaryResponse toSummaryWithoutPricing(ProductRow row) {
        MallConvert.ProductPriceContext ctx =
                new MallConvert.ProductPriceContext(row.getPrice(), null, null, null, null);
        return MallConvert.INSTANCE.toProductSummaryResponse(row, ctx);
    }

    private List<Map<String, Object>> buildLevelPrices(
            Long merchantId, Long skuId, Long brandId, Long categoryId, double basePrice) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<CustomerLevelConfigRow> levelConfigs =
                customerLevelMapper.selectAllCustomerLevelConfigs();
        if (levelConfigs == null) {
            return result;
        }
        for (CustomerLevelConfigRow level : levelConfigs) {
            Map<String, Object> priceInfo = new HashMap<>();
            String levelCode = level.getLevelCode();
            priceInfo.put("levelCode", levelCode);
            priceInfo.put("levelName", level.getLevelName());
            BigDecimal discountRate =
                    pricingMapper.selectBestLevelDiscountRate(
                            merchantId, levelCode, skuId, brandId, categoryId);
            double discount = discountRate != null ? discountRate.doubleValue() : 10.0;
            double levelPrice =
                    discount >= 10.0
                            ? basePrice
                            : BigDecimal.valueOf(basePrice)
                                    .multiply(BigDecimal.valueOf(discount))
                                    .divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP)
                                    .doubleValue();
            priceInfo.put("price", levelPrice);
            priceInfo.put("discountRate", discount);
            result.add(priceInfo);
        }
        return result;
    }

    private String getLevelName(String levelCode, List<CustomerLevelConfigRow> levelConfigs) {
        if (levelCode == null || levelConfigs == null) {
            return null;
        }
        for (CustomerLevelConfigRow level : levelConfigs) {
            if (levelCode.equals(level.getLevelCode())) {
                return level.getLevelName();
            }
        }
        return null;
    }

    /** 内部类：客户价格信息 */
    private static class CustomerPricingInfo {
        private boolean notLoggedIn = true;
        private String currentLevel;
        private boolean reachedThreshold = false;
        private List<CustomerLevelConfigRow> levelConfigs;
        private List<Map<String, Object>> levelDiscountRules;
        private Double accumulatedAmount;

        public boolean isNotLoggedIn() {
            return notLoggedIn;
        }

        public void setNotLoggedIn(boolean notLoggedIn) {
            this.notLoggedIn = notLoggedIn;
        }

        public String getCurrentLevel() {
            return currentLevel;
        }

        public void setCurrentLevel(String currentLevel) {
            this.currentLevel = currentLevel;
        }

        public boolean isReachedThreshold() {
            return reachedThreshold;
        }

        public void setReachedThreshold(boolean reachedThreshold) {
            this.reachedThreshold = reachedThreshold;
        }

        public List<CustomerLevelConfigRow> getLevelConfigs() {
            return levelConfigs;
        }

        public void setLevelConfigs(List<CustomerLevelConfigRow> levelConfigs) {
            this.levelConfigs = levelConfigs;
        }

        public List<Map<String, Object>> getLevelDiscountRules() {
            return levelDiscountRules;
        }

        public void setLevelDiscountRules(List<Map<String, Object>> levelDiscountRules) {
            this.levelDiscountRules = levelDiscountRules;
        }

        public Double getAccumulatedAmount() {
            return accumulatedAmount;
        }

        public void setAccumulatedAmount(Double accumulatedAmount) {
            this.accumulatedAmount = accumulatedAmount;
        }
    }
}
