package com.telecom.scm.admin.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableName;

import com.telecom.scm.common.base.BaseEntity;
import com.telecom.scm.common.enums.AccountStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 分类实体，映射 category 表。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class CategoryEntity extends BaseEntity {

    @Serial private static final long serialVersionUID = 1L;

    @Schema(description = "排序号")
    private Integer nextNum;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "层级")
    private Integer levelNo;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "状态")
    private AccountStatusEnum status;
}
