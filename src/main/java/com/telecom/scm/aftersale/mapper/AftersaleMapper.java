package com.telecom.scm.aftersale.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.aftersale.entity.AftersaleAuditLogEntity;
import com.telecom.scm.aftersale.entity.AftersaleInfoEntity;
import com.telecom.scm.aftersale.entity.AftersaleItemEntity;
import com.telecom.scm.aftersale.entity.RefundRecordEntity;
import com.telecom.scm.member.entity.ProductStockLogEntity;
import com.telecom.scm.order.mapper.OrderCreateContextRow;
import com.telecom.scm.order.mapper.OrderOperatorContextRow;

@Mapper
public interface AftersaleMapper {

    OrderCreateContextRow selectCustomerContextByUsername(@Param("username") String username);

    OrderOperatorContextRow selectMerchantContextByUsername(@Param("username") String username);

    List<AftersaleRow> selectCustomerAftersaleRows(
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countCustomerAftersales(@Param("username") String username);

    List<AftersaleRow> selectMerchantAftersaleRows(
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countMerchantAftersales(@Param("username") String username);

    @Select(
            """
        SELECT
            o.id,
            o.order_no,
            o.customer_id,
            o.merchant_id,
            o.pay_amount,
            o.pay_status,
            o.order_status,
            o.aftersale_status
        FROM order_info o
        WHERE o.deleted = 0
            AND o.id = #{orderId}
            AND o.customer_id = #{customerId}
        LIMIT 1
        """)
    AftersaleOrderRow selectCustomerOrderForApply(
            @Param("orderId") Long orderId, @Param("customerId") Long customerId);

    @Select(
            """
        SELECT COUNT(1)
        FROM aftersale_info
        WHERE deleted = 0
            AND order_id = #{orderId}
            AND aftersale_status NOT IN ('FINISHED', 'REJECTED')
        """)
    long countActiveAftersalesByOrderId(@Param("orderId") Long orderId);

    @Select(
            """
        SELECT COUNT(1)
        FROM aftersale_info
        WHERE deleted = 0
            AND order_id = #{orderId}
            AND aftersale_status = 'FINISHED'
        """)
    long countFinishedAftersalesByOrderId(@Param("orderId") Long orderId);

    @Select(
            """
        SELECT
            id AS order_item_id,
            sku_id,
            spu_name,
            sku_name,
            spec_text,
            quantity,
            final_amount
        FROM order_item
        WHERE order_id = #{orderId}
        ORDER BY id DESC
        """)
    List<AftersaleOrderItemRow> selectOrderItemsForAftersale(@Param("orderId") Long orderId);

    int insertAftersaleInfo(AftersaleInfoEntity aftersaleInfo);

    int insertAftersaleItems(@Param("items") List<AftersaleItemEntity> items);

    int insertAftersaleAuditLog(AftersaleAuditLogEntity auditLog);

    @Update(
            """
        UPDATE order_info
        SET aftersale_status = #{aftersaleStatus},
            updated_by = #{updatedBy}
        WHERE id = #{orderId}
            AND deleted = 0
        """)
    int updateOrderAftersaleStatus(
            @Param("orderId") Long orderId,
            @Param("aftersaleStatus") String aftersaleStatus,
            @Param("updatedBy") Long updatedBy);

    AftersaleProcessRow selectCustomerAftersaleForAction(
            @Param("aftersaleId") Long aftersaleId, @Param("customerId") Long customerId);

    AftersaleProcessRow selectMerchantAftersaleForAction(
            @Param("aftersaleId") Long aftersaleId, @Param("merchantId") Long merchantId);

    @Update(
            """
        UPDATE aftersale_info
        SET approved_amount = #{approvedAmount},
            aftersale_status = #{aftersaleStatus},
            remark = CASE
                WHEN #{remark} IS NULL OR #{remark} = '' THEN remark
                ELSE #{remark}
            END,
            updated_by = #{updatedBy}
        WHERE id = #{aftersaleId}
            AND deleted = 0
        """)
    int approveAftersale(
            @Param("aftersaleId") Long aftersaleId,
            @Param("approvedAmount") BigDecimal approvedAmount,
            @Param("aftersaleStatus") String aftersaleStatus,
            @Param("remark") String remark,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE aftersale_info
        SET aftersale_status = 'REJECTED',
            remark = CASE
                WHEN #{remark} IS NULL OR #{remark} = '' THEN remark
                ELSE #{remark}
            END,
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{aftersaleId}
            AND deleted = 0
        """)
    int rejectAftersale(
            @Param("aftersaleId") Long aftersaleId,
            @Param("remark") String remark,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE aftersale_info
        SET return_tracking_no = #{returnTrackingNo},
            aftersale_status = 'WAIT_RECEIVE',
            remark = CASE
                WHEN #{remark} IS NULL OR #{remark} = '' THEN remark
                ELSE #{remark}
            END,
            updated_by = #{updatedBy}
        WHERE id = #{aftersaleId}
            AND deleted = 0
        """)
    int registerReturnShipment(
            @Param("aftersaleId") Long aftersaleId,
            @Param("returnTrackingNo") String returnTrackingNo,
            @Param("remark") String remark,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE aftersale_info
        SET merchant_receive_time = NOW(),
            aftersale_status = 'WAIT_REFUND',
            updated_by = #{updatedBy}
        WHERE id = #{aftersaleId}
            AND deleted = 0
        """)
    int receiveReturn(@Param("aftersaleId") Long aftersaleId, @Param("updatedBy") Long updatedBy);

    int insertRefundRecord(RefundRecordEntity refundRecord);

    @Select(
            """
        SELECT COUNT(1)
        FROM payment_record
        WHERE aftersale_id = #{aftersaleId}
            AND record_type = 'REFUND'
        """)
    long countRefundRecordsByAftersaleId(@Param("aftersaleId") Long aftersaleId);

    @Select(
            """
        SELECT
            ai.sku_id AS skuId,
            ai.quantity AS quantity,
            oi.merchant_goods_id AS merchantGoodsId
        FROM aftersale_item ai
        JOIN order_item oi ON oi.id = ai.order_item_id
        WHERE ai.aftersale_id = #{aftersaleId}
        ORDER BY ai.id DESC
        """)
    List<AftersaleItemStockRow> selectAftersaleItemStocks(@Param("aftersaleId") Long aftersaleId);

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
    int restoreStock(
            @Param("skuId") Long skuId,
            @Param("merchantId") Long merchantId,
            @Param("quantity") int quantity,
            @Param("updatedBy") Long updatedBy);

    int insertProductStockLog(ProductStockLogEntity stockLog);

    @Select(
            """
        SELECT COUNT(1)
        FROM product_stock_log
        WHERE biz_type = 'AFTERSALE'
            AND biz_id = #{aftersaleId}
        """)
    long countStockRestoreLogsByAftersaleId(@Param("aftersaleId") Long aftersaleId);

    @Select(
            """
        SELECT COUNT(1)
        FROM product_stock_log
        WHERE biz_type = 'ORDER'
            AND biz_id = #{orderId}
            AND change_type = 'OUT'
        """)
    long countOrderStockDeductionLogs(@Param("orderId") Long orderId);

    @Update(
            """
        UPDATE aftersale_info
        SET aftersale_status = 'FINISHED',
            finish_time = NOW(),
            remark = CASE
                WHEN #{remark} IS NULL OR #{remark} = '' THEN remark
                ELSE #{remark}
            END,
            updated_by = #{updatedBy}
        WHERE id = #{aftersaleId}
            AND deleted = 0
        """)
    int finishAftersale(
            @Param("aftersaleId") Long aftersaleId,
            @Param("remark") String remark,
            @Param("updatedBy") Long updatedBy);
}
