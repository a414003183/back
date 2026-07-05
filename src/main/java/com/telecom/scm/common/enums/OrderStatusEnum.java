package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OrderStatusEnum {
    WAIT_PAY("WAIT_PAY", "待支付"),
    PENDING_AUDIT("PENDING_AUDIT", "待审核"),
    WAIT_SHIP("WAIT_SHIP", "待发货"),
    WAIT_RECEIVE("WAIT_RECEIVE", "待收货"),
    FINISHED("FINISHED", "已完成");

    @EnumValue private final String code;
    private final String description;

    OrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatusEnum fromCode(String code) {
        for (OrderStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + code);
    }
}
