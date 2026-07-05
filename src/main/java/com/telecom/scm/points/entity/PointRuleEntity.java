package com.telecom.scm.points.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 积分规则实体，对应 point_rule 表查询结果。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("point_rule")
public class PointRuleEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "值类型")
    private String valueType;

    @Schema(description = "规则值")
    private BigDecimal ruleValue;

    @Schema(description = "抵扣比例")
    private BigDecimal deductionRatio;

    @Schema(description = "最大抵扣比例")
    private BigDecimal maxDeductionRatio;

    @Schema(description = "是否启用抵扣")
    private Boolean deductionEnabled;
}
