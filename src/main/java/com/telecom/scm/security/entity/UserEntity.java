package com.telecom.scm.security.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.UserStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统用户实体，映射 sys_user 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "用户类型")
    private String userType;

    @Schema(description = "会员 ID")
    private Long memberId;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态")
    private UserStatusEnum status;

    @Schema(description = "注册来源")
    private String registerSource;

    @Schema(description = "注册状态")
    private String registerStatus;

    @Schema(description = "最后活跃身份类型")
    private String lastActiveIdentityType;
}
