package com.telecom.scm.order.service;

import java.util.List;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.order.dto.response.MerchantOrderDetailResponse;
import com.telecom.scm.order.dto.response.OrderSummaryResponse;
import com.telecom.scm.order.dto.response.OrderTimelineEventResponse;

public interface OrderQueryService {

    PageResult<OrderSummaryResponse> currentCustomerOrders(String username, int page, int pageSize);

    PageResult<OrderSummaryResponse> currentMerchantOrders(String username, int page, int pageSize);

    MerchantOrderDetailResponse currentMerchantOrderDetail(String username, Long orderId);

    List<OrderTimelineEventResponse> currentCustomerOrderTimeline(String username, Long orderId);

    List<OrderTimelineEventResponse> currentMerchantOrderTimeline(String username, Long orderId);

    List<OrderTimelineEventResponse> adminOrderTimeline(Long orderId);

    Long resolveOrderId(String orderIdOrNo);
}
