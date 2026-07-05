package com.telecom.scm.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RegistrationSubmission响应")
public record RegistrationSubmissionResponse(
        @Schema(description = "状态") String status, @Schema(description = "消息") String message) {

    public static RegistrationSubmissionResponse of(String status, String message) {
        return new RegistrationSubmissionResponse(status, message);
    }
}
