package com.telecom.scm.security.service;

import java.util.List;

import com.telecom.scm.security.dto.request.RegisterCustomerRequest;
import com.telecom.scm.security.dto.request.RegisterMerchantRequest;
import com.telecom.scm.security.dto.request.RegisterSupplierRequest;
import com.telecom.scm.security.dto.response.RegistrationSubmissionResponse;
import com.telecom.scm.security.dto.response.UserIdentityOption;
import com.telecom.scm.security.model.AuthenticatedUser;

public interface IdentitySessionService {
    List<UserIdentityOption> listIdentities(AuthenticatedUser user);

    List<UserIdentityOption> listIdentities(String username, String activeIdentityType);

    AuthenticatedUser switchIdentity(AuthenticatedUser currentUser, String identityType);

    void syncActiveIdentity(AuthenticatedUser user);

    RegistrationSubmissionResponse registerCustomer(RegisterCustomerRequest request);

    RegistrationSubmissionResponse registerMerchant(RegisterMerchantRequest request);

    RegistrationSubmissionResponse registerSupplier(RegisterSupplierRequest request);
}
