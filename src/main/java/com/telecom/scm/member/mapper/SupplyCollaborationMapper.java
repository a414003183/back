package com.telecom.scm.member.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.member.entity.MerchantGoodsEntity;
import com.telecom.scm.member.entity.MerchantSupplierRelationEntity;
import com.telecom.scm.member.entity.SupplierGoodsAuthorizationEntity;
import com.telecom.scm.member.mapper.row.ImportedMerchantGoodsRow;
import com.telecom.scm.member.mapper.row.MerchantContextRow;
import com.telecom.scm.member.mapper.row.MerchantImportAuthorizationRow;
import com.telecom.scm.member.mapper.row.MerchantOptionRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyCatalogRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyRelationRow;
import com.telecom.scm.member.mapper.row.PlatformSupplierRow;
import com.telecom.scm.member.mapper.row.SupplierAuthorizationBriefRow;
import com.telecom.scm.member.mapper.row.SupplierAuthorizationRow;
import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplierInfoRow;
import com.telecom.scm.member.mapper.row.SupplierOwnedSkuRow;
import com.telecom.scm.member.mapper.row.SupplierRelationDetailRow;
import com.telecom.scm.member.mapper.row.SupplierRelationRow;
import com.telecom.scm.member.mapper.row.SupplierSkuOptionRow;

@Mapper
public interface SupplyCollaborationMapper {

    @Select(
            """
        SELECT u.id AS userId, supplier.id AS supplierId
        FROM sys_user u
        JOIN member_account ma ON ma.id = u.member_id
            AND ma.deleted = 0
            AND ma.status = 'ENABLED'
            AND ma.member_type = 'SUPPLIER'
        JOIN supplier_info supplier ON supplier.member_id = ma.id
            AND supplier.deleted = 0
            AND supplier.status = 'ENABLED'
        WHERE u.deleted = 0
            AND u.status = 'ENABLED'
            AND u.username = #{username}
        LIMIT 1
        """)
    SupplierContextRow selectSupplierContextByUsername(@Param("username") String username);

    @Select(
            """
        SELECT u.id AS userId, merchant.id AS merchantId
        FROM sys_user u
        JOIN member_account ma ON ma.id = u.member_id
            AND ma.deleted = 0
            AND ma.status = 'ENABLED'
            AND ma.member_type = 'MERCHANT'
        JOIN merchant_info merchant ON merchant.member_id = ma.id
            AND merchant.deleted = 0
            AND merchant.status = 'ENABLED'
        WHERE u.deleted = 0
            AND u.status = 'ENABLED'
            AND u.username = #{username}
        LIMIT 1
        """)
    MerchantContextRow selectMerchantContextByUsername(@Param("username") String username);

    @Select(
            """
        SELECT
            CAST(merchant.id AS CHAR) AS value,
            CONCAT(merchant.shop_name, ' / ', COALESCE(ma.member_code, '-')) AS label
        FROM merchant_info merchant
        JOIN member_account ma ON ma.id = merchant.member_id
            AND ma.deleted = 0
            AND ma.status = 'ENABLED'
        WHERE merchant.deleted = 0
            AND merchant.status = 'ENABLED'
        ORDER BY merchant.shop_name ASC, merchant.id DESC
        """)
    List<MerchantOptionRow> selectMerchantOptions();

    @Select(
            """
        SELECT
            CAST(sku.id AS CHAR) AS value,
            CONCAT(spu.spu_name, ' / ', sku.sku_name, ' / ', sku.spec_text) AS label
        FROM product_spu spu
        JOIN product_sku sku ON sku.spu_id = spu.id
            AND sku.deleted = 0
        WHERE spu.deleted = 0
            AND spu.supplier_id = #{supplierId}
        ORDER BY spu.spu_name ASC, sku.id DESC
        """)
    List<SupplierSkuOptionRow> selectSupplierSkuOptions(@Param("supplierId") Long supplierId);

