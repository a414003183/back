package com.telecom.scm.order.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.telecom.scm.order.dto.response.OrderContractResponse;

public interface OrderDocumentService {
    ResponseEntity<byte[]> exportCustomerQuote(String username, Long orderId);

    ResponseEntity<byte[]> exportMerchantQuote(String username, Long orderId);

    List<OrderContractResponse> customerOrderContracts(String username, Long orderId);

    OrderContractResponse bindCustomerOrderContract(String username, Long orderId, Long fileId);

    ResponseEntity<byte[]> downloadCustomerOrderContract(
            String username, Long orderId, Long fileId);

    List<OrderContractResponse> merchantOrderContracts(String username, Long orderId);

    ResponseEntity<byte[]> downloadMerchantOrderContract(
            String username, Long orderId, Long fileId);
}
