package com.telecom.scm.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

@Mapper
public interface AppCustomerMapper {

    @Select(
            """
        SELECT
            c.id AS customerId,
            c.company_name AS companyName,
            c.contact_name AS contactName,
            c.contact_phone AS contactPhone,
            c.member_level AS memberLevel,
            u.username AS username
        FROM customer_info c
        JOIN sys_user u ON u.member_id = c.member_id AND u.deleted = 0 AND u.status = 'ENABLED'
        WHERE u.username = #{username} AND c.deleted = 0
        LIMIT 1
        """)
    CustomerProfileRow selectCustomerProfile(@Param("username") String username);

    @Select(
            """
        SELECT
            receiver_name AS receiverName,
            receiver_phone AS receiverPhone,
            province AS receiverProvince,
            city AS receiverCity,
            district AS receiverDistrict,
            detail_address AS receiverAddress
        FROM member_address
        WHERE member_id = #{customerId}
            AND is_default = 1
            AND deleted = 0
        LIMIT 1
        """)
    CustomerAddressRow selectDefaultAddress(@Param("customerId") Long customerId);

    @Update(
            """
        UPDATE customer_info
        SET contact_name = #{contactName},
            contact_phone = #{contactPhone},
            updated_time = NOW()
        WHERE id = (
            SELECT sub.id FROM (
                SELECT c.id FROM customer_info c
                JOIN sys_user u ON u.member_id = c.member_id AND u.deleted = 0 AND u.status = 'ENABLED'
                WHERE u.username = #{username} AND c.deleted = 0
                LIMIT 1
            ) AS sub
        )
        """)
    void updateCustomerProfile(
            @Param("username") String username,
            @Param("contactName") String contactName,
            @Param("contactPhone") String contactPhone);

    @Select(
            """
        SELECT
            CAST(o.id AS CHAR) AS id,
            o.order_no AS orderNo,
            customer.company_name AS customerName,
            merchant.shop_name AS merchantName,
            o.order_status AS orderStatus,
            o.pay_status AS payStatus,
            o.shipment_status AS shipmentStatus,
            o.aftersale_status AS aftersaleStatus,
            o.goods_amount AS goodsAmount,
            o.freight_amount AS freightAmount,
            o.discount_amount AS discountAmount,
            o.pay_amount AS payAmount,
            o.receiver_name AS receiverName,
            o.receiver_phone AS receiverPhone,
            CONCAT(o.receiver_province, o.receiver_city, o.receiver_district, o.receiver_address) AS receiverAddress,
            o.customer_remark AS customerRemark,
            DATE_FORMAT(o.created_time, '%Y-%m-%d %H:%i:%s') AS createdAt
        FROM order_info o
        LEFT JOIN customer_info customer ON customer.id = o.customer_id AND customer.deleted = 0
        LEFT JOIN merchant_info merchant ON merchant.id = o.merchant_id AND merchant.deleted = 0
        WHERE o.deleted = 0
            AND o.id = #{orderId}
            AND o.customer_id = (
                SELECT c.id
                FROM customer_info c
                JOIN sys_user u ON u.member_id = c.member_id AND u.deleted = 0 AND u.status = 'ENABLED'
                WHERE u.username = #{username}
                LIMIT 1
            )
        LIMIT 1
        """)
    CustomerOrderDetailRow selectCustomerOrderDetail(
            @Param("username") String username, @Param("orderId") Long orderId);

    @Select(
            """
        SELECT
            CAST(o.id AS CHAR) AS id,
            CAST(o.merchant_goods_id AS CHAR) AS merchantGoodsId,
            CAST(o.sku_id AS CHAR) AS skuId,
            o.spu_name AS spuName,
            o.sku_name AS skuName,
            o.spec_text AS specText,
            o.quantity AS quantity,
            o.final_unit_price AS finalUnitPrice,
            o.final_amount AS finalAmount,
            CAST(mg.main_image_id AS CHAR) AS mainImageId
        FROM order_item o
        LEFT JOIN merchant_goods mg ON mg.id = o.merchant_goods_id AND mg.deleted = 0
        WHERE o.order_id = #{orderId}
        ORDER BY o.id DESC
        """)
    List<CustomerOrderItemRow> selectCustomerOrderItems(@Param("orderId") Long orderId);
}
