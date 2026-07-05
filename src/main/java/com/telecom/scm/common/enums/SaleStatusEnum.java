package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SaleStatusEnum {
    ON("ON", "上架"),
    OFF("OFF", "下架");

    @EnumValue private final String code;
    private final String description;

    SaleStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SaleStatusEnum fromCode(String code) {
        for (SaleStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown sale status: " + code);
    }
}
