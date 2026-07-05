package com.telecom.scm.admin.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "导入导出概览响应")
public class ImportExportOverviewResponse {

    private final List<SupportedType> supportedTypes;

    public ImportExportOverviewResponse(List<SupportedType> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    public List<SupportedType> getSupportedTypes() {
        return supportedTypes;
    }

    public record SupportedType(
            @Schema(description = "编码") String code, @Schema(description = "名称") String name) {}
}
