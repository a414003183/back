package com.telecom.scm.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.telecom.scm.app.dto.request.UpdateCustomerProfileRequest;
import com.telecom.scm.app.dto.response.CustomerOrderDetailResponse;
import com.telecom.scm.app.dto.response.CustomerProfileResponse;
import com.telecom.scm.app.service.AppCustomerService;
import com.telecom.scm.common.api.ApiResponse;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.order.dto.response.OrderSummaryResponse;
import com.telecom.scm.order.dto.response.OrderTimelineEventResponse;
import com.telecom.scm.order.service.OrderCommandService;
import com.telecom.scm.order.service.OrderQueryService;
import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.support.CurrentUser;

@RestController
@RequestMapping("/api/app/customer")
public class AppCustomerController {

    private final AppCustomerService appCustomerService;
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    public AppCustomerController(
            AppCustomerService appCustomerService,
            OrderQueryService orderQueryService,
            OrderCommandService orderCommandService) {
        this.appCustomerService = appCustomerService;
        this.orderQueryService = orderQueryService;
        this.orderCommandService = orderCommandService;
    }

    @GetMapping("/profile")
    public ApiResponse<CustomerProfileResponse> getProfile(@CurrentUser AuthenticatedUser user) {
        return ApiResponse.success(appCustomerService.getCustomerProfile(user.username()));
    }

    @PostMapping("/profile")
    public ApiResponse<Void> updateProfile(
            @CurrentUser AuthenticatedUser user,
            @RequestBody UpdateCustomerProfileRequest request) {
        appCustomerService.updateCustomerProfile(user.username(), request);
        return ApiResponse.success();
    }

    @GetMapping("/orders")
    public ApiResponse<PageResult<OrderSummaryResponse>> listOrders(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(
                orderQueryService.currentCustomerOrders(user.username(), page, pageSize));
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<CustomerOrderDetailResponse> getOrderDetail(
            @CurrentUser AuthenticatedUser user, @PathVariable String orderId) {
        return ApiResponse.success(
                appCustomerService.getCustomerOrderDetail(user.username(), orderId));
    }

    @GetMapping("/orders/{orderId}/timeline")
    public ApiResponse<List<OrderTimelineEventResponse>> getOrderTimeline(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        return ApiResponse.success(
                orderQueryService.currentCustomerOrderTimeline(user.username(), orderId));
    }

    @PostMapping("/orders/{orderId}/confirm-receive")
    public ApiResponse<Object> confirmReceive(
            @CurrentUser AuthenticatedUser user, @PathVariable Long orderId) {
        return ApiResponse.success(
                orderCommandService.confirmCustomerReceive(user.username(), orderId));
    }
}