    List<SupplierRelationRow> selectSupplierRelationRows(
            @Param("supplierId") Long supplierId,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countSupplierRelationRows(
            @Param("supplierId") Long supplierId, @Param("status") String status);

    List<SupplierAuthorizationRow> selectSupplierAuthorizationRows(
            @Param("supplierId") Long supplierId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countSupplierAuthorizationRows(@Param("supplierId") Long supplierId);

    @Select(
            """
        SELECT relation_row.id AS id, relation_row.merchant_id AS merchantId, relation_row.supplier_id AS supplierId, relation_row.status AS status
        FROM merchant_supplier_relation relation_row
        WHERE relation_row.deleted = 0
            AND relation_row.supplier_id = #{supplierId}
            AND relation_row.merchant_id = #{merchantId}
        LIMIT 1
        """)
    SupplierRelationDetailRow selectSupplierRelationByPair(
            @Param("supplierId") Long supplierId, @Param("merchantId") Long merchantId);

    @Insert(
            """
        INSERT INTO merchant_supplier_relation (
            merchant_id, supplier_id, status, cooperation_start_at, cooperation_end_at, remark, created_by, updated_by, deleted
        ) VALUES (
            #{merchantId}, #{supplierId}, #{status}, #{cooperationStartAt}, #{cooperationEndAt}, #{remark}, #{createdBy}, #{updatedBy}, 0
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSupplierRelation(MerchantSupplierRelationEntity payload);

    @Update(
            """
        UPDATE merchant_supplier_relation
        SET status = #{status},
            cooperation_start_at = #{cooperationStartAt},
            cooperation_end_at = #{cooperationEndAt},
            remark = #{remark},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP,
            deleted = 0
        WHERE id = #{relationId}
        """)
    int updateSupplierRelation(MerchantSupplierRelationEntity payload);

    @Select(
            """
        SELECT
            auth_row.id AS id,
            auth_row.supplier_id AS supplierId,
            auth_row.merchant_id AS merchantId,
            auth_row.supplier_sku_id AS supplierSkuId,
            auth_row.auth_status AS authStatus
        FROM supplier_goods_authorization auth_row
        WHERE auth_row.deleted = 0
            AND auth_row.supplier_id = #{supplierId}
            AND auth_row.merchant_id = #{merchantId}
            AND auth_row.supplier_sku_id = #{supplierSkuId}
        LIMIT 1
        """)
    SupplierAuthorizationBriefRow selectSupplierAuthorizationByPair(
            @Param("supplierId") Long supplierId,
            @Param("merchantId") Long merchantId,
            @Param("supplierSkuId") Long supplierSkuId);

    @Select(
            """
        SELECT sku.id AS supplierSkuId, sku.base_price AS basePrice, sku.cost_price AS costPrice, sku.stock_qty AS stockQty
        FROM product_sku sku
        JOIN product_spu spu ON spu.id = sku.spu_id
            AND spu.deleted = 0
        WHERE sku.deleted = 0
            AND sku.id = #{supplierSkuId}
            AND spu.supplier_id = #{supplierId}
        LIMIT 1
        """)
    SupplierOwnedSkuRow selectSupplierOwnedSku(
            @Param("supplierId") Long supplierId, @Param("supplierSkuId") Long supplierSkuId);

    @Select(
            """
        SELECT auth_row.id AS id, auth_row.supplier_id AS supplierId, auth_row.merchant_id AS merchantId,
               auth_row.supplier_sku_id AS supplierSkuId, auth_row.auth_status AS authStatus,
               auth_row.authorized_price AS authorizedPrice, auth_row.remark AS remark
        FROM supplier_goods_authorization auth_row
        WHERE auth_row.deleted = 0 AND auth_row.id = #{authorizationId}
        LIMIT 1
        """)
    SupplierAuthorizationBriefRow selectSupplierAuthorizationById(
            @Param("authorizationId") Long authorizationId);

    @Insert(
            """
        INSERT INTO supplier_goods_authorization (
            supplier_id, merchant_id, supplier_sku_id, auth_status, authorized_price, allocated_stock_qty, authorized_at, revoked_at, remark, created_by, updated_by, deleted
        ) VALUES (
            #{supplierId}, #{merchantId}, #{supplierSkuId}, #{authStatus}, #{authorizedPrice}, #{allocatedStockQty}, #{authorizedAt}, #{revokedAt}, #{remark}, #{createdBy}, #{updatedBy}, 0
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSupplierAuthorization(SupplierGoodsAuthorizationEntity payload);

    @Update(
            """
        UPDATE supplier_goods_authorization
        SET auth_status = #{authStatus},
            authorized_price = COALESCE(#{authorizedPrice}, authorized_price),
            allocated_stock_qty = COALESCE(#{allocatedStockQty}, allocated_stock_qty),
            authorized_at = #{authorizedAt},
            revoked_at = #{revokedAt},
            remark = #{remark},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP,
            deleted = 0
        WHERE id = #{authorizationId}
        """)
    int updateSupplierAuthorization(SupplierGoodsAuthorizationEntity payload);

    @Update(
            """
        UPDATE supplier_goods_authorization
        SET auth_status = 'REVOKED',
            revoked_at = NOW(),
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE deleted = 0
            AND supplier_id = #{supplierId}
            AND merchant_id = #{merchantId}
            AND auth_status = 'ACTIVE'
        """)
    int revokeAuthorizationsByRelation(
            @Param("supplierId") Long supplierId,
            @Param("merchantId") Long merchantId,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE merchant_goods
        SET sale_status = 'OFF',
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE deleted = 0
            AND supplier_id = #{supplierId}
            AND merchant_id = #{merchantId}
        """)
    int offSaleMerchantGoodsByRelation(
            @Param("supplierId") Long supplierId,
            @Param("merchantId") Long merchantId,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE merchant_goods
        SET current_cost_price = #{authorizedPrice},
            stock_qty = #{allocatedStockQty},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE deleted = 0
            AND supplier_id = #{supplierId}
            AND merchant_id = #{merchantId}
            AND sku_id = #{supplierSkuId}
        """)
    int updateImportedGoodsByAuthorization(
            @Param("supplierId") Long supplierId,
            @Param("merchantId") Long merchantId,
            @Param("supplierSkuId") Long supplierSkuId,
            @Param("authorizedPrice") BigDecimal authorizedPrice,
            @Param("allocatedStockQty") Integer allocatedStockQty,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE merchant_goods
        SET sale_status = 'OFF',
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE deleted = 0
            AND supplier_id = #{supplierId}
            AND merchant_id = #{merchantId}
            AND sku_id = #{supplierSkuId}
        """)
    int offSaleMerchantGoodsByAuthorization(
            @Param("supplierId") Long supplierId,
            @Param("merchantId") Long merchantId,
            @Param("supplierSkuId") Long supplierSkuId,
            @Param("updatedBy") Long updatedBy);

    List<MerchantSupplyRelationRow> selectMerchantSupplyRelationRows(
            @Param("merchantId") Long merchantId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Select(
            """
        SELECT COUNT(1)
        FROM merchant_supplier_relation relation_row
        JOIN supplier_info supplier ON supplier.id = relation_row.supplier_id
            AND supplier.deleted = 0
        JOIN member_account ma ON ma.id = supplier.member_id
            AND ma.deleted = 0
        WHERE relation_row.deleted = 0
            AND relation_row.merchant_id = #{merchantId}
        """)
    long countMerchantSupplyRelationRows(@Param("merchantId") Long merchantId);

    List<Map<String, Object>> selectMerchantSupplyCatalogRows(@Param("merchantId") Long merchantId);

    List<MerchantSupplyCatalogRow> selectMerchantSupplyCatalogWithRelationRows(
            @Param("merchantId") Long merchantId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countMerchantSupplyCatalogWithRelationRows(@Param("merchantId") Long merchantId);

    MerchantImportAuthorizationRow selectMerchantImportAuthorization(
            @Param("merchantId") Long merchantId, @Param("supplierSkuId") Long supplierSkuId);

    @Select(
            """
        SELECT mg.id AS merchantGoodsId
        FROM merchant_goods mg
        WHERE mg.deleted = 0
            AND mg.merchant_id = #{merchantId}
            AND mg.sku_id = #{supplierSkuId}
        LIMIT 1
        """)
    ImportedMerchantGoodsRow selectImportedMerchantGoods(
            @Param("merchantId") Long merchantId, @Param("supplierSkuId") Long supplierSkuId);

    @Insert(
            """
        INSERT INTO merchant_goods (
            merchant_id, supplier_id, sku_id, sale_price, current_cost_price, stock_qty, rebate_rate, sale_status, created_by, updated_by, deleted
        ) VALUES (
            #{merchantId}, #{supplierId}, #{supplierSkuId}, #{salePrice}, #{currentCostPrice}, #{stockQty}, #{rebateRate}, #{saleStatus}, #{createdBy}, #{updatedBy}, 0
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMerchantGoods(MerchantGoodsEntity payload);

    List<PlatformSupplierRow> selectPlatformSuppliers(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Select(
            """
        SELECT COUNT(1)
        FROM supplier_info supplier
        JOIN member_account ma ON ma.id = supplier.member_id
            AND ma.deleted = 0
            AND ma.status = 'ENABLED'
        WHERE supplier.deleted = 0
            AND supplier.status = 'ENABLED'
            AND (#{keyword} IS NULL OR #{keyword} = ''
                OR supplier.supplier_name LIKE CONCAT('%', #{keyword}, '%')
                OR ma.member_code LIKE CONCAT('%', #{keyword}, '%'))
        """)
    long countPlatformSuppliers(@Param("keyword") String keyword);

    @Select(
            """
        SELECT supplier.id AS id, supplier.supplier_name AS supplierName
        FROM supplier_info supplier
        WHERE supplier.deleted = 0 AND supplier.id = #{supplierId}
        LIMIT 1
        """)
    SupplierInfoRow selectSupplierById(@Param("supplierId") Long supplierId);

    @Select(
            """
        SELECT relation_row.id AS id, relation_row.merchant_id AS merchantId,
               relation_row.supplier_id AS supplierId, relation_row.status AS status,
               relation_row.remark AS remark
        FROM merchant_supplier_relation relation_row
        WHERE relation_row.deleted = 0 AND relation_row.id = #{relationId}
        LIMIT 1
        """)
    SupplierRelationDetailRow selectSupplierRelationById(@Param("relationId") Long relationId);
}
