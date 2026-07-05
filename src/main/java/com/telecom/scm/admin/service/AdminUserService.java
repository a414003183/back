package com.telecom.scm.admin.service;

import com.telecom.scm.admin.dto.request.AssignAdminUserRoleRequest;
import com.telecom.scm.admin.dto.request.CreateAdminUserRequest;
import com.telecom.scm.admin.dto.request.ResetAdminUserPasswordRequest;
import com.telecom.scm.admin.dto.request.UpdateAdminUserStatusRequest;
import com.telecom.scm.admin.dto.response.AdminUserResponse;
import com.telecom.scm.common.api.PageResult;

public interface AdminUserService {

    PageResult<AdminUserResponse> listUsers(int page, int pageSize);

    AdminUserResponse createUser(Long operatorId, CreateAdminUserRequest request);

    AdminUserResponse assignRole(Long operatorId, Long userId, AssignAdminUserRoleRequest request);

    AdminUserResponse updateStatus(
            Long operatorId, Long userId, UpdateAdminUserStatusRequest request);

    void resetPassword(Long operatorId, Long userId, ResetAdminUserPasswordRequest request);

    void deleteUser(Long operatorId, Long userId);
}
