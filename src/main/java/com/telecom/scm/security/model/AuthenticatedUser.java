package com.telecom.scm.security.model;

import java.util.List;

public record AuthenticatedUser(
        Long userId,
        String username,
        String passwordHash,
        String role,
        String identityType,
        Long memberId,
        String displayName,
        String route,
        List<String> permissions) {

    public boolean hasPermission(String permissionCode) {
        return permissionCode == null
                || permissionCode.isBlank()
                || permissions != null && permissions.contains(permissionCode);
    }
}
