package com.telecom.scm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum DataScopeEnum {
    ALL("ALL", "全部数据"),
    CUSTOM("CUSTOM", "自定义数据"),
    DEPT_ONLY("DEPT_ONLY", "本部门数据"),
    DEPT_AND_CHILD("DEPT_AND_CHILD", "本部门及以下数据");

    @EnumValue private final String code;
    private final String description;

    DataScopeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DataScopeEnum fromCode(String code) {
        for (DataScopeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown data scope: " + code);
    }
}
