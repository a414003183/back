package com.telecom.scm.security.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.telecom.scm.common.enums.MemberTypeEnum;

@Service
public class SeedDataBootstrapService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataBootstrapService.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public SeedDataBootstrapService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 检查是否已有测试数据
        Integer userCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM sys_user WHERE deleted = 0", Integer.class);

        if (userCount != null && userCount > 0) {
            log.info("Seed data already exists, skipping...");
            return;
        }

        log.info("Creating seed data...");
        createAdminUser();
        createMerchantUser();
        createSupplierUser();
        createCustomerUser();
        createSampleProducts();
        log.info("Seed data created successfully!");
    }

    private void createAdminUser() {
        // 创建管理员账号
        Long memberId =
                insertMemberAccount("platform_admin", "管理员", MemberTypeEnum.ADMIN.getCode());
        Long userId =
                insertSysUser(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "系统管理员",
                        memberId,
                        MemberTypeEnum.ADMIN.getCode());
        insertUserRole(userId, MemberTypeEnum.ADMIN.getCode());
        insertUserIdentityBinding(userId, MemberTypeEnum.ADMIN.getCode(), memberId);
        log.info("Admin user created: admin / admin123");
    }

    private void createMerchantUser() {
        // 创建商家账号
        Long memberId = insertMemberAccount("旗舰店", "商家", MemberTypeEnum.MERCHANT.getCode());
        Long userId =
                insertSysUser(
                        "merchant1",
                        passwordEncoder.encode("merchant123"),
                        "测试商家",
                        memberId,
                        MemberTypeEnum.MERCHANT.getCode());
        insertUserRole(userId, MemberTypeEnum.MERCHANT.getCode());
        insertUserIdentityBinding(userId, MemberTypeEnum.MERCHANT.getCode(), memberId);

        // 创建商家资料
        jdbcTemplate.update(
                """
            INSERT INTO merchant_info (member_id, shop_name, contact_name, contact_phone, status, created_by, updated_by, deleted)
            VALUES (?, ?, ?, ?, 'ENABLED', ?, ?, 0)
            """,
                memberId,
                "测试商家旗舰店",
                "张三",
                "13800138000",
                userId,
                userId);

        log.info("Merchant user created: merchant1 / merchant123");
    }

    private void createSupplierUser() {
        // 创建供应商账号
        Long memberId = insertMemberAccount("供应商", "供应商", MemberTypeEnum.SUPPLIER.getCode());
        Long userId =
                insertSysUser(
                        "supplier1",
                        passwordEncoder.encode("supplier123"),
                        "测试供应商",
                        memberId,
                        MemberTypeEnum.SUPPLIER.getCode());
        insertUserRole(userId, MemberTypeEnum.SUPPLIER.getCode());
        insertUserIdentityBinding(userId, MemberTypeEnum.SUPPLIER.getCode(), memberId);

        // 创建供应商资料
        jdbcTemplate.update(
                """
            INSERT INTO supplier_info (member_id, supplier_name, contact_name, contact_phone, status, created_by, updated_by, deleted)
            VALUES (?, ?, ?, ?, 'ENABLED', ?, ?, 0)
            """,
                memberId,
                "测试供应商有限公司",
                "李四",
                "13900139000",
                userId,
                userId);

        log.info("Supplier user created: supplier1 / supplier123");
    }

    private void createCustomerUser() {
        // 创建客户账号
        Long memberId = insertMemberAccount("采购商", "客户", MemberTypeEnum.CUSTOMER.getCode());
        Long userId =
                insertSysUser(
                        "customer1",
                        passwordEncoder.encode("customer123"),
                        "测试客户",
                        memberId,
                        MemberTypeEnum.CUSTOMER.getCode());
        insertUserRole(userId, MemberTypeEnum.CUSTOMER.getCode());
        insertUserIdentityBinding(userId, MemberTypeEnum.CUSTOMER.getCode(), memberId);

        // 创建客户资料
        jdbcTemplate.update(
                """
            INSERT INTO customer_info (member_id, company_name, contact_name, contact_phone, status, created_by, updated_by, deleted)
            VALUES (?, ?, ?, ?, 'ENABLED', ?, ?, 0)
            """,
                memberId,
                "测试采购公司",
                "王五",
                "13700137000",
                userId,
                userId);

        // 创建客户等级
        insertCustomerLevel(memberId);

        log.info("Customer user created: customer1 / customer123");
    }

    private void insertCustomerLevel(Long memberId) {
        // 确保有客户等级配置（使用新表结构，不插入折扣值）
        Integer levelCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM customer_level_config WHERE deleted = 0",
                        Integer.class);

        if (levelCount == null || levelCount == 0) {
            jdbcTemplate.update(
                    """
                INSERT INTO customer_level_config (level_name, level_code, upgrade_threshold_amount, sort_no, status, created_by, updated_by, deleted)
                VALUES ('普通会员', 'NORMAL', 0.00, 1, 'ENABLED', 1, 1, 0)
                """);
            jdbcTemplate.update(
                    """
                INSERT INTO customer_level_config (level_name, level_code, upgrade_threshold_amount, sort_no, status, created_by, updated_by, deleted)
                VALUES ('VIP会员', 'VIP', 1000.00, 2, 'ENABLED', 1, 1, 0)
                """);
        }

        // 设置默认会员等级（使用 member_level 字段）
        jdbcTemplate.update(
                """
            UPDATE customer_info SET member_level = 'NORMAL' WHERE member_id = ?
            """,
                memberId);
    }

    private void createSampleProducts() {
        // 获取商家ID
        Integer merchantMemberId =
                jdbcTemplate.queryForObject(
                        """
            SELECT id FROM member_account WHERE member_type = 'MERCHANT' AND deleted = 0 LIMIT 1
            """,
                        Integer.class);

        if (merchantMemberId == null) return;

        // 获取供应商ID
        Integer supplierMemberId =
                jdbcTemplate.queryForObject(
                        """
            SELECT id FROM member_account WHERE member_type = 'SUPPLIER' AND deleted = 0 LIMIT 1
            """,
                        Integer.class);

        // 创建商品分类
        Long categoryId = insertProductCategory("手机数码", "PHONE_DIGITAL");
        Long categoryId2 = insertProductCategory("电脑办公", "COMPUTER_OFFICE");

        // 创建商品品牌
        Long brandId = insertProductBrand("Apple", "APPLE");
        Long brandId2 = insertProductBrand("联想", "LENOVO");

        // 创建商品 SPU
        Long supplierId = supplierMemberId != null ? supplierMemberId.longValue() : null;
        Long productId = insertProductSpu("iPhone 16 Pro", "IPHONE16PRO", supplierId, categoryId, brandId);
        Long productId2 = insertProductSpu("ThinkPad X1 Carbon", "THINKPAD_X1", supplierId, categoryId2, brandId2);

        // 创建 SKU
        Long skuId = insertProductSku(productId, "SKU_001", "iPhone 16 Pro 256GB", "256GB", 9999.00, 8999.00);
        Long skuId2 = insertProductSku(productId, "SKU_002", "iPhone 16 Pro 512GB", "512GB", 11999.00, 10999.00);
        Long skuId3 = insertProductSku(productId2, "SKU_003", "ThinkPad X1 Carbon i7/16GB/512GB", "i7/16GB/512GB", 12999.00, 11999.00);

        // 创建商家商品
        insertMerchantGoods(merchantMemberId.longValue(), supplierId, productId, skuId, 100);
        insertMerchantGoods(merchantMemberId.longValue(), supplierId, productId, skuId2, 50);
        insertMerchantGoods(merchantMemberId.longValue(), supplierId, productId2, skuId3, 30);

        log.info("Sample products created");
    }

    private Long insertMemberAccount(String name, String displayName, String type) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        jdbcTemplate.update(
                """
            INSERT INTO member_account (member_code, member_name, member_type, status, created_time, updated_time, deleted)
            VALUES (?, ?, ?, 'ENABLED', NOW(), NOW(), 0)
            """,
                uuid,
                name,
                type);

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Long insertSysUser(
            String username, String password, String nickName, Long memberId, String role) {
        String userType = "ADMIN".equals(role) ? "ADMIN" : "MEMBER";
        jdbcTemplate.update(
                """
            INSERT INTO sys_user (username, password, nick_name, email, member_id, user_type, status, created_time, updated_time, deleted)
            VALUES (?, ?, ?, ?, ?, ?, 'ENABLED', NOW(), NOW(), 0)
            """,
                username,
                password,
                nickName,
                username + "@test.com",
                memberId,
                userType);

        Long userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        // 设置最后活跃身份
        jdbcTemplate.update(
                "UPDATE sys_user SET last_active_identity_type = ? WHERE id = ?", role, userId);

        return userId;
    }

    private void insertUserRole(Long userId, String roleCode) {
        Integer roleId =
                jdbcTemplate.queryForObject(
                        """
            SELECT id FROM sys_role WHERE role_code = ? AND deleted = 0
            """,
                        Integer.class,
                        roleCode);

        if (roleId != null) {
            jdbcTemplate.update(
                    """
                INSERT INTO sys_user_role (user_id, role_id)
                VALUES (?, ?)
                """,
                    userId,
                    roleId);
        }
    }

    private void insertUserIdentityBinding(Long userId, String identityType, Long identityRefId) {
        jdbcTemplate.update(
                """
            INSERT INTO user_identity_binding (user_id, identity_type, identity_ref_id, status, is_default, created_by, updated_by, deleted)
            VALUES (?, ?, ?, 'ENABLED', 1, ?, ?, 0)
            """,
                userId,
                identityType,
                identityRefId,
                userId,
                userId);
    }

    private Long insertProductCategory(String name, String code) {
        jdbcTemplate.update(
                """
            INSERT INTO product_category (category_name, category_code, parent_id, sort_no, status, created_by, updated_by, deleted)
            VALUES (?, ?, 0, 0, 'ENABLED', 1, 1, 0)
            """,
                name,
                code);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Long insertProductBrand(String name, String code) {
        jdbcTemplate.update(
                """
            INSERT INTO product_brand (brand_name, brand_code, status, created_by, updated_by, deleted)
            VALUES (?, ?, 'ENABLED', 1, 1, 0)
            """,
                name,
                code);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Long insertProductSpu(
            String spuName, String spuCode, Long supplierId, Long categoryId, Long brandId) {
        jdbcTemplate.update(
                """
            INSERT INTO product_spu (supplier_id, brand_id, category_id, spu_code, spu_name, sale_status, created_by, updated_by, deleted)
            VALUES (?, ?, ?, ?, ?, 'ON', 1, 1, 0)
            """,
                supplierId,
                brandId,
                categoryId,
                spuCode,
                spuName);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Long insertProductSku(
            Long spuId, String skuCode, String skuName, String specText, Double costPrice, Double basePrice) {
        jdbcTemplate.update(
                """
            INSERT INTO product_sku (spu_id, sku_code, sku_name, spec_text, cost_price, base_price, sale_status, created_by, updated_by, deleted)
            VALUES (?, ?, ?, ?, ?, ?, 'ON', 1, 1, 0)
            """,
                spuId,
                skuCode,
                skuName,
                specText,
                costPrice,
                basePrice);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private void insertMerchantGoods(
            Long merchantId, Long supplierId, Long spuId, Long skuId, Integer stockQty) {
        jdbcTemplate.update(
                """
            INSERT INTO merchant_goods (merchant_id, supplier_id, spu_id, sku_id, current_cost_price, stock_qty, sale_status, created_by, updated_by, deleted)
            VALUES (?, ?, ?, ?, (SELECT base_price FROM product_sku WHERE id = ?), ?, 'ON', 1, 1, 0)
            """,
                merchantId,
                supplierId,
                spuId,
                skuId,
                skuId,
                stockQty);
    }
}
