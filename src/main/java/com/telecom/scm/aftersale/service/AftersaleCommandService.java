package com.telecom.scm.aftersale.service;

import com.telecom.scm.aftersale.dto.request.ApproveAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.CreateAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.RefundAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.RegisterReturnShipmentRequest;
import com.telecom.scm.aftersale.dto.request.RejectAftersaleRequest;
import com.telecom.scm.aftersale.dto.response.AftersaleActionResponse;

public interface AftersaleCommandService {

    AftersaleActionResponse createCustomerAftersale(
            String username, CreateAftersaleRequest request);

    AftersaleActionResponse registerCustomerReturnShipment(
            String username, Long aftersaleId, RegisterReturnShipmentRequest request);

    AftersaleActionResponse approveMerchantAftersale(
            String username, Long aftersaleId, ApproveAftersaleRequest request);

    AftersaleActionResponse rejectMerchantAftersale(
            String username, Long aftersaleId, RejectAftersaleRequest request);

    AftersaleActionResponse receiveMerchantReturn(String username, Long aftersaleId);

    AftersaleActionResponse refundMerchantAftersale(
            String username, Long aftersaleId, RefundAftersaleRequest request);
}
