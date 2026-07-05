package com.telecom.scm.member.service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.SaveSupplierProductRequest;
import com.telecom.scm.member.dto.response.SupplierProductOptionsResponse;
import com.telecom.scm.member.mapper.row.SupplierProductRow;

public interface SupplierProductService {
    PageResult<SupplierProductRow> products(String username, int page, int pageSize);

    SupplierProductOptionsResponse options();

    SupplierProductRow createProduct(String username, SaveSupplierProductRequest request);

    SupplierProductRow updateProduct(
            String username, Long skuId, SaveSupplierProductRequest request);

    void deleteProduct(String username, Long skuId);
}
