package com.telecom.scm.order.service;

import com.telecom.scm.order.dto.request.AdjustMerchantOrderRequest;
import com.telecom.scm.order.dto.request.CreateOrderRequest;
import com.telecom.scm.order.dto.request.MerchantApproveOrderRequest;
import com.telecom.scm.order.dto.request.MerchantShipOrderRequest;
import com.telecom.scm.order.dto.request.RegisterOrderPaymentRequest;
import com.telecom.scm.order.dto.response.OrderActionResponse;
import com.telecom.scm.order.dto.response.OrderCreateResponse;

public interface OrderCommandService {

    OrderCreateResponse createCustomerOrder(
            String username, CreateOrderRequest request, String orderSource);

    default OrderCreateResponse createCustomerOrder(String username, CreateOrderRequest request) {
        return createCustomerOrder(username, request, "WEB_MALL");
    }

    OrderActionResponse registerCustomerPayment(
            String username, Long orderId, RegisterOrderPaymentRequest request);

    OrderActionResponse confirmCustomerReceive(String username, Long orderId);

    OrderActionResponse approveMerchantOrder(
            String username, Long orderId, MerchantApproveOrderRequest request);

    OrderActionResponse adjustMerchantOrder(
            String username, Long orderId, AdjustMerchantOrderRequest request);

    OrderActionResponse shipMerchantOrder(
            String username, Long orderId, MerchantShipOrderRequest request);
}
