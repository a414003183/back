package com.telecom.scm.security.mapper;

import java.util.List;
import java.util.Map;

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
import com.telecom.scm.security.entity.UserIdentityBindingEntity;

@Mapper
public interface IdentityManagementMapper {

    Map<String, Object> selectUserAccountByUsername(@Param("username") String username);

    @Select(
            """
        SELECT
            binding.id AS bindingId,
            binding.user_id AS userId,
            binding.identity_type AS identityType,
            binding.identity_ref_id AS memberId,
            binding.status AS status,
            binding.is_default AS defaultIdentity
        FROM user_identity_binding binding
        WHERE binding.deleted = 0
            AND binding.user_id = #{userId}
            AND binding.identity_type = #{identityType}
        ORDER BY binding.is_default DESC, binding.id DESC
        LIMIT 1
        """)
    Map<String, Object> selectIdentityBindingByUserAndType(
            @Param("userId") Long userId, @Param("identityType") String identityType);

    Map<String, Object> selectIdentityBindingById(@Param("bindingId") Long bindingId);

    @Select(
            """
        SELECT id
        FROM sys_role
        WHERE deleted = 0
            AND status = 'ENABLED'
            AND role_code = #{roleCode}
        LIMIT 1
        """)
    Long selectRoleIdByCode(@Param("roleCode") String roleCode);

    @Select(
            """
        SELECT COUNT(1)
        FROM sys_user_role
        WHERE user_id = #{userId}
            AND role_id = #{roleId}
        """)
    int countUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select(
            """
        SELECT COUNT(1)
        FROM user_identity_binding
        WHERE deleted = 0
            AND user_id = #{userId}
            AND status = #{status}
        """)
    int countIdentityBindingsByUserAndStatus(
            @Param("userId") Long userId, @Param("status") String status);

    int insertUser(UserEntity param);

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

    int insertMemberAccount(MemberAccountEntity param);

    int insertCustomerInfo(CustomerInfoEntity param);

    int insertMerchantInfo(MerchantInfoEntity param);

    int insertSupplierInfo(SupplierInfoEntity param);

    int insertUserIdentityBinding(UserIdentityBindingEntity param);

    @Update(
            """
        UPDATE sys_user
        SET member_id = #{memberId},
            last_active_identity_type = #{identityType},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{userId}
            AND deleted = 0
        """)
    int updateUserActiveIdentity(
            @Param("userId") Long userId,
            @Param("memberId") Long memberId,
            @Param("identityType") String identityType,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE sys_user
        SET register_status = #{registerStatus},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{userId}
            AND deleted = 0
        """)
    int updateUserRegisterStatus(
            @Param("userId") Long userId,
            @Param("registerStatus") String registerStatus,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE user_identity_binding
        SET status = #{status},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{bindingId}
            AND deleted = 0
        """)
    int updateIdentityBindingStatus(
            @Param("bindingId") Long bindingId,
            @Param("status") String status,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE member_account
        SET status = #{status},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{memberId}
            AND deleted = 0
        """)
    int updateMemberAccountStatus(
            @Param("memberId") Long memberId,
            @Param("status") String status,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE customer_info
        SET status = #{status},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE member_id = #{memberId}
            AND deleted = 0
        """)
    int updateCustomerInfoStatusByMemberId(
            @Param("memberId") Long memberId,
            @Param("status") String status,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE merchant_info
        SET status = #{status},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE member_id = #{memberId}
            AND deleted = 0
        """)
    int updateMerchantInfoStatusByMemberId(
            @Param("memberId") Long memberId,
            @Param("status") String status,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE supplier_info
        SET status = #{status},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE member_id = #{memberId}
            AND deleted = 0
        """)
    int updateSupplierInfoStatusByMemberId(
            @Param("memberId") Long memberId,
            @Param("status") String status,
            @Param("updatedBy") Long updatedBy);

    List<Map<String, Object>> selectPendingRegistrationRows(
            @Param("offset") int offset, @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM user_identity_binding binding
        JOIN sys_user u ON u.id = binding.user_id
            AND u.deleted = 0
        LEFT JOIN member_account member_account ON member_account.id = binding.identity_ref_id
            AND member_account.deleted = 0
        WHERE binding.deleted = 0
            AND binding.status = 'PENDING'
        """)
    long countPendingRegistrations();

    @Select(
            """
        SELECT shop_name AS displayName
        FROM merchant_info
        WHERE member_id = #{memberId}
            AND deleted = 0
        LIMIT 1
        """)
    String selectMerchantDisplayNameByMemberId(@Param("memberId") Long memberId);

    @Select(
            """
        SELECT supplier_name AS displayName
        FROM supplier_info
        WHERE member_id = #{memberId}
            AND deleted = 0
        LIMIT 1
        """)
    String selectSupplierDisplayNameByMemberId(@Param("memberId") Long memberId);

    @Select(
            """
        SELECT company_name AS displayName
        FROM customer_info
        WHERE member_id = #{memberId}
            AND deleted = 0
        LIMIT 1
        """)
    String selectCustomerDisplayNameByMemberId(@Param("memberId") Long memberId);

    @Update(
            """
        UPDATE member_account
        SET member_name = #{memberName},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{memberId}
            AND deleted = 0
        """)
    int updateMemberAccountName(
            @Param("memberId") Long memberId,
            @Param("memberName") String memberName,
            @Param("updatedBy") Long updatedBy);

    @Update(
            """
        UPDATE sys_user
        SET nick_name = #{nickName},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{userId}
            AND deleted = 0
        """)
    int updateUserNickName(
            @Param("userId") Long userId,
            @Param("nickName") String nickName,
            @Param("updatedBy") Long updatedBy);
}
