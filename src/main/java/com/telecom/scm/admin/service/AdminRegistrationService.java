package com.telecom.scm.admin.service;

import com.telecom.scm.admin.dto.response.RegistrationApplicationResponse;
import com.telecom.scm.common.api.PageResult;

public interface AdminRegistrationService {
    PageResult<RegistrationApplicationResponse> listPendingRegistrations(int page, int pageSize);

    void reviewRegistration(Long operatorId, Long bindingId, String action);
}
