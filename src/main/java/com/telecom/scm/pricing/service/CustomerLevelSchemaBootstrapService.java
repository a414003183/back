package com.telecom.scm.pricing.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerLevelSchemaBootstrapService implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public CustomerLevelSchemaBootstrapService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureCustomerInfoColumns();
        ensureCustomerLevelConfigTable();
        seedDefaultLevels();
        backfillAccumulatedPaidAmount();
    }

    private void ensureCustomerInfoColumns() {
        ensureColumn(
                "customer_info",
                "accumulated_paid_amount",
                """
            ALTER TABLE customer_info
            ADD COLUMN accumulated_paid_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '累计完成实付金额'
            AFTER member_level
            """);
        ensureColumn(
                "customer_info",
                "last_level_upgrade_at",
                """
            ALTER TABLE customer_info
            ADD COLUMN last_level_upgrade_at DATETIME NULL COMMENT '最近升级时间'
            AFTER accumulated_paid_amount
            """);
    }

    private void ensureCustomerLevelConfigTable() {
        jdbcTemplate.execute(
                """
            CREATE TABLE IF NOT EXISTS customer_level_config (
              id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
              level_code VARCHAR(20) NOT NULL COMMENT '等级编码',
              level_name VARCHAR(50) NOT NULL COMMENT '等级名称',
              upgrade_threshold_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '升级阈值金额',
              discount_scope VARCHAR(30) NULL COMMENT '折扣范围(已废弃)',
              discount_value DECIMAL(10, 2) NULL COMMENT '折扣值(已废弃)',
              sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
              status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
              remark VARCHAR(255) NULL COMMENT '备注',
              created_by BIGINT UNSIGNED NULL COMMENT '创建人',
              created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              updated_by BIGINT UNSIGNED NULL COMMENT '更新人',
              updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
              PRIMARY KEY (id),
              UNIQUE KEY uk_customer_level_config_code (level_code),
              KEY idx_customer_level_config_status (status),
              KEY idx_customer_level_config_threshold (upgrade_threshold_amount)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户等级配置表'
            """);
    }

    private void seedDefaultLevels() {
        jdbcTemplate.update(
                """
            INSERT IGNORE INTO customer_level_config (
                level_code, level_name, upgrade_threshold_amount,
                sort_no, status, remark, created_by, updated_by, deleted
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
            """,
                "NORMAL",
                "普通会员",
                0.00,
                1,
                "ENABLED",
                "default seeded level",
                1L,
                1L);
        jdbcTemplate.update(
                """
            INSERT IGNORE INTO customer_level_config (
                level_code, level_name, upgrade_threshold_amount,
                sort_no, status, remark, created_by, updated_by, deleted
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
            """,
                "GOLD",
                "金牌会员",
                1000.00,
                2,
                "ENABLED",
                "default seeded level",
                1L,
                1L);
        jdbcTemplate.update(
                """
            INSERT IGNORE INTO customer_level_config (
                level_code, level_name, upgrade_threshold_amount,
                sort_no, status, remark, created_by, updated_by, deleted
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
            """,
                "PROJECT",
                "项目会员",
                5000.00,
                3,
                "ENABLED",
                "default seeded level",
                1L,
                1L);
    }

    private void backfillAccumulatedPaidAmount() {
        jdbcTemplate.update(
                """
            UPDATE customer_info customer
            SET customer.accumulated_paid_amount = COALESCE((
                SELECT SUM(order_info.pay_amount)
                FROM order_info order_info
                WHERE order_info.deleted = 0
                    AND order_info.customer_id = customer.id
                    AND order_info.order_status = 'FINISHED'
            ), 0.00)
            WHERE customer.deleted = 0
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
