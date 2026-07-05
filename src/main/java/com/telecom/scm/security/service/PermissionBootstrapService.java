package com.telecom.scm.security.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.security.entity.SysMenuEntity;
import com.telecom.scm.security.mapper.PermissionBootstrapMapper;
import com.telecom.scm.security.service.PermissionMenuCatalog.MenuDefinition;

@Service
public class PermissionBootstrapService implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionBootstrapService.class);
    private static final Long SYSTEM_OPERATOR_ID = 1L;

    private final PermissionBootstrapMapper permissionBootstrapMapper;

    public PermissionBootstrapService(PermissionBootstrapMapper permissionBootstrapMapper) {
        this.permissionBootstrapMapper = permissionBootstrapMapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        syncMenus();
        syncRoleMenus();
    }

    private void syncMenus() {
        for (MenuDefinition definition : PermissionMenuCatalog.definitions()) {
            if (permissionBootstrapMapper.countMenuById(definition.id()) == 0) {
                permissionBootstrapMapper.insertMenu(toInsertParam(definition));
            } else {
                permissionBootstrapMapper.updateMenu(toUpdateParam(definition));
            }
        }
    }

    private void syncRoleMenus() {
        Map<String, Long> roleIdCache = new HashMap<>();
        for (MenuDefinition definition : PermissionMenuCatalog.definitions()) {
            LOGGER.info(
                    "Processing menu: {} for roles: {}",
                    definition.id(),
                    definition.defaultRoleCodes());
            for (String roleCode : definition.defaultRoleCodes()) {
                Long roleId =
                        roleIdCache.computeIfAbsent(
                                roleCode, permissionBootstrapMapper::selectRoleIdByCode);
                LOGGER.info("RoleCode: {} -> RoleId: {}", roleCode, roleId);
                if (roleId == null) {
                    LOGGER.warn(
                            "Skip permission bootstrap because role code {} does not exist",
                            roleCode);
                    continue;
                }
                int existingCount =
                        permissionBootstrapMapper.countRoleMenu(roleId, definition.id());
                LOGGER.info(
                        "Existing role_menu count for roleId={}, menuId={}: {}",
                        roleId,
                        definition.id(),
                        existingCount);
                if (existingCount == 0) {
                    LOGGER.info(
                            "Inserting role_menu: roleId={}, menuId={}", roleId, definition.id());
                    permissionBootstrapMapper.insertRoleMenu(roleId, definition.id());
                }
            }
        }
    }

    private void cleanupLegacyRoleMenus() {
        permissionBootstrapMapper.deleteRoleMenusByRoleCodeAndPaths(
                "MERCHANT",
                List.of("/member/merchant/pricing/level", "/member/merchant/goods/auth"));
    }

    private SysMenuEntity toInsertParam(MenuDefinition definition) {
        SysMenuEntity param = new SysMenuEntity();
        param.setId(definition.id());
        param.setParentId(definition.parentId());
        param.setMenuName(definition.menuName());
        param.setMenuType(definition.menuType());
        param.setPath(definition.path());
        param.setComponent(definition.component());
        param.setPermissionCode(definition.permissionCode());
        param.setIcon(definition.icon());
        param.setSortNo(definition.sortNo());
        param.setVisible(definition.visible() ? 1 : 0);
        param.setStatus(AccountStatusEnum.ENABLED);
        param.setOperatorId(SYSTEM_OPERATOR_ID);
        return param;
    }

    private SysMenuEntity toUpdateParam(MenuDefinition definition) {
        SysMenuEntity param = new SysMenuEntity();
        param.setId(definition.id());
        param.setParentId(definition.parentId());
        param.setMenuName(definition.menuName());
        param.setMenuType(definition.menuType());
        param.setPath(definition.path());
        param.setComponent(definition.component());
        param.setPermissionCode(definition.permissionCode());
        param.setIcon(definition.icon());
        param.setSortNo(definition.sortNo());
        param.setVisible(definition.visible() ? 1 : 0);
        param.setStatus(AccountStatusEnum.ENABLED);
        param.setOperatorId(SYSTEM_OPERATOR_ID);
        return param;
    }
}
