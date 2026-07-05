package com.telecom.scm.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.order.dto.response.OrderTimelineEventResponse;
import com.telecom.scm.order.service.OrderQueryService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.PermissionGuard;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderQueryService orderService;
    private final PermissionGuard permissionGuard;

    public AdminOrderController(OrderQueryService orderService, PermissionGuard permissionGuard) {
        this.orderService = orderService;
        this.permissionGuard = permissionGuard;
    }

    @GetMapping("/{orderId}/timeline")
    public ApiResponse<List<OrderTimelineEventResponse>> adminOrderTimeline(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        permissionGuard.require(user, "admin:dashboard:view");
        return ApiResponse.success(orderService.adminOrderTimeline(orderId));
    }
}
