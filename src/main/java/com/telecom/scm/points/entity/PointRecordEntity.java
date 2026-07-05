package com.telecom.scm.points.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 积分流水实体，对应 point_record 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("point_record")
public class PointRecordEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "积分账户ID")
    private Long pointAccountId;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "来源类型")
    private String sourceType;

    @Schema(description = "来源ID")
    private Long sourceId;

    @Schema(description = "变更积分")
    private Integer changePoints;

    @Schema(description = "变更后余额")
    private Integer balanceAfter;
}
