package com.telecom.scm.audit.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 操作日志实体，对应 sys_operation_log 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("operation_log")
public class OperationLogEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "请求URI")
    private String requestUri;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "操作状态")
    private String operationStatus;

    @Schema(description = "响应消息")
    private String responseMessage;

    @Schema(description = "错误堆栈")
    private String errorStack;
}
