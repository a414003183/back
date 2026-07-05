package com.telecom.scm.security.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserIdentitySchemaBootstrapService implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public UserIdentitySchemaBootstrapService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureSysUserColumns();
        ensureUserIdentityBindingTable();
        backfillExistingIdentityBindings();
        backfillUserIdentityState();
    }

    private void ensureSysUserColumns() {
        ensureColumn(
                "sys_user",
                "register_source",
                """
            ALTER TABLE sys_user
            ADD COLUMN register_source VARCHAR(30) NULL COMMENT '注册来源'
            AFTER email
            """);
        ensureColumn(
                "sys_user",
                "register_status",
                """
            ALTER TABLE sys_user
            ADD COLUMN register_status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '注册状态'
            AFTER register_source
            """);
        ensureColumn(
                "sys_user",
                "last_active_identity_type",
                """
            ALTER TABLE sys_user
            ADD COLUMN last_active_identity_type VARCHAR(20) NULL COMMENT '最近活跃身份'
            AFTER register_status
            """);
    }

    private void ensureUserIdentityBindingTable() {
        jdbcTemplate.execute(
                """
            CREATE TABLE IF NOT EXISTS user_identity_binding (
              id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
              user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
              identity_type VARCHAR(20) NOT NULL COMMENT '身份类型',
              identity_ref_id BIGINT UNSIGNED NOT NULL COMMENT '身份主体ID',
              status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '绑定状态',
              is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认身份',
              created_by BIGINT UNSIGNED NULL COMMENT '创建人',
              created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              updated_by BIGINT UNSIGNED NULL COMMENT '更新人',
              updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
              PRIMARY KEY (id),
              UNIQUE KEY uk_user_identity_binding (user_id, identity_type, identity_ref_id),
              KEY idx_user_identity_binding_status (status),
              KEY idx_user_identity_binding_user (user_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户身份绑定表'
            """);
    }

    private void backfillExistingIdentityBindings() {
        jdbcTemplate.update(
                """
            INSERT INTO user_identity_binding (
                user_id,
                identity_type,
                identity_ref_id,
                status,
                is_default,
                created_by,
                updated_by,
                deleted
            )
            SELECT
                u.id,
                r.role_code,
                u.member_id,
                'ENABLED',
                1,
                COALESCE(u.created_by, u.id),
                COALESCE(u.updated_by, u.id),
                0
            FROM sys_user u
            JOIN sys_user_role ur ON ur.user_id = u.id
            JOIN sys_role r ON r.id = ur.role_id
                AND r.deleted = 0
                AND r.role_code IN ('CUSTOMER', 'MERCHANT', 'SUPPLIER')
            WHERE u.deleted = 0
                AND u.member_id IS NOT NULL
                AND NOT EXISTS (
                    SELECT 1
                    FROM user_identity_binding binding
                    WHERE binding.user_id = u.id
                        AND binding.identity_type = r.role_code
                        AND binding.identity_ref_id = u.member_id
                )
            """);
    }

    private void backfillUserIdentityState() {
        jdbcTemplate.update(
                """
            UPDATE sys_user
            SET register_source = COALESCE(register_source, 'SYSTEM_SEED')
            WHERE deleted = 0
            """);

        jdbcTemplate.update(
                """
            UPDATE sys_user u
            SET u.last_active_identity_type = (
                SELECT binding.identity_type
                FROM user_identity_binding binding
                WHERE binding.deleted = 0
                    AND binding.user_id = u.id
                    AND binding.status = 'ENABLED'
                ORDER BY binding.is_default DESC, binding.id ASC
                LIMIT 1
            )
            WHERE u.deleted = 0
                AND (u.last_active_identity_type IS NULL OR u.last_active_identity_type = '')
            """);

        jdbcTemplate.update(
                """
            UPDATE sys_user u
            SET u.register_status = CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM user_identity_binding binding
                    WHERE binding.deleted = 0
                        AND binding.user_id = u.id
                        AND binding.status = 'PENDING'
                ) THEN 'PENDING'
                WHEN EXISTS (
                    SELECT 1
                    FROM user_identity_binding binding
                    WHERE binding.deleted = 0
                        AND binding.user_id = u.id
                        AND binding.status = 'ENABLED'
                ) THEN 'ENABLED'
                WHEN EXISTS (
                    SELECT 1
                    FROM user_identity_binding binding
                    WHERE binding.deleted = 0
                        AND binding.user_id = u.id
                        AND binding.status = 'REJECTED'
                ) THEN 'REJECTED'
                WHEN EXISTS (
                    SELECT 1
                    FROM sys_user_role ur
                    JOIN sys_role role ON role.id = ur.role_id
                        AND role.deleted = 0
                        AND role.status = 'ENABLED'
                        AND role.role_code = 'ADMIN'
                    WHERE ur.user_id = u.id
                ) THEN 'ENABLED'
                ELSE 'DISABLED'
            END
            WHERE u.deleted = 0
            """);
    }

    private void ensureColumn(String tableName, String columnName, String alterSql) {
        Integer count =
                jdbcTemplate.queryForObject(
                        """
            SELECT COUNT(1)
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
                AND table_name = ?
                AND column_name = ?
            """,
                        Integer.class,
                        tableName,
                        columnName);
        if (count == null || count == 0) {
            jdbcTemplate.execute(alterSql);
        }
    }
}
