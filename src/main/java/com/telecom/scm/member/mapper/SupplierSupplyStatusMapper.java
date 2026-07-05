package com.telecom.scm.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplyStatusRow;

@Mapper
public interface SupplierSupplyStatusMapper {

    @Select(
            """
        SELECT
            u.id AS userId,
            supplier.id AS supplierId
        FROM sys_user u
        JOIN member_account member_account ON member_account.id = u.member_id
            AND member_account.deleted = 0
            AND member_account.status = 'ENABLED'
            AND member_account.member_type = 'SUPPLIER'
        JOIN supplier_info supplier ON supplier.member_id = member_account.id
            AND supplier.deleted = 0
            AND supplier.status = 'ENABLED'
        WHERE u.deleted = 0
            AND u.status = 'ENABLED'
            AND u.username = #{username}
        LIMIT 1
        """)
    SupplierContextRow selectSupplierContextByUsername(@Param("username") String username);

    List<SupplyStatusRow> selectSupplyStatusBySupplierId(
            @Param("supplierId") Long supplierId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countSupplyStatusBySupplierId(@Param("supplierId") Long supplierId);
}
