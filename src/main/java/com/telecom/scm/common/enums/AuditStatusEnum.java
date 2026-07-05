package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum AuditStatusEnum {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已驳回");

    @EnumValue private final String code;
    private final String description;

    AuditStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AuditStatusEnum fromCode(String code) {
        for (AuditStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown audit status: " + code);
    }
}
