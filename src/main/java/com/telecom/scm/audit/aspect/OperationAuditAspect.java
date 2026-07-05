package com.telecom.scm.audit.aspect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.telecom.scm.audit.annotation.OperationAudit;
import com.telecom.scm.audit.service.AuditLogService;
import com.telecom.scm.security.model.AuthenticatedUser;

@Aspect
@Component
public class OperationAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationAuditAspect.class);

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public OperationAuditAspect(AuditLogService auditLogService, ObjectMapper objectMapper) {
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(operationAudit)")
    public Object around(ProceedingJoinPoint joinPoint, OperationAudit operationAudit)
            throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();
        AuditActor actor = resolveActor(joinPoint.getArgs());
        String requestParams = serializeArgs(joinPoint);
        String requestUri = request == null ? "" : request.getRequestURI();
        String requestMethod = request == null ? "" : request.getMethod();

        try {
            Object result = joinPoint.proceed();
            recordOperationSafely(
                    actor.userId(),
                    actor.username(),
                    operationAudit.module(),
                    operationAudit.businessType(),
                    requestUri,
                    requestMethod,
                    requestParams,
                    "SUCCESS",
                    "ok",
                    null);
            return result;
        } catch (Throwable throwable) {
            recordOperationSafely(
                    actor.userId(),
                    actor.username(),
                    operationAudit.module(),
                    operationAudit.businessType(),
                    requestUri,
                    requestMethod,
                    requestParams,
                    "FAIL",
                    throwable.getMessage(),
                    getStackTrace(throwable));
            throw throwable;
        }
    }

    private void recordOperationSafely(
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
        try {
            auditLogService.recordOperation(
                    userId,
                    username,
                    moduleName,
                    businessType,
                    requestUri,
                    requestMethod,
                    requestParams,
                    operationStatus,
                    responseMessage,
                    errorStack);
        } catch (Exception auditException) {
            log.warn(
                    "operation audit persistence failed: {} {}",
                    requestMethod,
                    requestUri,
                    auditException);
        }
    }

    private AuditActor resolveActor(Object[] args) {
        return Arrays.stream(args)
                .filter(Authentication.class::isInstance)
                .map(Authentication.class::cast)
                .map(Authentication::getPrincipal)
                .filter(AuthenticatedUser.class::isInstance)
                .map(AuthenticatedUser.class::cast)
                .findFirst()
                .map(user -> new AuditActor(user.userId(), user.username()))
                .orElse(new AuditActor(null, "anonymous"));
    }

    private String serializeArgs(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> payload = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null || arg instanceof Authentication) {
                continue;
            }
            payload.put(
                    parameterNames != null && i < parameterNames.length
                            ? parameterNames[i]
                            : "arg" + i,
                    arg);
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            return "{}";
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.toString();
        return stackTrace.length() > 4000 ? stackTrace.substring(0, 4000) : stackTrace;
    }

    private record AuditActor(Long userId, String username) {}
}
