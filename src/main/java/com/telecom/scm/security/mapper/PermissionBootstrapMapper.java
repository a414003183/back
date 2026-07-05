package com.telecom.scm.security.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.telecom.scm.security.entity.SysMenuEntity;

@Mapper
public interface PermissionBootstrapMapper {

    @Select(
            """
        SELECT COUNT(1)
        FROM sys_menu
        WHERE id = #{menuId}
            AND deleted = 0
        """)
    int countMenuById(@Param("menuId") Long menuId);

    int insertMenu(SysMenuEntity param);

    int updateMenu(SysMenuEntity param);

    @Select(
            """
        SELECT id
        FROM sys_role
        WHERE role_code = #{roleCode}
            AND deleted = 0
        LIMIT 1
        """)
    Long selectRoleIdByCode(@Param("roleCode") String roleCode);

    @Select(
            """
        SELECT COUNT(1)
        FROM sys_role_menu
        WHERE role_id = #{roleId}
            AND menu_id = #{menuId}
        """)
    int countRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    int deleteRoleMenusByRoleCodeAndPaths(
            @Param("roleCode") String roleCode, @Param("paths") List<String> paths);

    @Insert(
            """
        INSERT INTO sys_role_menu (
            role_id,
            menu_id,
            created_time
        ) VALUES (
            #{roleId},
            #{menuId},
            CURRENT_TIMESTAMP
        )
        """)
    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
}
