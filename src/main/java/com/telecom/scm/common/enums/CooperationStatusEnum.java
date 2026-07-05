package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum CooperationStatusEnum {
    ACTIVE("ACTIVE", "合作中"),
    ENDED("ENDED", "已结束");

    @EnumValue private final String code;
    private final String description;

    CooperationStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CooperationStatusEnum fromCode(String code) {
        for (CooperationStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown cooperation status: " + code);
    }
}
