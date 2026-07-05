package com.telecom.scm.audit.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 登录日志实体，对应 sys_login_log 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("login_log")
public class LoginLogEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "登录状态")
    private String loginStatus;

    @Schema(description = "登录消息")
    private String loginMessage;
}
