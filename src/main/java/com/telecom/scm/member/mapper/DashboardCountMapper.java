package com.telecom.scm.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DashboardCountMapper {

    long countCustomerWaitPayOrders(@Param("username") String username);

    long countCustomerWaitReceiveOrders(@Param("username") String username);

    long countCustomerProcessingAftersales(@Param("username") String username);

    long sumCustomerCurrentPoints(@Param("username") String username);

    @Select("SELECT COUNT(1) FROM order_info WHERE deleted = 0 AND order_status = 'WAIT_PAY'")
    long countWaitPayOrders();

    @Select("SELECT COUNT(1) FROM order_info WHERE deleted = 0 AND order_status = 'WAIT_RECEIVE'")
    long countWaitReceiveOrders();

    @Select(
            "SELECT COUNT(1) FROM aftersale_info WHERE deleted = 0 AND aftersale_status IN ('WAIT_AUDIT','WAIT_RETURN','WAIT_RECEIVE','WAIT_REFUND')")
    long countProcessingAftersales();

    @Select("SELECT COALESCE(SUM(current_points), 0) FROM point_account")
    long sumCurrentPoints();

    @Select("SELECT COUNT(1) FROM order_info WHERE deleted = 0")
    long countAllOrders();

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM order_info WHERE deleted = 0")
    double sumOrderAmount();

    @Select("SELECT COUNT(1) FROM order_info WHERE deleted = 0 AND order_status = 'PENDING_AUDIT'")
    long countPendingAuditOrders();

    @Select("SELECT COUNT(1) FROM order_info WHERE deleted = 0 AND order_status = 'WAIT_SHIP'")
    long countPendingShipmentOrders();

    long countMerchantOrders(@Param("username") String username);

    double sumMerchantOrderAmount(@Param("username") String username);

    long countMerchantPendingAuditOrders(@Param("username") String username);

    long countMerchantPendingShipmentOrders(@Param("username") String username);

    long countSupplierSpu(@Param("username") String username);

    long countSupplierSku(@Param("username") String username);

    long countSupplierStockWarnings(@Param("username") String username);

    @Select("SELECT COUNT(1) FROM product_spu WHERE deleted = 0")
    long countSpu();

    @Select("SELECT COUNT(1) FROM product_sku WHERE deleted = 0")
    long countSku();

    @Select("SELECT COUNT(1) FROM product_sku WHERE deleted = 0 AND stock_qty <= safety_stock")
    long countStockWarnings();

    @Select(
            "SELECT COUNT(DISTINCT merchant_id) FROM merchant_supplier_relation WHERE deleted = 0 AND status = 'ACTIVE'")
    long countCooperatingMerchants();
}
