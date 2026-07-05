package com.telecom.scm.audit.service;

public interface AuditLogService {

    void recordLogin(
            Long userId,
            String username,
            String ipAddress,
            String loginStatus,
            String loginMessage);

    void recordOperation(
            Long userId,
            String username,
            String moduleName,
            String businessType,
            String requestUri,
            String requestMethod,
            String requestParams,
            String operationStatus,
            String responseMessage,
            String errorStack);
}
