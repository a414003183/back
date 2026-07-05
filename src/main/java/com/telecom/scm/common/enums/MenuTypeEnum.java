package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum MenuTypeEnum {
    MENU("MENU", "菜单"),
    BUTTON("BUTTON", "按钮"),
    CATALOG("CATALOG", "目录"),
    IFRAME("IFRAME", "内嵌页面"),
    LINK("LINK", "外链");

    @EnumValue private final String code;
    private final String description;

    MenuTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MenuTypeEnum fromCode(String code) {
        for (MenuTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown menu type: " + code);
    }
}
