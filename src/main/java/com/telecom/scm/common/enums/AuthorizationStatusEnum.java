package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum AuthorizationStatusEnum {
    ACTIVE("ACTIVE", "已授权"),
    REVOKED("REVOKED", "已撤销");

    @EnumValue private final String code;
    private final String description;

    AuthorizationStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AuthorizationStatusEnum fromCode(String code) {
        for (AuthorizationStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown authorization status: " + code);
    }
}
