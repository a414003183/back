package com.telecom.scm.admin.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "导入数据响应")
public class ImportDataResponse {

    @Schema(description = "类型")
    private final String type;

    @Schema(description = "Name")
    private final String fileName;

    @Schema(description = "Rows")
    private final int totalRows;

    @Schema(description = "Count")
    private final int successCount;

    @Schema(description = "Count")
    private final int skippedCount;

    @Schema(description = "messages")
    private final List<String> messages;

    public ImportDataResponse(
            String type,
            String fileName,
            int totalRows,
            int successCount,
            int skippedCount,
            List<String> messages) {
        this.type = type;
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.successCount = successCount;
        this.skippedCount = skippedCount;
        this.messages = messages;
    }

    public String getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public List<String> getMessages() {
        return messages;
    }
}
