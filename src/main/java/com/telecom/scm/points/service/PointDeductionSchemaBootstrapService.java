package com.telecom.scm.points.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PointDeductionSchemaBootstrapService implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public PointDeductionSchemaBootstrapService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensurePointRuleColumns();
        ensureOrderInfoColumns();
        seedDeductionRule();
    }

    private void ensurePointRuleColumns() {
        ensureColumn(
                "point_rule",
                "deduction_ratio",
                """
            ALTER TABLE point_rule
            ADD COLUMN deduction_ratio DECIMAL(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '每积分可抵扣金额'
            AFTER rule_value
            """);
        ensureColumn(
                "point_rule",
                "max_deduction_ratio",
                """
            ALTER TABLE point_rule
            ADD COLUMN max_deduction_ratio DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单最高抵扣比例'
            AFTER deduction_ratio
            """);
        ensureColumn(
                "point_rule",
                "is_deduction_enabled",
                """
            ALTER TABLE point_rule
            ADD COLUMN is_deduction_enabled TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否启用积分抵现'
            AFTER max_deduction_ratio
            """);
    }

    private void ensureOrderInfoColumns() {
        ensureColumn(
                "order_info",
                "used_points",
                """
            ALTER TABLE order_info
            ADD COLUMN used_points INT NOT NULL DEFAULT 0 COMMENT '下单抵扣使用的积分'
            AFTER discount_amount
            """);
        ensureColumn(
                "order_info",
                "points_deduction_amount",
                """
            ALTER TABLE order_info
            ADD COLUMN points_deduction_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵现金额'
            AFTER used_points
            """);
    }

    private void seedDeductionRule() {
        Integer count =
                jdbcTemplate.queryForObject(
                        """
            SELECT COUNT(1)
            FROM point_rule
            WHERE deleted = 0
                AND rule_type = 'ORDER_DEDUCTION'
            """,
                        Integer.class);
        if (count != null && count > 0) {
            return;
        }

        jdbcTemplate.update(
                """
            INSERT INTO point_rule (
                rule_name,
                rule_type,
                value_type,
                rule_value,
                deduction_ratio,
                max_deduction_ratio,
                is_deduction_enabled,
                status,
                remark,
                created_by,
                updated_by,
                deleted
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
            """,
                "积分抵现",
                "ORDER_DEDUCTION",
                "RATE",
                0.00,
                0.10,
                20.00,
                1,
                "ENABLED",
                "default seeded deduction rule",
                1L,
                1L);
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
