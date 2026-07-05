package com.telecom.scm.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.member.entity.ProductStockLogEntity;
import com.telecom.scm.order.entity.OrderInfoEntity;
import com.telecom.scm.order.entity.OrderItemAdjustLogEntity;
import com.telecom.scm.order.entity.OrderItemEntity;
import com.telecom.scm.order.entity.OrderStatusLogEntity;
import com.telecom.scm.order.entity.PaymentRecordEntity;
import com.telecom.scm.order.entity.ShipmentInfoEntity;

@Mapper
public interface OrderWriteMapper {

    OrderCreateContextRow selectCustomerContextByUsername(@Param("username") String username);

    OrderOperatorContextRow selectMerchantContextByUsername(@Param("username") String username);

    List<OrderCreateGoodsRow> selectGoodsForCreate(
            @Param("merchantGoodsIds") List<Long> merchantGoodsIds,
            @Param("customerId") Long customerId,
            @Param("memberLevel") String memberLevel);

    OrderProcessRow selectCustomerOrderForAction(
            @Param("orderId") Long orderId, @Param("customerId") Long customerId);

    OrderProcessRow selectMerchantOrderForAction(
            @Param("orderId") Long orderId, @Param("merchantId") Long merchantId);

    int insertOrderInfo(OrderInfoEntity orderInfo);

    int insertOrderItems(@Param("items") List<OrderItemEntity> items);

    int insertOrderStatusLog(OrderStatusLogEntity statusLog);

    int insertPaymentRecord(PaymentRecordEntity paymentRecord);

    int updateLatestPaymentRecordForOrder(
            @Param("orderId") Long orderId,
            @Param("payStatus") String payStatus,
            @Param("confirmedBy") Long confirmedBy,
            @Param("remark") String remark);

    int updateOrderState(
            @Param("orderId") Long orderId,
            @Param("orderStatus") String orderStatus,
            @Param("payStatus") String payStatus,
            @Param("shipmentStatus") String shipmentStatus,
            @Param("updatedBy") Long updatedBy);

    int upsertShipmentInfo(ShipmentInfoEntity shipmentInfo);

    @Update(
            """
        UPDATE shipment_info
        SET ship_status = 'SIGNED',
            sign_time = NOW(),
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE order_id = #{orderId}
        """)
    int signShipment(@Param("orderId") Long orderId, @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE merchant_goods mg
        JOIN product_sku sku ON sku.id = mg.sku_id AND sku.deleted = 0
        SET mg.stock_qty = COALESCE(mg.stock_qty, sku.stock_qty, 0) - #{quantity},
            sku.stock_qty = sku.stock_qty - #{quantity},
            mg.updated_by = #{updatedBy},
            mg.updated_time = CURRENT_TIMESTAMP,
            sku.updated_by = #{updatedBy},
            sku.updated_time = CURRENT_TIMESTAMP
        WHERE mg.merchant_id = #{merchantId}
            AND sku.id = #{skuId}
            AND sku.deleted = 0
            AND COALESCE(mg.stock_qty, sku.stock_qty, 0) >= #{quantity}
            AND sku.stock_qty >= #{quantity}
        """)
    int decreaseStock(
            @Param("skuId") Long skuId,
            @Param("quantity") int quantity,
            @Param("updatedBy") Long updatedBy,
            @Param("merchantId") Long merchantId);

    @Select(
            """
        SELECT
            CASE
                WHEN mg.supplier_id IS NOT NULL THEN LEAST(COALESCE(mg.stock_qty, sku.stock_qty), sku.stock_qty)
                ELSE COALESCE(mg.stock_qty, sku.stock_qty, 0)
            END
        FROM product_sku sku
        JOIN merchant_goods mg ON mg.sku_id = sku.id AND mg.deleted = 0
        WHERE sku.id = #{skuId}
            AND sku.deleted = 0
            AND mg.merchant_id = #{merchantId}
        LIMIT 1
        """)
    Integer selectSkuStockQty(@Param("skuId") Long skuId, @Param("merchantId") Long merchantId);

    @Update(
            """
        UPDATE merchant_goods mg
        JOIN product_sku sku ON sku.id = mg.sku_id AND sku.deleted = 0
        SET mg.stock_qty = COALESCE(mg.stock_qty, sku.stock_qty, 0) + #{quantity},
            sku.stock_qty = sku.stock_qty + #{quantity},
            mg.updated_by = #{updatedBy},
            mg.updated_time = CURRENT_TIMESTAMP,
            sku.updated_by = #{updatedBy},
            sku.updated_time = CURRENT_TIMESTAMP
        WHERE mg.merchant_id = #{merchantId}
            AND sku.id = #{skuId}
            AND sku.deleted = 0
        """)
    int increaseStock(
            @Param("skuId") Long skuId,
            @Param("quantity") int quantity,
            @Param("updatedBy") Long updatedBy,
            @Param("merchantId") Long merchantId);

    int insertProductStockLog(ProductStockLogEntity stockLog);

    @Select(
            """
        SELECT COUNT(1)
        FROM product_stock_log
        WHERE biz_type = 'ORDER'
            AND biz_id = #{orderId}
            AND change_type = 'OUT'
        """)
    long countOrderStockDeductionLogs(@Param("orderId") Long orderId);

    List<OrderItemForAdjustRow> selectOrderItemsForAdjust(@Param("orderId") Long orderId);

    int updateOrderItemForAdjust(
            @Param("orderId") Long orderId,
            @Param("orderItemId") Long orderItemId,
            @Param("quantity") int quantity,
            @Param("finalUnitPrice") java.math.BigDecimal finalUnitPrice,
            @Param("finalAmount") java.math.BigDecimal finalAmount,
            @Param("profitAmount") java.math.BigDecimal profitAmount);

    int insertOrderItemAdjustLog(OrderItemAdjustLogEntity adjustLog);

    int updateOrderAmounts(
            @Param("orderId") Long orderId,
            @Param("goodsAmount") java.math.BigDecimal goodsAmount,
            @Param("costAmount") java.math.BigDecimal costAmount,
            @Param("profitAmount") java.math.BigDecimal profitAmount,
            @Param("payAmount") java.math.BigDecimal payAmount,
            @Param("updatedBy") Long updatedBy);
}
