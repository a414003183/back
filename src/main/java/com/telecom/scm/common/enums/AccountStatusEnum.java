package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum AccountStatusEnum {
    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "禁用");

    @EnumValue private final String code;
    private final String description;

    AccountStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AccountStatusEnum fromCode(String code) {
        for (AccountStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown account status: " + code);
    }
}
