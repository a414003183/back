package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum MemberTypeEnum {
    ADMIN("ADMIN", "管理员"),
    MERCHANT("MERCHANT", "商家"),
    SUPPLIER("SUPPLIER", "供应商"),
    CUSTOMER("CUSTOMER", "客户"),
    PLATFORM("PLATFORM", "平台");

    @EnumValue private final String code;
    private final String description;

    MemberTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MemberTypeEnum fromCode(String code) {
        for (MemberTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown member type: " + code);
    }
}
