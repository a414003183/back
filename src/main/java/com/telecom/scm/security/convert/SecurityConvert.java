package com.telecom.scm.security.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.common.enums.UserStatusEnum;
import com.telecom.scm.security.dto.request.RegisterCustomerRequest;
import com.telecom.scm.security.dto.request.RegisterMerchantRequest;
import com.telecom.scm.security.dto.request.RegisterSupplierRequest;
import com.telecom.scm.security.dto.response.CurrentUserMenuItem;
import com.telecom.scm.security.dto.response.UserIdentityOption;
import com.telecom.scm.security.entity.CustomerInfoEntity;
import com.telecom.scm.security.entity.MemberAccountEntity;
import com.telecom.scm.security.entity.MerchantInfoEntity;
import com.telecom.scm.security.entity.SupplierInfoEntity;
import com.telecom.scm.security.entity.UserEntity;
import com.telecom.scm.security.entity.UserIdentityBindingEntity;
import com.telecom.scm.security.mapper.SecurityIdentityRow;
import com.telecom.scm.security.mapper.SecurityUserMenuRow;
import com.telecom.scm.security.mapper.SecurityUserRow;
import com.telecom.scm.security.model.AuthenticatedUser;

/**
 * 安全认证领域对象转换器。
 *
 * <p>使用 MapStruct 将数据库行对象/实体转换为接口响应对象，避免手写 getter/setter 构造。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SecurityConvert {

    SecurityConvert INSTANCE = Mappers.getMapper(SecurityConvert.class);

    /** 将菜单行对象转换为前端菜单项。 */
    @Mapping(target = "visible", expression = "java(Boolean.TRUE.equals(row.getVisible()))")
    CurrentUserMenuItem toCurrentUserMenuItem(SecurityUserMenuRow row);

    /**
     * 将身份行对象转换为前端身份选项。
     *
     * <p>displayName、active、route 均依赖 identityType 计算得出。
     */
    @Mapping(
            target = "displayName",
            expression = "java(translateDisplayName(row.getIdentityType()))")
    @Mapping(
            target = "active",
            expression =
                    "java(row.getIdentityType() != null && row.getIdentityType().equalsIgnoreCase(activeIdentityType))")
    @Mapping(target = "route", expression = "java(resolveRoute(row.getIdentityType()))")
    UserIdentityOption toUserIdentityOption(SecurityIdentityRow row, String activeIdentityType);

    /** 将用户行对象转换为认证用户模型。 */
    @Mapping(target = "role", source = "row.roleCode")
    @Mapping(target = "route", expression = "java(resolveRoute(row.getRoleCode()))")
    AuthenticatedUser toAuthenticatedUser(SecurityUserRow row, List<String> permissions);

    /** 构建用户实体，用于注册时创建新用户。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    UserEntity toUserEntity(
            String username,
            String nickName,
            String userType,
            String phone,
            String email,
            UserStatusEnum status,
            String registerSource,
            String registerStatus,
            String lastActiveIdentityType,
            Long createdBy,
            Long updatedBy);

    /** 构建会员账户实体，用于注册时创建会员。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    MemberAccountEntity toMemberAccountEntity(
            String memberCode,
            String memberType,
            String memberName,
            String phone,
            String email,
            String status,
            String remark,
            Long createdBy,
            Long updatedBy);

    /** 从客户注册请求构建客户信息实体。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "memberId", source = "memberId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "remark", source = "remark")
    @Mapping(target = "createdBy", source = "userId")
    @Mapping(target = "updatedBy", source = "userId")
    @Mapping(
            target = "companyName",
            expression =
                    "java(request.companyName() == null || request.companyName().isBlank() ? null : request.companyName().trim())")
    @Mapping(target = "contactName", expression = "java(request.contactName().trim())")
    @Mapping(target = "contactPhone", expression = "java(request.contactPhone().trim())")
    @Mapping(
            target = "inviteCode",
            expression =
                    "java(request.inviteCode() == null || request.inviteCode().isBlank() ? null : request.inviteCode().trim())")
    @Mapping(target = "memberLevel", constant = "NORMAL")
    @Mapping(target = "pointsBalance", constant = "0")
    @Mapping(target = "accumulatedPoints", constant = "0")
    @Mapping(target = "usedPoints", constant = "0")
    CustomerInfoEntity toCustomerInfoEntity(
            Long memberId,
            String status,
            String remark,
            Long userId,
            RegisterCustomerRequest request);

    /** 从商家注册请求构建商家信息实体。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "memberId", source = "memberId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "remark", source = "remark")
    @Mapping(target = "createdBy", source = "userId")
    @Mapping(target = "updatedBy", source = "userId")
    @Mapping(target = "shopName", expression = "java(request.shopName().trim())")
    @Mapping(target = "contactName", expression = "java(request.contactName().trim())")
    @Mapping(target = "contactPhone", expression = "java(request.contactPhone().trim())")
    MerchantInfoEntity toMerchantInfoEntity(
            Long memberId,
            String status,
            String remark,
            Long userId,
            RegisterMerchantRequest request);

    /** 从供应商注册请求构建供应商信息实体。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "memberId", source = "memberId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "remark", source = "remark")
    @Mapping(target = "createdBy", source = "userId")
    @Mapping(target = "updatedBy", source = "userId")
    @Mapping(target = "supplierName", expression = "java(request.supplierName().trim())")
    @Mapping(target = "contactName", expression = "java(request.contactName().trim())")
    @Mapping(target = "contactPhone", expression = "java(request.contactPhone().trim())")
    SupplierInfoEntity toSupplierInfoEntity(
            Long memberId,
            String status,
            String remark,
            Long userId,
            RegisterSupplierRequest request);

    /** 构建身份绑定实体。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "identityType", source = "identityType")
    @Mapping(target = "memberId", source = "memberId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "defaultIdentity", source = "defaultIdentity")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    UserIdentityBindingEntity toUserIdentityBindingEntity(
            Long userId,
            String identityType,
            Long memberId,
            String status,
            Integer defaultIdentity,
            Long createdBy,
            Long updatedBy);

    /** 根据身份类型返回中文显示名称。 */
    @Named("translateDisplayName")
    default String translateDisplayName(String identityType) {
        if (identityType == null) {
            return "未知";
        }
        String upper = identityType.toUpperCase();
        if (MemberTypeEnum.CUSTOMER.getCode().equals(upper)) {
            return "客户";
        }
        if (MemberTypeEnum.MERCHANT.getCode().equals(upper)) {
            return "商家";
        }
        if (MemberTypeEnum.SUPPLIER.getCode().equals(upper)) {
            return "供应商";
        }
        if (MemberTypeEnum.ADMIN.getCode().equals(upper)) {
            return "管理员";
        }
        return identityType;
    }

    /** 根据身份类型解析默认路由。 */
    @Named("resolveRoute")
    default String resolveRoute(String identityType) {
        if (MemberTypeEnum.CUSTOMER.getCode().equals(identityType)) {
            return "/member/customer/dashboard";
        }
        if (MemberTypeEnum.MERCHANT.getCode().equals(identityType)) {
            return "/member/merchant/dashboard";
        }
        if (MemberTypeEnum.SUPPLIER.getCode().equals(identityType)) {
            return "/member/supplier/dashboard";
        }
        if (MemberTypeEnum.ADMIN.getCode().equals(identityType)) {
            return "/admin/dashboard";
        }
        return "/";
    }
}
