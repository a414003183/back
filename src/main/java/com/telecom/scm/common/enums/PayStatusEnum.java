package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum PayStatusEnum {
    UNPAID("UNPAID", "未支付"),
    PAID_REGISTERED("PAID_REGISTERED", "已登记支付"),
    PAID("PAID", "已支付");

    @EnumValue private final String code;
    private final String description;

    PayStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PayStatusEnum fromCode(String code) {
        for (PayStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown pay status: " + code);
    }
}
