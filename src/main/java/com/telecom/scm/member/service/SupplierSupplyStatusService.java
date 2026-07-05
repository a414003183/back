package com.telecom.scm.member.service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.mapper.row.SupplyStatusRow;

public interface SupplierSupplyStatusService {
    PageResult<SupplyStatusRow> getSupplyStatus(String username, int page, int pageSize);
}
