package com.telecom.scm.pricing.service;

import java.util.List;

import com.telecom.scm.admin.dto.request.SaveCustomerLevelConfigRequest;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.pricing.mapper.row.CustomerLevelConfigRow;
import com.telecom.scm.pricing.mapper.row.MemberLevelOptionRow;

public interface CustomerLevelService {

    PageResult<CustomerLevelConfigRow> configs(int page, int pageSize);

    CustomerLevelConfigRow saveConfig(Long operatorId, SaveCustomerLevelConfigRequest request);

    List<MemberLevelOptionRow> levelOptions();

    CustomerLevelConfigRow updateStatus(String levelCode, AccountStatusEnum status);

    void refreshCustomerLevelOnOrderFinished(
            Long customerId, Long operatorId, String operatorName, String orderNo);
}
