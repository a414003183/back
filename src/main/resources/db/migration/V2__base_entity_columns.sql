-- 为所有业务表补充 BaseEntity 公共字段
-- 适用于已引入 com.telecom.scm.common.base.BaseEntity 的项目
-- 执行前请确认表名与实际 schema 一致

DELIMITER $$

DROP PROCEDURE IF EXISTS add_column_if_missing$$

CREATE PROCEDURE add_column_if_missing(
    IN p_table VARCHAR(100),
    IN p_column VARCHAR(100),
    IN p_definition TEXT
)
BEGIN
    SET @sql = NULL;
    SELECT CONCAT('ALTER TABLE ', p_table, ' ADD COLUMN ', p_column, ' ', p_definition)
    INTO @sql
    FROM DUAL
    WHERE NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table
          AND COLUMN_NAME = p_column
    );

    IF @sql IS NOT NULL THEN
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

-- aftersale_audit_log
CALL add_column_if_missing('aftersale_audit_log', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('aftersale_audit_log', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('aftersale_audit_log', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('aftersale_audit_log', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('aftersale_audit_log', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('aftersale_audit_log', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('aftersale_audit_log', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- aftersale_info
CALL add_column_if_missing('aftersale_info', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('aftersale_info', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('aftersale_info', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('aftersale_info', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('aftersale_info', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('aftersale_info', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('aftersale_info', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- aftersale_item
CALL add_column_if_missing('aftersale_item', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('aftersale_item', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('aftersale_item', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('aftersale_item', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('aftersale_item', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('aftersale_item', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('aftersale_item', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- product_brand
CALL add_column_if_missing('product_brand', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('product_brand', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('product_brand', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('product_brand', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('product_brand', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('product_brand', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('product_brand', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- product_category
CALL add_column_if_missing('product_category', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('product_category', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('product_category', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('product_category', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('product_category', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('product_category', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('product_category', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- customer_info
CALL add_column_if_missing('customer_info', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('customer_info', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('customer_info', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('customer_info', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('customer_info', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('customer_info', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('customer_info', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- customer_level_config
CALL add_column_if_missing('customer_level_config', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('customer_level_config', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('customer_level_config', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('customer_level_config', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('customer_level_config', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('customer_level_config', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('customer_level_config', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- customer_price_rule
CALL add_column_if_missing('customer_price_rule', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('customer_price_rule', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('customer_price_rule', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('customer_price_rule', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('customer_price_rule', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('customer_price_rule', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('customer_price_rule', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- file_storage
CALL add_column_if_missing('file_storage', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('file_storage', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('file_storage', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('file_storage', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('file_storage', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('file_storage', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('file_storage', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- goods_auth_rule
CALL add_column_if_missing('goods_auth_rule', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('goods_auth_rule', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('goods_auth_rule', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('goods_auth_rule', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('goods_auth_rule', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('goods_auth_rule', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('goods_auth_rule', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- level_discount_rule
CALL add_column_if_missing('level_discount_rule', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('level_discount_rule', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('level_discount_rule', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('level_discount_rule', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('level_discount_rule', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('level_discount_rule', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('level_discount_rule', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- sys_login_log
CALL add_column_if_missing('sys_login_log', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('sys_login_log', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('sys_login_log', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('sys_login_log', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('sys_login_log', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('sys_login_log', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('sys_login_log', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- member_account
CALL add_column_if_missing('member_account', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('member_account', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('member_account', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('member_account', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('member_account', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('member_account', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('member_account', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- member_address
CALL add_column_if_missing('member_address', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('member_address', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('member_address', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('member_address', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('member_address', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('member_address', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('member_address', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- merchant_goods
CALL add_column_if_missing('merchant_goods', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('merchant_goods', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('merchant_goods', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('merchant_goods', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('merchant_goods', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('merchant_goods', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('merchant_goods', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- merchant_info
CALL add_column_if_missing('merchant_info', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('merchant_info', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('merchant_info', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('merchant_info', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('merchant_info', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('merchant_info', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('merchant_info', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- merchant_supplier_relation
CALL add_column_if_missing('merchant_supplier_relation', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('merchant_supplier_relation', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('merchant_supplier_relation', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('merchant_supplier_relation', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('merchant_supplier_relation', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('merchant_supplier_relation', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('merchant_supplier_relation', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- sys_operation_log
CALL add_column_if_missing('sys_operation_log', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('sys_operation_log', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('sys_operation_log', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('sys_operation_log', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('sys_operation_log', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('sys_operation_log', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('sys_operation_log', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- order_info
CALL add_column_if_missing('order_info', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('order_info', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('order_info', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('order_info', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('order_info', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('order_info', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('order_info', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- order_item
CALL add_column_if_missing('order_item', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('order_item', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('order_item', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('order_item', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('order_item', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('order_item', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('order_item', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- order_item_adjust_log
CALL add_column_if_missing('order_item_adjust_log', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('order_item_adjust_log', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('order_item_adjust_log', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('order_item_adjust_log', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('order_item_adjust_log', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('order_item_adjust_log', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('order_item_adjust_log', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- order_status_log
CALL add_column_if_missing('order_status_log', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('order_status_log', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('order_status_log', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('order_status_log', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('order_status_log', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('order_status_log', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('order_status_log', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- payment_record
CALL add_column_if_missing('payment_record', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('payment_record', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('payment_record', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('payment_record', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('payment_record', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('payment_record', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('payment_record', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- point_account
CALL add_column_if_missing('point_account', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('point_account', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('point_account', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('point_account', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('point_account', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('point_account', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('point_account', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- point_record
CALL add_column_if_missing('point_record', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('point_record', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('point_record', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('point_record', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('point_record', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('point_record', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('point_record', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- point_rule
CALL add_column_if_missing('point_rule', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('point_rule', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('point_rule', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('point_rule', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('point_rule', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('point_rule', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('point_rule', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- product_sku
CALL add_column_if_missing('product_sku', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('product_sku', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('product_sku', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('product_sku', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('product_sku', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('product_sku', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('product_sku', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- product_spu
CALL add_column_if_missing('product_spu', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('product_spu', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('product_spu', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('product_spu', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('product_spu', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('product_spu', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('product_spu', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- product_stock_log
CALL add_column_if_missing('product_stock_log', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('product_stock_log', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('product_stock_log', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('product_stock_log', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('product_stock_log', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('product_stock_log', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('product_stock_log', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- shipment_info
CALL add_column_if_missing('shipment_info', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('shipment_info', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('shipment_info', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('shipment_info', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('shipment_info', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('shipment_info', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('shipment_info', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- supplier_goods_authorization
CALL add_column_if_missing('supplier_goods_authorization', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('supplier_goods_authorization', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('supplier_goods_authorization', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('supplier_goods_authorization', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('supplier_goods_authorization', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('supplier_goods_authorization', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('supplier_goods_authorization', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- supplier_info
CALL add_column_if_missing('supplier_info', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('supplier_info', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('supplier_info', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('supplier_info', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('supplier_info', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('supplier_info', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('supplier_info', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- sys_menu
CALL add_column_if_missing('sys_menu', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('sys_menu', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('sys_menu', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('sys_menu', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('sys_menu', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('sys_menu', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('sys_menu', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- sys_user
CALL add_column_if_missing('sys_user', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('sys_user', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('sys_user', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('sys_user', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('sys_user', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('sys_user', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('sys_user', 'remark', 'VARCHAR(500) COMMENT ''备注''');

-- user_identity_binding
CALL add_column_if_missing('user_identity_binding', 'created_by', 'BIGINT COMMENT ''创建人 ID''');
CALL add_column_if_missing('user_identity_binding', 'updated_by', 'BIGINT COMMENT ''更新人 ID''');
CALL add_column_if_missing('user_identity_binding', 'created_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''');
CALL add_column_if_missing('user_identity_binding', 'updated_time', 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''');
CALL add_column_if_missing('user_identity_binding', 'deleted', 'TINYINT NOT NULL DEFAULT 0 COMMENT ''是否删除：0 否，1 是''');
CALL add_column_if_missing('user_identity_binding', 'version', 'INT NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_missing('user_identity_binding', 'remark', 'VARCHAR(500) COMMENT ''备注''');

DROP PROCEDURE IF EXISTS add_column_if_missing;
