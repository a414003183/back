package com.telecom.scm.security.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** UserIdentityBindingEntity 实体，映射 user_identity_binding 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_identity_binding")
public class UserIdentityBindingEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "user id")
    private Long userId;

    @Schema(description = "identity type")
    private String identityType;

    @Schema(description = "member id")
    private Long memberId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "default identity")
    private Integer defaultIdentity;
}
