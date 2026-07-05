package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum AftersaleStatusEnum {
    NONE("NONE", "无售后"),
    WAIT_AUDIT("WAIT_AUDIT", "待审核"),
    WAIT_RETURN("WAIT_RETURN", "待退货"),
    WAIT_RECEIVE("WAIT_RECEIVE", "待收货"),
    WAIT_REFUND("WAIT_REFUND", "待退款"),
    FINISHED("FINISHED", "已完成"),
    REJECTED("REJECTED", "已驳回");

    @EnumValue private final String code;
    private final String description;

    AftersaleStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AftersaleStatusEnum fromCode(String code) {
        for (AftersaleStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown aftersale status: " + code);
    }
}
