package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OrderSourceEnum {
    WEB_MALL("WEB_MALL", "网页商城下单"),
    ANDROID_APP("ANDROID_APP", "安卓App下单");

    @EnumValue private final String code;
    private final String description;

    OrderSourceEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderSourceEnum fromCode(String code) {
        for (OrderSourceEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        throw new IllegalArgumentException("Unknown order source: " + code);
    }
}
