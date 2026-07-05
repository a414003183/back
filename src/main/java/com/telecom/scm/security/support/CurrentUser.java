package com.telecom.scm.security.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在 Controller 方法参数上注入当前登录用户。
 *
 * <p>当 required=true（默认）时，如果用户未登录则抛出 403 异常。 当 required=false 时，未登录用户将注入 null，适用于公开接口需要可选登录状态的场景。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {

    /** 是否要求用户必须登录。默认 true。 设为 false 时，未登录用户将注入 null。 */
    boolean required() default true;
}
