package com.telecom.scm.pricing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "定价保存响应")
public class PricingSaveResponse {

    @Schema(description = "消息")
    private String message;

    public PricingSaveResponse() {}

    public PricingSaveResponse(String message) {
        this.message = message;
    }

    /** 静态工厂方法，用于替代 {@code new PricingSaveResponse("...")}。 */
    public static PricingSaveResponse of(String message) {
        return new PricingSaveResponse(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
