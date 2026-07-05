package com.telecom.scm.security.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 会员账号实体，映射 member_account 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_account")
public class MemberAccountEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "会员编码")
    private String memberCode;

    @Schema(description = "会员类型")
    private String memberType;

    @Schema(description = "会员名称")
    private String memberName;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态")
    private String status;
}
