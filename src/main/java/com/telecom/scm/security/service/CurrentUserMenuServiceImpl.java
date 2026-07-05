package com.telecom.scm.security.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.security.convert.SecurityConvert;
import com.telecom.scm.security.dto.response.CurrentUserMenuItem;
import com.telecom.scm.security.mapper.SecurityUserMapper;
import com.telecom.scm.security.mapper.SecurityUserMenuRow;
import com.telecom.scm.security.model.AuthenticatedUser;

@Service
public class CurrentUserMenuServiceImpl implements CurrentUserMenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserMenuServiceImpl.class);

    private final SecurityUserMapper securityUserMapper;

    public CurrentUserMenuServiceImpl(SecurityUserMapper securityUserMapper) {
        this.securityUserMapper = securityUserMapper;
    }

    @Override
    public List<CurrentUserMenuItem> listMenus(AuthenticatedUser user) {
        LOGGER.info("listMenus called with username={}, role={}", user.username(), user.role());

        List<SecurityUserMenuRow> roleMenus =
                securityUserMapper.selectRouteMenusByUsernameAndRole(user.username(), user.role());
        LOGGER.info("Query returned {} rows", roleMenus.size());

        List<CurrentUserMenuItem> menus;

        // Fallback: Use PermissionMenuCatalog for demo/development
        if (roleMenus.isEmpty()) {
            LOGGER.warn(
                    "No menu rows found for user={}, falling back to PermissionMenuCatalog",
                    user.username());
            // Filter menus by user's role
            String userRole = user.role();
            menus =
                    PermissionMenuCatalog.routeMenusForPermissions(null).stream()
                            .filter(menu -> isMenuForRole(menu, userRole))
                            .toList();
        } else {
            menus =
                    roleMenus.stream()
                            .map(SecurityConvert.INSTANCE::toCurrentUserMenuItem)
                            .toList();
        }

        // If user has no permissions, extract them from menus
        if (user.permissions() == null || user.permissions().isEmpty()) {
            LOGGER.info("User has no permissions, extracting from menus");
            List<String> extractedPermissions =
                    menus.stream()
                            .map(CurrentUserMenuItem::permissionCode)
                            .filter(pc -> pc != null && !pc.isBlank())
                            .toList();
            // This would need to be set via a different mechanism
            // For now, we'll modify the AuthenticatedUser to include these
        }

        return menus;
    }

    private boolean isMenuForRole(CurrentUserMenuItem menu, String userRole) {
        // Check if menu path starts with the role's expected prefix
        if (MemberTypeEnum.MERCHANT.getCode().equals(userRole)) {
            return menu.path().startsWith("/member/merchant/");
        }
        if (MemberTypeEnum.CUSTOMER.getCode().equals(userRole)) {
            return menu.path().startsWith("/member/customer/");
        }
        if (MemberTypeEnum.SUPPLIER.getCode().equals(userRole)) {
            return menu.path().startsWith("/member/supplier/");
        }
        if (MemberTypeEnum.ADMIN.getCode().equals(userRole)) {
            return menu.path().startsWith("/admin/");
        }
        return true;
    }
}
