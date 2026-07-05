package com.telecom.scm.security.service;

import org.springframework.stereotype.Component;

import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.security.model.AuthenticatedUser;

@Component
public class PermissionGuard {

    public void require(AuthenticatedUser user, String permissionCode) {
        if (user == null || !user.hasPermission(permissionCode)) {
            throw new BusinessException(403, "permission denied: " + permissionCode);
        }
    }

    public void requireAny(AuthenticatedUser user, String... permissionCodes) {
        if (user == null || permissionCodes == null || permissionCodes.length == 0) {
            throw new BusinessException(403, "permission denied");
        }
        for (String permissionCode : permissionCodes) {
            if (user.hasPermission(permissionCode)) {
                return;
            }
        }
        throw new BusinessException(403, "permission denied");
    }
}
