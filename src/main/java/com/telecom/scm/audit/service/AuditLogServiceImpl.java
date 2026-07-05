package com.telecom.scm.audit.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.audit.entity.LoginLogEntity;
import com.telecom.scm.audit.entity.OperationLogEntity;
import com.telecom.scm.audit.mapper.SysAuditMapper;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final int RESPONSE_MESSAGE_MAX_LENGTH = 255;

    private final SysAuditMapper sysAuditMapper;

    public AuditLogServiceImpl(SysAuditMapper sysAuditMapper) {
        this.sysAuditMapper = sysAuditMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordLogin(
            Long userId,
            String username,
            String ipAddress,
            String loginStatus,
            String loginMessage) {
        LoginLogEntity loginLog = new LoginLogEntity();
        loginLog.setUserId(userId);
        loginLog.setUsername(username);
        loginLog.setIpAddress(ipAddress);
        loginLog.setLoginStatus(loginStatus);
        loginLog.setLoginMessage(loginMessage);
        sysAuditMapper.insertLoginLog(loginLog);
        if (userId != null && "SUCCESS".equals(loginStatus)) {
            sysAuditMapper.updateLastLoginTime(userId, LocalDateTime.now());
        }
    }

    @Override
    public void recordOperation(
            Long userId,
            String username,
            String moduleName,
            String businessType,
            String requestUri,
            String requestMethod,
            String requestParams,
            String operationStatus,
            String responseMessage,
            String errorStack) {
        OperationLogEntity operationLog = new OperationLogEntity();
        operationLog.setUserId(userId);
        operationLog.setUsername(username == null || username.isBlank() ? "anonymous" : username);
        operationLog.setModuleName(moduleName);
        operationLog.setBusinessType(businessType);
        operationLog.setRequestUri(requestUri);
        operationLog.setRequestMethod(requestMethod);
        operationLog.setRequestParams(requestParams);
        operationLog.setOperationStatus(operationStatus);
        operationLog.setResponseMessage(truncate(responseMessage, RESPONSE_MESSAGE_MAX_LENGTH));
        operationLog.setErrorStack(errorStack);
        sysAuditMapper.insertOperationLog(operationLog);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
