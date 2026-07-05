package com.telecom.scm.security.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 客户信息实体，映射 customer_info 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_info")
public class CustomerInfoEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "会员 ID")
    private Long memberId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "会员等级")
    private String memberLevel;

    @Schema(description = "积分余额")
    private Integer pointsBalance;

    @Schema(description = "累计积分")
    private Integer accumulatedPoints;

    @Schema(description = "已用积分")
    private Integer usedPoints;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "状态")
    private String status;
}
