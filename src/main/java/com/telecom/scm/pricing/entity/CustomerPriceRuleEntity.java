package com.telecom.scm.pricing.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** CustomerPriceRuleEntity 实体，映射 customer_price_rule 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_price_rule")
public class CustomerPriceRuleEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "operator id")
    private Long operatorId;

    @Schema(description = "客户 ID")
    private Long customerId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "special price")
    private BigDecimal specialPrice;

    @Schema(description = "状态")
    private AccountStatusEnum status;
}
