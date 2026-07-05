package com.telecom.scm.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.member.entity.MerchantGoodsEntity;
import com.telecom.scm.member.entity.ProductSkuEntity;
import com.telecom.scm.member.entity.ProductSpuEntity;
import com.telecom.scm.member.mapper.row.BrandOptionRow;
import com.telecom.scm.member.mapper.row.CategoryOptionRow;
import com.telecom.scm.member.mapper.row.MerchantContextRow;
import com.telecom.scm.member.mapper.row.MerchantGoodOwnershipRow;
import com.telecom.scm.member.mapper.row.MerchantGoodsRow;

@Mapper
public interface MerchantGoodsMapper {

    MerchantContextRow selectMerchantContextByUsername(@Param("username") String username);

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

    int insertDirectSpu(ProductSpuEntity payload);

    int insertDirectSku(ProductSkuEntity payload);

    int insertMerchantDirectGoods(MerchantGoodsEntity payload);

    @Update(
            """
        UPDATE merchant_goods
        SET main_image_id = #{mainImageId},
            image_ids = #{imageIds},
            keywords = #{keywords},
            detail_content = #{detailContent},
            updated_by = #{operatorId},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{merchantGoodsId}
            AND merchant_id = #{merchantId}
            AND deleted = 0
        """)
    int updateMerchantDirectGoods(MerchantGoodsEntity payload);

    List<MerchantGoodsRow> selectMerchantGoodsRows(
            @Param("merchantId") Long merchantId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countMerchantGoodsRows(@Param("merchantId") Long merchantId);

    MerchantGoodOwnershipRow selectMerchantGoodOwnership(
            @Param("merchantId") Long merchantId, @Param("merchantGoodsId") Long merchantGoodsId);

    @Update(
            """
        UPDATE merchant_goods
        SET sale_price = #{salePrice},
            rebate_rate = #{rebateRate},
            sale_status = #{saleStatus},
            delivery_mode = #{deliveryMode},
            stock_qty = #{stockQty},
            safety_stock = #{safetyStock},
            updated_by = #{operatorId},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{merchantGoodsId}
            AND merchant_id = #{merchantId}
            AND deleted = 0
        """)
    int updateMerchantGood(MerchantGoodsEntity payload);

    int updateMerchantDirectGoodFull(MerchantGoodsEntity payload);

    @Update(
            "UPDATE merchant_goods SET deleted = 1, updated_time = CURRENT_TIMESTAMP WHERE id = #{merchantGoodsId}")
    int deleteMerchantGood(@Param("merchantGoodsId") Long merchantGoodsId);

    MerchantGoodsRow selectMerchantGoodById(
            @Param("merchantId") Long merchantId, @Param("merchantGoodsId") Long merchantGoodsId);
}
