package com.telecom.scm.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商城分类响应")
public record MallCategoryResponse(
        @Schema(description = "ID") Long id,
        @Schema(description = "名称") String name,
        @Schema(description = "父级 ID") Long parentId) {}
