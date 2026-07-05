package com.telecom.scm.admin.convert;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.admin.dto.response.AdminUserResponse;
import com.telecom.scm.admin.dto.response.RegistrationApplicationResponse;
import com.telecom.scm.admin.mapper.AdminUserRow;

/**
 * Admin 模块对象转换器。
 *
 * <p>使用 MapStruct 替代手写 new XxxResponse(...) 构造，复杂或 Map 来源的映射通过默认方法实现。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminConvert {

    AdminConvert INSTANCE = Mappers.getMapper(AdminConvert.class);

    /** 将后台用户查询行转换为响应 DTO。 */
    AdminUserResponse toAdminUserResponse(AdminUserRow row);

    /**
     * 将待审核注册申请 Map 行对象转换为响应 DTO。
     *
     * <p>源数据来自 MyBatis {@code resultType="java.util.HashMap"}，字段需显式提取并转字符串。
     */
    default RegistrationApplicationResponse toRegistrationApplicationResponse(
            Map<String, Object> row) {
        if (row == null) {
            return null;
        }
        return new RegistrationApplicationResponse(
                String.valueOf(row.get("id")),
                String.valueOf(row.get("username")),
                String.valueOf(row.get("identityType")),
                String.valueOf(row.get("displayName")),
                String.valueOf(row.get("phone")),
                String.valueOf(row.get("email")),
                String.valueOf(row.get("status")),
                String.valueOf(row.get("createdAt")));
    }
}
