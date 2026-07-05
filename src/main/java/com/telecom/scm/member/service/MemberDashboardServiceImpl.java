package com.telecom.scm.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.telecom.scm.member.dto.response.MetricCardResponse;
import com.telecom.scm.member.mapper.DashboardCountMapper;

@Service
public class MemberDashboardServiceImpl implements MemberDashboardService {

    private final DashboardCountMapper dashboardCountMapper;

    public MemberDashboardServiceImpl(DashboardCountMapper dashboardCountMapper) {
        this.dashboardCountMapper = dashboardCountMapper;
    }

    @Override
    public List<MetricCardResponse> customerMetrics(String username) {
        return List.of(
                MetricCardResponse.of(
                        "待支付订单",
                        String.valueOf(dashboardCountMapper.countCustomerWaitPayOrders(username)),
                        "来自当前客户订单"),
                MetricCardResponse.of(
                        "待收货订单",
                        String.valueOf(
                                dashboardCountMapper.countCustomerWaitReceiveOrders(username)),
                        "来自当前客户订单"),
                MetricCardResponse.of(
                        "处理中售后",
                        String.valueOf(
                                dashboardCountMapper.countCustomerProcessingAftersales(username)),
                        "来自当前客户售后"),
                MetricCardResponse.of(
                        "当前积分",
                        String.valueOf(dashboardCountMapper.sumCustomerCurrentPoints(username)),
                        "来自当前客户积分账户"));
    }

    @Override
    public List<MetricCardResponse> merchantMetrics(String username) {
        return List.of(
                MetricCardResponse.of(
                        "累计订单数",
                        String.valueOf(dashboardCountMapper.countMerchantOrders(username)),
                        "来自当前商家订单"),
                MetricCardResponse.of(
                        "累计销售额",
                        String.format(
                                "%.0f", dashboardCountMapper.sumMerchantOrderAmount(username)),
                        "来自当前商家订单"),
                MetricCardResponse.of(
                        "待审核订单",
                        String.valueOf(
                                dashboardCountMapper.countMerchantPendingAuditOrders(username)),
                        "来自订单状态"),
                MetricCardResponse.of(
                        "待发货订单",
                        String.valueOf(
                                dashboardCountMapper.countMerchantPendingShipmentOrders(username)),
                        "来自订单状态"));
    }

    @Override
    public List<MetricCardResponse> supplierMetrics(String username) {
        return List.of(
                MetricCardResponse.of(
                        "商品数量",
                        String.valueOf(dashboardCountMapper.countSupplierSpu(username)),
                        "来自商品档案"),
                MetricCardResponse.of(
                        "规格数量",
                        String.valueOf(dashboardCountMapper.countSupplierSku(username)),
                        "来自商品规格"),
                MetricCardResponse.of(
                        "库存预警",
                        String.valueOf(dashboardCountMapper.countSupplierStockWarnings(username)),
                        "库存小于等于安全库存"),
                MetricCardResponse.of(
                        "合作商家",
                        String.valueOf(dashboardCountMapper.countCooperatingMerchants()),
                        "来自商家商品表"));
    }
}
