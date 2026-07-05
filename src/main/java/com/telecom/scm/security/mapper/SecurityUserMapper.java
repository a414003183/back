package com.telecom.scm.security.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SecurityUserMapper {

    SecurityUserRow selectAdminByUsername(@Param("username") String username);

    SecurityUserRow selectMemberIdentityByUsername(
            @Param("username") String username, @Param("identityType") String identityType);

    @Select(
            """
        SELECT last_active_identity_type
        FROM sys_user
        WHERE deleted = 0
            AND (username = #{username} OR phone = #{username})
        LIMIT 1
        """)
    String selectLastActiveIdentityTypeByUsername(@Param("username") String username);

    List<SecurityIdentityRow> selectIdentityRowsByUsername(@Param("username") String username);

    @Select(
            """
        SELECT DISTINCT menu.permission_code
        FROM sys_role role
        JOIN sys_role_menu role_menu ON role_menu.role_id = role.id
        JOIN sys_menu menu ON menu.id = role_menu.menu_id
            AND menu.deleted = 0
            AND menu.status = 'ENABLED'
        WHERE role.deleted = 0
            AND role.status = 'ENABLED'
            AND role.role_code = #{roleCode}
            AND menu.permission_code IS NOT NULL
            AND menu.permission_code <> ''
        ORDER BY menu.permission_code ASC
        """)
    List<String> selectPermissionCodesByUsernameAndRole(
            @Param("username") String username, @Param("roleCode") String roleCode);

    List<SecurityUserMenuRow> selectRouteMenusByUsernameAndRole(
            @Param("username") String username, @Param("roleCode") String roleCode);
}
