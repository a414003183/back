package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum YesNoEnum {
    YES("Y", "是"),
    NO("N", "否");

    @EnumValue private final String code;
    private final String description;

    YesNoEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static YesNoEnum fromCode(String code) {
        for (YesNoEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown yes/no flag: " + code);
    }
}
