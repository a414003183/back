package com.telecom.scm.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderDocumentMapper {

    OrderQuoteSummaryRow selectCustomerQuoteSummary(
            @Param("username") String username, @Param("orderId") Long orderId);

    OrderQuoteSummaryRow selectMerchantQuoteSummary(
            @Param("username") String username, @Param("orderId") Long orderId);

    String selectCustomerOrderAccess(
            @Param("username") String username, @Param("orderId") Long orderId);

    String selectMerchantOrderAccess(
            @Param("username") String username, @Param("orderId") Long orderId);

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            spu_name AS spuName,
            sku_name AS skuName,
            spec_text AS specText,
            quantity AS quantity,
            final_unit_price AS finalUnitPrice,
            final_amount AS finalAmount
        FROM order_item
        WHERE order_id = #{orderId}
        ORDER BY id DESC
        """)
    List<OrderQuoteItemRow> selectQuoteItems(@Param("orderId") Long orderId);

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            original_name AS originalName,
            content_type AS contentType,
            file_size AS fileSize,
            DATE_FORMAT(upload_time, '%Y-%m-%d %H:%i:%s') AS uploadTime
        FROM file_storage
        WHERE biz_type IN ('ORDER_CONTRACT', 'ORDER_PAYMENT', 'ORDER_SHIPMENT')
            AND biz_id = #{orderId}
        ORDER BY upload_time DESC, id DESC
        """)
    List<OrderContractRow> selectOrderContractRows(@Param("orderId") Long orderId);

    @Select(
            """
        SELECT id
        FROM file_storage
        WHERE id = #{fileId}
            AND biz_type IN ('ORDER_CONTRACT', 'ORDER_PAYMENT', 'ORDER_SHIPMENT')
            AND biz_id = #{orderId}
        LIMIT 1
        """)
    Long selectOrderContractFileId(@Param("orderId") Long orderId, @Param("fileId") Long fileId);
}
