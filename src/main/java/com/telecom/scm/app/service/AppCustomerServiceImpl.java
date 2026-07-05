package com.telecom.scm.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.app.convert.AppConvert;
import com.telecom.scm.app.dto.request.UpdateCustomerProfileRequest;
import com.telecom.scm.app.dto.response.CustomerAddressResponse;
import com.telecom.scm.app.dto.response.CustomerOrderDetailResponse;
import com.telecom.scm.app.dto.response.CustomerProfileResponse;
import com.telecom.scm.app.mapper.AppCustomerMapper;
import com.telecom.scm.app.mapper.CustomerAddressRow;
import com.telecom.scm.app.mapper.CustomerOrderDetailRow;
import com.telecom.scm.app.mapper.CustomerOrderItemRow;
import com.telecom.scm.app.mapper.CustomerProfileRow;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.order.mapper.OrderQueryMapper;

@Service
public class AppCustomerServiceImpl implements AppCustomerService {

    private final AppCustomerMapper appCustomerMapper;
    private final OrderQueryMapper orderQueryMapper;

    public AppCustomerServiceImpl(
            AppCustomerMapper appCustomerMapper, OrderQueryMapper orderQueryMapper) {
        this.appCustomerMapper = appCustomerMapper;
        this.orderQueryMapper = orderQueryMapper;
    }

    @Override
    public CustomerProfileResponse getCustomerProfile(String username) {
        CustomerProfileRow profile = appCustomerMapper.selectCustomerProfile(username);
        if (profile == null) {
            throw new BusinessException(404, "客户资料不存在");
        }

        CustomerAddressRow address =
                appCustomerMapper.selectDefaultAddress(profile.getCustomerId());
        CustomerAddressResponse defaultAddress =
                address == null
                        ? new CustomerAddressResponse("", "", "", "", "", "")
                        : AppConvert.INSTANCE.toCustomerAddressResponse(address);

        return AppConvert.INSTANCE.toCustomerProfileResponse(profile, defaultAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerProfile(String username, UpdateCustomerProfileRequest request) {
        String contactName = request.contactName();
        String contactPhone = request.contactPhone();
        if (contactName != null || contactPhone != null) {
            appCustomerMapper.updateCustomerProfile(
                    username,
                    contactName != null ? contactName : "",
                    contactPhone != null ? contactPhone : "");
        }
    }

    @Override
    public CustomerOrderDetailResponse getCustomerOrderDetail(String username, String orderIdOrNo) {
        Long orderId;
        try {
            orderId = Long.valueOf(orderIdOrNo);
        } catch (NumberFormatException e) {
            orderId = orderQueryMapper.selectOrderIdByOrderNo(orderIdOrNo);
            if (orderId == null) {
                throw new BusinessException(404, "订单不存在");
            }
        }
        CustomerOrderDetailRow detail =
                appCustomerMapper.selectCustomerOrderDetail(username, orderId);
        if (detail == null) {
            throw new BusinessException(404, "订单不存在或无权查看");
        }
        List<CustomerOrderItemRow> items = appCustomerMapper.selectCustomerOrderItems(orderId);
        return AppConvert.INSTANCE.toCustomerOrderDetailResponse(detail, items);
    }
}
