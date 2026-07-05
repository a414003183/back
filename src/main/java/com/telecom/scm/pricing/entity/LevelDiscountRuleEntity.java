package com.telecom.scm.pricing.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** LevelDiscountRuleEntity 实体，映射 level_discount_rule 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("level_discount_rule")
public class LevelDiscountRuleEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "operator id")
    private Long operatorId;

    @Schema(description = "member level")
    private String memberLevel;

    @Schema(description = "target type")
    private String targetType;

    @Schema(description = "target id")
    private Long targetId;

    @Schema(description = "discount rate")
    private BigDecimal discountRate;

    @Schema(description = "状态")
    private AccountStatusEnum status;
}
