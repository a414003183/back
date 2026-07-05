package com.telecom.scm.member.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** MemberAddressEntity 实体，映射 member_address 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_address")
public class MemberAddressEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "member id")
    private Long memberId;

    @Schema(description = "contact name")
    private String contactName;

    @Schema(description = "contact phone")
    private String contactPhone;

    @Schema(description = "receiver province")
    private String receiverProvince;

    @Schema(description = "receiver city")
    private String receiverCity;

    @Schema(description = "receiver district")
    private String receiverDistrict;

    @Schema(description = "receiver address")
    private String receiverAddress;
}
