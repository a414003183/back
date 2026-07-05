package com.telecom.scm.admin.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 品牌实体，映射 brand 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("brand")
public class BrandEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "排序号")
    private Integer nextNum;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "品牌描述")
    private String brandDesc;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "状态")
    private AccountStatusEnum status;
}
