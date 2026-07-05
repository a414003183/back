package com.telecom.scm.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "消息响应")
public class MessageResponse {

    @Schema(description = "消息")
    private final String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }

    public String getMessage() {
        return message;
    }
}
