package com.telecom.scm.member.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SupplyCollaborationSchemaBootstrapService implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public SupplyCollaborationSchemaBootstrapService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureMerchantSupplierRelationTable();
        ensureSupplierGoodsAuthorizationTable();
        backfillRelationsFromMerchantGoods();
        backfillAuthorizationsFromMerchantGoods();
    }

    private void ensureMerchantSupplierRelationTable() {
        jdbcTemplate.execute(
                """
            CREATE TABLE IF NOT EXISTS merchant_supplier_relation (
              id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
              merchant_id BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
              supplier_id BIGINT UNSIGNED NOT NULL COMMENT '供应商ID',
              status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '合作状态：ACTIVE/ENDED',
              cooperation_start_at DATETIME NULL COMMENT '合作开始时间',
              cooperation_end_at DATETIME NULL COMMENT '合作结束时间',
              remark VARCHAR(255) NULL COMMENT '备注',
              created_by BIGINT UNSIGNED NULL COMMENT '创建人',
              created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              updated_by BIGINT UNSIGNED NULL COMMENT '更新人',
              updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
              PRIMARY KEY (id),
              UNIQUE KEY uk_merchant_supplier_relation (merchant_id, supplier_id),
              KEY idx_merchant_supplier_relation_supplier (supplier_id),
              KEY idx_merchant_supplier_relation_status (status)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家供应商合作关系表'
            """);
    }

    private void ensureSupplierGoodsAuthorizationTable() {
        jdbcTemplate.execute(
                """
            CREATE TABLE IF NOT EXISTS supplier_goods_authorization (
              id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
              supplier_id BIGINT UNSIGNED NOT NULL COMMENT '供应商ID',
              merchant_id BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
              supplier_sku_id BIGINT UNSIGNED NOT NULL COMMENT '供应商SKU ID',
              auth_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '授权状态：ACTIVE/REVOKED',
              authorized_price DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '授权成本价',
              authorized_at DATETIME NULL COMMENT '授权时间',
              revoked_at DATETIME NULL COMMENT '撤销时间',
              remark VARCHAR(255) NULL COMMENT '备注',
              created_by BIGINT UNSIGNED NULL COMMENT '创建人',
              created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              updated_by BIGINT UNSIGNED NULL COMMENT '更新人',
              updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
              PRIMARY KEY (id),
              UNIQUE KEY uk_supplier_goods_authorization (supplier_id, merchant_id, supplier_sku_id),
              KEY idx_supplier_goods_authorization_merchant (merchant_id),
              KEY idx_supplier_goods_authorization_status (auth_status)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商商品授权表'
            """);
    }

    private void backfillRelationsFromMerchantGoods() {
        jdbcTemplate.update(
                """
            INSERT INTO merchant_supplier_relation (
                merchant_id,
                supplier_id,
                status,
                cooperation_start_at,
                remark,
                created_by,
                updated_by,
                deleted
            )
            SELECT DISTINCT
                mg.merchant_id,
                mg.supplier_id,
                'ACTIVE',
                COALESCE(mg.created_time, NOW()),
                'backfilled from merchant_goods',
                COALESCE(mg.created_by, 1),
                COALESCE(mg.updated_by, COALESCE(mg.created_by, 1)),
                0
            FROM merchant_goods mg
            WHERE mg.deleted = 0
                AND mg.supplier_id IS NOT NULL
                AND NOT EXISTS (
                    SELECT 1
                    FROM merchant_supplier_relation relation_row
                    WHERE relation_row.deleted = 0
                        AND relation_row.merchant_id = mg.merchant_id
                        AND relation_row.supplier_id = mg.supplier_id
                )
            """);
    }

    private void backfillAuthorizationsFromMerchantGoods() {
        jdbcTemplate.update(
                """
            INSERT INTO supplier_goods_authorization (
                supplier_id,
                merchant_id,
                supplier_sku_id,
                auth_status,
                authorized_price,
                authorized_at,
                remark,
                created_by,
                updated_by,
                deleted
            )
            SELECT DISTINCT
                mg.supplier_id,
                mg.merchant_id,
                mg.sku_id,
                'ACTIVE',
                COALESCE(mg.current_cost_price, sku.cost_price, 0.00),
                COALESCE(mg.created_time, NOW()),
                'backfilled from merchant_goods',
                COALESCE(mg.created_by, 1),
                COALESCE(mg.updated_by, COALESCE(mg.created_by, 1)),
                0
            FROM merchant_goods mg
            LEFT JOIN product_sku sku ON sku.id = mg.sku_id
                AND sku.deleted = 0
            WHERE mg.deleted = 0
                AND mg.supplier_id IS NOT NULL
                AND NOT EXISTS (
                    SELECT 1
                    FROM supplier_goods_authorization auth_row
                    WHERE auth_row.deleted = 0
                        AND auth_row.supplier_id = mg.supplier_id
                        AND auth_row.merchant_id = mg.merchant_id
                        AND auth_row.supplier_sku_id = mg.sku_id
                )
            """);
    }
}
