package com.telecom.scm.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MallCartMapper {

    @Select(
            """
        SELECT
            merchant_goods_id AS merchantGoodsId,
            sku_id AS skuId,
            quantity
        FROM cart_item
        WHERE customer_id = #{customerId}
        ORDER BY updated_time DESC, id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<CartItemRow> selectCartItems(
            @Param("customerId") Long customerId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM cart_item
        WHERE customer_id = #{customerId}
        """)
    long countCartItems(@Param("customerId") Long customerId);

    @Insert(
            """
        INSERT INTO cart_item (
            customer_id,
            merchant_goods_id,
            sku_id,
            quantity,
            checked_flag
        ) VALUES (
            #{customerId},
            #{merchantGoodsId},
            #{skuId},
            #{quantity},
            1
        )
        ON DUPLICATE KEY UPDATE
            sku_id = VALUES(sku_id),
            quantity = quantity + VALUES(quantity),
            checked_flag = 1,
            updated_time = CURRENT_TIMESTAMP
        """)
    int upsertCartItem(
            @Param("customerId") Long customerId,
            @Param("merchantGoodsId") Long merchantGoodsId,
            @Param("skuId") Long skuId,
            @Param("quantity") int quantity);

    @Update(
            """
        UPDATE cart_item
        SET quantity = #{quantity},
            checked_flag = 1,
            updated_time = CURRENT_TIMESTAMP
        WHERE customer_id = #{customerId}
            AND merchant_goods_id = #{merchantGoodsId}
        """)
    int updateCartQuantity(
            @Param("customerId") Long customerId,
            @Param("merchantGoodsId") Long merchantGoodsId,
            @Param("quantity") int quantity);

    @Delete(
            """
        DELETE FROM cart_item
        WHERE customer_id = #{customerId}
            AND merchant_goods_id = #{merchantGoodsId}
        """)
    int deleteCartItem(
            @Param("customerId") Long customerId, @Param("merchantGoodsId") Long merchantGoodsId);

    @Delete(
            """
        DELETE FROM cart_item
        WHERE customer_id = #{customerId}
        """)
    int clearCart(@Param("customerId") Long customerId);
}
