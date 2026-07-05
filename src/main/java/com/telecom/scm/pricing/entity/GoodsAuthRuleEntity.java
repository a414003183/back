package com.telecom.scm.pricing.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** GoodsAuthRuleEntity 实体，映射 goods_auth_rule 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("goods_auth_rule")
public class GoodsAuthRuleEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "operator id")
    private Long operatorId;

    @Schema(description = "auth type")
    private String authType;

    @Schema(description = "target id")
    private Long targetId;

    @Schema(description = "状态")
    private AccountStatusEnum status;
}
