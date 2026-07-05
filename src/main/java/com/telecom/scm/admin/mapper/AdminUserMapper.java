package com.telecom.scm.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.security.entity.CustomerInfoEntity;
import com.telecom.scm.security.entity.MemberAccountEntity;
import com.telecom.scm.security.entity.MerchantInfoEntity;
import com.telecom.scm.security.entity.SupplierInfoEntity;
import com.telecom.scm.security.entity.UserEntity;

@Mapper
public interface AdminUserMapper {

    List<AdminUserRow> selectAdminUsers(@Param("offset") int offset, @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM sys_user u
        LEFT JOIN sys_user_role ur ON ur.user_id = u.id
        LEFT JOIN sys_role r ON r.id = ur.role_id AND r.deleted = 0
        WHERE u.deleted = 0
        """)
    long countAdminUsers();

    AdminUserRow selectAdminUserById(@Param("userId") Long userId);

    UserContextRow selectUserContextById(@Param("userId") Long userId);

    @Select(
            """
        SELECT COUNT(1)
        FROM sys_user
        WHERE deleted = 0
            AND username = #{username}
        """)
    int countUserByUsername(@Param("username") String username);

    @Select(
            """
        SELECT
            id,
            role_code AS roleCode,
            role_name AS roleName,
            status
        FROM sys_role
        WHERE deleted = 0
            AND id = #{roleId}
        LIMIT 1
        """)
    RoleContextRow selectRoleById(@Param("roleId") Long roleId);

    @Select(
            """
        SELECT
            id,
            role_code AS roleCode,
            role_name AS roleName,
            status
        FROM sys_role
        WHERE deleted = 0
            AND role_code = #{roleCode}
        LIMIT 1
        """)
    RoleContextRow selectRoleByCode(@Param("roleCode") String roleCode);

    int insertUser(UserEntity payload);

    @Update(
            """
        UPDATE sys_user
        SET member_id = #{memberId},
            user_type = #{userType},
            nick_name = #{nickName},
            phone = #{phone},
            email = #{email},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{id}
            AND deleted = 0
        """)
    int updateUserRoleProfile(UserEntity payload);

    @Delete(
            """
        DELETE FROM sys_user_role
        WHERE user_id = #{userId}
        """)
    int deleteUserRoles(@Param("userId") Long userId);

    @Insert(
            """
        INSERT INTO sys_user_role (
            user_id,
            role_id
        ) VALUES (
            #{userId},
            #{roleId}
        )
        """)
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Update(
            """
        UPDATE sys_user
        SET status = #{status},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{userId}
            AND deleted = 0
        """)
    int updateUserStatus(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE sys_user
        SET password = #{passwordHash},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{userId}
            AND deleted = 0
        """)
    int updateUserPassword(
            @Param("userId") Long userId,
            @Param("passwordHash") String passwordHash,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE sys_user
        SET deleted = 1,
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{userId}
            AND deleted = 0
        """)
    int deleteUser(@Param("userId") Long userId, @Param("updatedBy") Long updatedBy);

    int insertMemberAccount(MemberAccountEntity payload);

    @Update(
            """
        UPDATE member_account
        SET member_type = #{memberType},
            member_name = #{memberName},
            phone = #{phone},
            email = #{email},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{id}
            AND deleted = 0
        """)
    int updateMemberAccount(MemberAccountEntity payload);

    @Select(
            """
        SELECT id
        FROM customer_info
        WHERE deleted = 0
            AND member_id = #{memberId}
        LIMIT 1
        """)
    Long selectCustomerInfoIdByMemberId(@Param("memberId") Long memberId);

    @Select(
            """
        SELECT id
        FROM merchant_info
        WHERE deleted = 0
            AND member_id = #{memberId}
        LIMIT 1
        """)
    Long selectMerchantInfoIdByMemberId(@Param("memberId") Long memberId);

    @Select(
            """
        SELECT id
        FROM supplier_info
        WHERE deleted = 0
            AND member_id = #{memberId}
        LIMIT 1
        """)
    Long selectSupplierInfoIdByMemberId(@Param("memberId") Long memberId);

    int insertCustomerInfo(CustomerInfoEntity payload);

    int insertMerchantInfo(MerchantInfoEntity payload);

    int insertSupplierInfo(SupplierInfoEntity payload);
}
