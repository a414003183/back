package com.telecom.scm.aftersale.service;

import com.telecom.scm.aftersale.dto.response.AftersaleSummaryResponse;
import com.telecom.scm.common.api.PageResult;

public interface AftersaleQueryService {

    PageResult<AftersaleSummaryResponse> currentCustomerAftersales(
            String username, int page, int pageSize);

    PageResult<AftersaleSummaryResponse> currentMerchantAftersales(
            String username, int page, int pageSize);
}
