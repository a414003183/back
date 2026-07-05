package com.telecom.scm.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderQueryMapper {

    List<OrderRow> selectCustomerOrderRows(
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countCustomerOrders(@Param("username") String username);

    List<OrderRow> selectMerchantOrderRows(
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countMerchantOrders(@Param("username") String username);

    @Select(
            """
        SELECT o.id
        FROM order_info o
        WHERE o.deleted = 0
            AND o.order_no = #{orderNo}
        LIMIT 1
        """)
    Long selectOrderIdByOrderNo(@Param("orderNo") String orderNo);

    String selectCustomerOrderAccess(
            @Param("username") String username, @Param("orderId") Long orderId);

    String selectMerchantOrderAccess(
            @Param("username") String username, @Param("orderId") Long orderId);

    OrderBaseInfoRow selectOrderBaseInfo(@Param("orderId") Long orderId);

    MerchantOrderDetailRow selectMerchantOrderDetail(
            @Param("merchantId") Long merchantId, @Param("orderId") Long orderId);

    List<MerchantOrderDetailItemRow> selectMerchantOrderDetailItems(@Param("orderId") Long orderId);

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            old_status AS oldStatus,
            new_status AS newStatus,
            operation_type AS operationType,
            operator_name AS operatorName,
            remark AS remark,
            DATE_FORMAT(created_time, '%Y-%m-%d %H:%i:%s') AS createdAt
        FROM order_status_log
        WHERE order_id = #{orderId}
        ORDER BY created_time DESC, id DESC
        """)
    List<OrderStatusTimelineRow> selectOrderStatusTimelineRows(@Param("orderId") Long orderId);

    List<OrderPaymentTimelineRow> selectOrderPaymentTimelineRows(@Param("orderId") Long orderId);

    OrderShipmentTimelineRow selectOrderShipmentTimelineRow(@Param("orderId") Long orderId);

    List<OrderFileTimelineRow> selectOrderFileTimelineRows(@Param("orderId") Long orderId);

    List<OrderAftersaleTimelineRow> selectOrderAftersaleTimelineRows(
            @Param("orderId") Long orderId);

    List<OrderAftersaleAuditTimelineRow> selectOrderAftersaleAuditTimelineRows(
            @Param("orderId") Long orderId);

    List<OrderItemAdjustLogRow> selectOrderItemAdjustLogs(@Param("orderId") Long orderId);
}
