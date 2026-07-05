package com.telecom.scm.pricing.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.telecom.scm.pricing.entity.CustomerPriceRuleEntity;
import com.telecom.scm.pricing.entity.GoodsAuthRuleEntity;
import com.telecom.scm.pricing.entity.LevelDiscountRuleEntity;
import com.telecom.scm.pricing.mapper.row.BrandOptionRow;
import com.telecom.scm.pricing.mapper.row.CategoryOptionRow;
import com.telecom.scm.pricing.mapper.row.CustomerOptionRow;
import com.telecom.scm.pricing.mapper.row.CustomerPriceRuleRow;
import com.telecom.scm.pricing.mapper.row.GoodsAuthRuleRow;
import com.telecom.scm.pricing.mapper.row.LevelDiscountRuleRow;
import com.telecom.scm.pricing.mapper.row.MerchantContextRow;
import com.telecom.scm.pricing.mapper.row.MerchantSkuOptionRow;

@Mapper
public interface PricingMapper {

    MerchantContextRow selectMerchantContextByUsername(@Param("username") String username);

    List<GoodsAuthRuleRow> selectGoodsAuthRules(@Param("username") String username);

    int upsertGoodsAuthRule(GoodsAuthRuleEntity payload);

    List<LevelDiscountRuleRow> selectLevelDiscountRules(@Param("username") String username);

    int upsertLevelDiscountRule(LevelDiscountRuleEntity payload);

    List<CustomerPriceRuleRow> selectCustomerPriceRules(@Param("username") String username);

    int upsertCustomerPriceRule(CustomerPriceRuleEntity payload);

    @Select(
            """
        SELECT CAST(id AS CHAR) AS value, brand_name AS label
        FROM product_brand
        WHERE deleted = 0
            AND status = 'ENABLED'
        ORDER BY sort_no ASC, id DESC
        """)
    List<BrandOptionRow> selectBrandOptions();

    @Select(
            """
        SELECT CAST(id AS CHAR) AS value, category_name AS label, parent_id AS parentId, level_no AS levelNo
        FROM product_category
        WHERE deleted = 0
            AND status = 'ENABLED'
        ORDER BY level_no ASC, sort_no ASC, id DESC
        """)
    List<CategoryOptionRow> selectCategoryOptions();

    List<MerchantSkuOptionRow> selectMerchantSkuOptions(@Param("username") String username);

    @Select(
            """
        SELECT CAST(id AS CHAR) AS value, company_name AS label
        FROM customer_info
        WHERE deleted = 0
            AND status = 'ENABLED'
        ORDER BY id DESC
        """)
    List<CustomerOptionRow> selectCustomerOptions();

    java.math.BigDecimal selectBestLevelDiscountRate(
            @Param("merchantId") Long merchantId,
            @Param("memberLevel") String memberLevel,
            @Param("skuId") Long skuId,
            @Param("brandId") Long brandId,
            @Param("categoryId") Long categoryId);
}
