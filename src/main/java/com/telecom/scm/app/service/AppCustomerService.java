package com.telecom.scm.app.service;

import com.telecom.scm.app.dto.request.UpdateCustomerProfileRequest;
import com.telecom.scm.app.dto.response.CustomerOrderDetailResponse;
import com.telecom.scm.app.dto.response.CustomerProfileResponse;

public interface AppCustomerService {

    CustomerProfileResponse getCustomerProfile(String username);

    void updateCustomerProfile(String username, UpdateCustomerProfileRequest request);

    CustomerOrderDetailResponse getCustomerOrderDetail(String username, String orderIdOrNo);
}
