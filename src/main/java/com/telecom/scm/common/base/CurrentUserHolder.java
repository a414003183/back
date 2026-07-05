package com.telecom.scm.common.base;

/**
 * 当前登录用户 ID holder。
 *
 * <p>用于在 MyBatis-Plus 自动填充 createdBy / updatedBy 时获取当前操作人。
 *
 * <p>实际项目中通常由拦截器或 AOP 在请求开始时 set，请求结束时 clear。
 */
public final class CurrentUserHolder {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    private CurrentUserHolder() {}

    public static void setUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long getUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
    }
}
