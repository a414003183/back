package com.telecom.scm.security.service;

import java.util.List;

public final class PermissionCatalog {

    private PermissionCatalog() {}

    public static List<String> defaultPermissionsForRole(String roleCode) {
        return switch (roleCode) {
            case "CUSTOMER" ->
                    List.of(
                            "customer:dashboard:view",
                            "customer:profile:view",
                            "customer:order:view",
                            "customer:aftersale:view",
                            "customer:point:view",
                            "customer:referral:view");
            case "MERCHANT" ->
                    List.of(
                            "merchant:dashboard:view",
                            "merchant:profile:view",
                            "merchant:goods:view",
                            "merchant:goods:edit",
                            "merchant:supply:view",
                            "merchant:supply:import",
                            "merchant:pricing:customer:view",
                            "merchant:order:view",
                            "merchant:order:approve",
                            "merchant:order:ship",
                            "merchant:aftersale:view",
                            "merchant:aftersale:approve",
                            "merchant:aftersale:reject",
                            "merchant:aftersale:refund",
                            "merchant:report:view",
                            "merchant:report:export",
                            "merchant:cost:view",
                            "merchant:profit:view");
            case "SUPPLIER" ->
                    List.of(
                            "supplier:dashboard:view",
                            "supplier:profile:view",
                            "supplier:product:view",
                            "supplier:stock:view",
                            "supplier:cooperation:view",
                            "supplier:cooperation:edit",
                            "supplier:authorization:edit");
            case "ADMIN" ->
                    List.of(
                            "admin:dashboard:view",
                            "admin:user:view",
                            "admin:role:view",
                            "admin:role:assign",
                            "admin:registration:view",
                            "admin:registration:review",
                            "admin:customer-level:view",
                            "admin:customer-level:edit",
                            "admin:product:view",
                            "admin:product:edit",
                            "admin:menu:view",
                            "admin:menu:assign",
                            "admin:login-log:view",
                            "admin:operation-log:view",
                            "admin:transfer:view",
                            "admin:import:run",
                            "admin:export:view");
            default -> List.of();
        };
    }
}
