package com.telecom.scm.aftersale.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 售后审核日志实体，对应 aftersale_audit_log 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("aftersale_audit_log")
public class AftersaleAuditLogEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "售后单 ID")
    private Long aftersaleId;

    @Schema(description = "action type")
    private String actionType;

    @Schema(description = "old status")
    private String oldStatus;

    @Schema(description = "new status")
    private String newStatus;

    @Schema(description = "operator id")
    private Long operatorId;

    @Schema(description = "operator name")
    private String operatorName;
}
