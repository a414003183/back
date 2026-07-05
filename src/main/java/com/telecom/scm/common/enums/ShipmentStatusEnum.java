package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ShipmentStatusEnum {
    WAIT_SHIP("WAIT_SHIP", "待发货"),
    SHIPPED("SHIPPED", "已发货"),
    SIGNED("SIGNED", "已签收");

    @EnumValue private final String code;
    private final String description;

    ShipmentStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShipmentStatusEnum fromCode(String code) {
        for (ShipmentStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown shipment status: " + code);
    }
}
