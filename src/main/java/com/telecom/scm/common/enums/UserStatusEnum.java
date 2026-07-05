package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum UserStatusEnum {
    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "禁用"),
    LOCKED("LOCKED", "锁定"),
    DELETED("DELETED", "已删除");

    @EnumValue private final String code;
    private final String description;

    UserStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserStatusEnum fromCode(String code) {
        for (UserStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown user status: " + code);
    }
}
