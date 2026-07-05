package com.telecom.scm.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.member.entity.ProductSkuEntity;
import com.telecom.scm.member.entity.ProductSpuEntity;
import com.telecom.scm.member.mapper.row.BrandOptionRow;
import com.telecom.scm.member.mapper.row.CategoryOptionRow;
import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplierOwnedProductRow;
import com.telecom.scm.member.mapper.row.SupplierProductRow;

@Mapper
public interface SupplierProductMapper {

    @Select(
            """
        SELECT
            u.id AS userId,
            supplier.id AS supplierId
        FROM sys_user u
        JOIN member_account member_account ON member_account.id = u.member_id
            AND member_account.deleted = 0
            AND member_account.status = 'ENABLED'
            AND member_account.member_type = 'SUPPLIER'
        JOIN supplier_info supplier ON supplier.member_id = member_account.id
            AND supplier.deleted = 0
            AND supplier.status = 'ENABLED'
        WHERE u.deleted = 0
            AND u.status = 'ENABLED'
            AND u.username = #{username}
        LIMIT 1
        """)
    SupplierContextRow selectSupplierContextByUsername(@Param("username") String username);

    List<SupplierProductRow> selectSupplierProducts(
            @Param("supplierId") Long supplierId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countSupplierProducts(@Param("supplierId") Long supplierId);

    @Select(
            """
        SELECT CAST(id AS CHAR) AS value, brand_name AS label
        FROM product_brand
        WHERE deleted = 0
            AND status = 'ENABLED'
        ORDER BY sort_no ASC, id DESC
        """)
    List<BrandOptionRow> selectBrandOptions();

    @Select(
            """
        SELECT CAST(id AS CHAR) AS value, category_name AS label, parent_id AS parentId, level_no AS levelNo
        FROM product_category
        WHERE deleted = 0
            AND status = 'ENABLED'
        ORDER BY level_no ASC, sort_no ASC, id DESC
        """)
    List<CategoryOptionRow> selectCategoryOptions();

    int insertSpu(ProductSpuEntity payload);

    int insertSku(ProductSkuEntity payload);

    @Select(
            """
        SELECT
            spu.id AS spuId,
            sku.id AS skuId
        FROM product_spu spu
        JOIN product_sku sku ON sku.spu_id = spu.id
            AND sku.deleted = 0
        WHERE spu.deleted = 0
            AND spu.supplier_id = #{supplierId}
            AND sku.id = #{skuId}
        LIMIT 1
        """)
    SupplierOwnedProductRow selectSupplierOwnedProduct(
            @Param("supplierId") Long supplierId, @Param("skuId") Long skuId);

    @Update(
            """
        UPDATE product_spu
        SET brand_id = #{brandId},
            category_id = #{categoryId},
            spu_name = #{spuName},
            main_image_id = #{mainImageId},
            image_ids = #{imageIds},
            keywords = #{keywords},
            detail_content = #{detailContent},
            description = #{description},
            updated_by = #{operatorId},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{spuId}
            AND supplier_id = #{supplierId}
            AND deleted = 0
        """)
    int updateSpu(ProductSpuEntity payload);

    @Update(
            """
        UPDATE product_sku
        SET sku_name = #{skuName},
            spec_text = #{specText},
            base_price = #{basePrice},
            stock_qty = #{stockQty},
            safety_stock = #{safetyStock},
            updated_by = #{operatorId},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{skuId}
            AND deleted = 0
        """)
    int updateSku(ProductSkuEntity payload);

    @Update(
            "UPDATE product_sku SET deleted = 1, updated_time = CURRENT_TIMESTAMP WHERE id = #{skuId}")
    int deleteSku(@Param("skuId") Long skuId);

    @Update(
            "UPDATE product_spu SET deleted = 1, updated_time = CURRENT_TIMESTAMP WHERE id = #{spuId}")
    int deleteSpu(@Param("spuId") Long spuId);

    SupplierProductRow selectSupplierProductBySkuId(
            @Param("supplierId") Long supplierId, @Param("skuId") Long skuId);
}
