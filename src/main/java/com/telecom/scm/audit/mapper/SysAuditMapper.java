package com.telecom.scm.audit.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.audit.entity.LoginLogEntity;
import com.telecom.scm.audit.entity.OperationLogEntity;

@Mapper
public interface SysAuditMapper {

    @Insert(
            """
        INSERT INTO sys_login_log (
            user_id,
            username,
            ip_address,
            login_status,
            login_message,
            login_time
        ) VALUES (
            #{userId},
            #{username},
            #{ipAddress},
            #{loginStatus},
            #{loginMessage},
            NOW()
        )
        """)
    int insertLoginLog(LoginLogEntity loginLog);

    @Insert(
            """
        INSERT INTO sys_operation_log (
            user_id,
            username,
            module_name,
            business_type,
            request_uri,
            request_method,
            request_params,
            operation_status,
            response_message,
            error_stack,
            operation_time
        ) VALUES (
            #{userId},
            #{username},
            #{moduleName},
            #{businessType},
            #{requestUri},
            #{requestMethod},
            #{requestParams},
            #{operationStatus},
            #{responseMessage},
            #{errorStack},
            NOW()
        )
        """)
    int insertOperationLog(OperationLogEntity operationLog);

    @Update(
            """
        UPDATE sys_user
        SET last_login_time = #{lastLoginTime},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{userId}
            AND deleted = 0
        """)
    int updateLastLoginTime(
            @Param("userId") Long userId, @Param("lastLoginTime") LocalDateTime lastLoginTime);
}
