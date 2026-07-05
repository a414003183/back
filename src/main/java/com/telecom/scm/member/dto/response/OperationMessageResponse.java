package com.telecom.scm.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "操作消息响应")
public record OperationMessageResponse(@Schema(description = "消息") String message) {

    public static OperationMessageResponse of(String message) {
        return new OperationMessageResponse(message);
    }
}
