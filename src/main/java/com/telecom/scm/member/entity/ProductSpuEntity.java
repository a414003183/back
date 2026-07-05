package com.telecom.scm.member.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** ProductSpuEntity 实体，映射 product_spu 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_spu")
public class ProductSpuEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "brand id")
    private Long brandId;

    @Schema(description = "category id")
    private Long categoryId;

    @Schema(description = "spu code")
    private String spuCode;

    @Schema(description = "spu name")
    private String spuName;

    @Schema(description = "main image id")
    private Long mainImageId;

    @Schema(description = "image ids")
    private String imageIds;

    @Schema(description = "keywords")
    private String keywords;

    @Schema(description = "detail content")
    private String detailContent;

    @Schema(description = "description")
    private String description;

    @Schema(description = "operator id")
    private Long operatorId;
}
