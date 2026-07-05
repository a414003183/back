package com.telecom.scm.points.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 积分账户实体，对应 point_account 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("point_account")
public class PointAccountEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "当前积分")
    private Integer currentPoints;

    @Schema(description = "累计增加积分")
    private Integer totalIncrease;

    @Schema(description = "累计减少积分")
    private Integer totalDecrease;

    @Schema(description = "状态")
    private AccountStatusEnum status;
}
