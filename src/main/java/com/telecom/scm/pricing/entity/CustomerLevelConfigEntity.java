package com.telecom.scm.pricing.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** CustomerLevelConfigEntity 实体，映射 customer_level_config 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_level_config")
public class CustomerLevelConfigEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "level code")
    private String levelCode;

    @Schema(description = "level name")
    private String levelName;

    @Schema(description = "upgrade threshold amount")
    private BigDecimal upgradeThresholdAmount;

    @Schema(description = "sort no")
    private Integer sortNo;

    @Schema(description = "状态")
    private AccountStatusEnum status;

    @Schema(description = "operator id")
    private Long operatorId;
}
