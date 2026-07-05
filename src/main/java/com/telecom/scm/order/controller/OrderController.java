package com.telecom.scm.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.order.dto.response.OrderTimelineEventResponse;
import com.telecom.scm.order.service.OrderQueryService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderQueryService orderService;
    private final PermissionGuard permissionGuard;

    public OrderController(OrderQueryService orderService, PermissionGuard permissionGuard) {
        this.orderService = orderService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/{orderId}/timeline")
    public ApiResponse<List<OrderTimelineEventResponse>> currentOrderTimeline(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        return switch (user.role()) {
            case "CUSTOMER" -> {
                permissionGuard.require(user, "customer:order:view");
                yield ApiResponse.success(
                        orderService.currentCustomerOrderTimeline(user.username(), orderId));
            }
            case "MERCHANT" -> {
                permissionGuard.require(user, "merchant:order:view");
                yield ApiResponse.success(
                        orderService.currentMerchantOrderTimeline(user.username(), orderId));
            }
            case "ADMIN" -> {
                permissionGuard.require(user, "admin:dashboard:view");
                yield ApiResponse.success(orderService.adminOrderTimeline(orderId));
            }
            default ->
                    throw new BusinessException(
                            403, "current role does not support order timeline");
        };
    }
}
