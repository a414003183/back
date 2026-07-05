package com.telecom.scm.admin.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminGovernanceMapper {

    List<RoleRow> selectRoleRows(@Param("offset") int offset, @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM sys_role role
        WHERE role.deleted = 0
        """)
    long countRoleRows();

    List<MenuRow> selectMenuRows(@Param("offset") int offset, @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM sys_menu menu
        LEFT JOIN sys_menu parent ON parent.id = menu.parent_id AND parent.deleted = 0
        WHERE menu.deleted = 0
        """)
    long countMenuRows();

    @Select(
            """
        SELECT CAST(menu_id AS CHAR)
        FROM sys_role_menu
        WHERE role_id = #{roleId}
        ORDER BY menu_id ASC
        """)
    List<String> selectRoleMenuIds(@Param("roleId") Long roleId);

    @Delete(
            """
        DELETE FROM sys_role_menu
        WHERE role_id = #{roleId}
        """)
    int deleteRoleMenus(@Param("roleId") Long roleId);

    int insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    List<LoginLogRow> selectLoginLogs(@Param("offset") int offset, @Param("limit") int limit);

    long countLoginLogs();

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            username AS username,
            module_name AS moduleName,
            business_type AS businessType,
            request_uri AS requestUri,
            operation_status AS operationStatus,
            response_message AS responseMessage,
            DATE_FORMAT(operation_time, '%Y-%m-%d %H:%i:%s') AS operationTime
        FROM sys_operation_log
        ORDER BY operation_time DESC, id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<OperationLogRow> selectOperationLogs(
            @Param("offset") int offset, @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM sys_operation_log
        """)
    long countOperationLogs();

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            username AS username,
            module_name AS moduleName,
            business_type AS businessType,
            request_uri AS requestUri,
            operation_status AS operationStatus,
            response_message AS responseMessage,
            DATE_FORMAT(operation_time, '%Y-%m-%d %H:%i:%s') AS operationTime
        FROM sys_operation_log
        ORDER BY operation_time DESC, id DESC
        LIMIT #{limit}
        """)
    List<Map<String, Object>> selectRecentOperationLogs(@Param("limit") int limit);

    List<ImportExportLogRow> selectImportExportLogsByType(
            @Param("type") String type, @Param("offset") int offset, @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM sys_operation_log
        WHERE business_type IN ('IMPORT_DATA', 'EXPORT_DATA')
          AND (request_uri LIKE CONCAT('%type=', #{type}, '%') OR request_params LIKE CONCAT('%{\"type\":\"', #{type}, '\"}%'))
        """)
    long countImportExportLogsByType(@Param("type") String type);

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            username,
            user_type AS userType,
            phone,
            email,
            status
        FROM sys_user
        WHERE deleted = 0
        ORDER BY id DESC
        """)
    List<ExportUserRow> selectExportUsers();

    @Select(
            """
        SELECT
            CAST(customer.id AS CHAR) AS id,
            customer.company_name AS companyName,
            customer.member_level AS memberLevel,
            customer.contact_name AS contactName,
            customer.contact_phone AS contactPhone,
            customer.invite_code AS inviteCode,
            customer.status AS status
        FROM customer_info customer
        WHERE customer.deleted = 0
        ORDER BY customer.id DESC
        """)
    List<ExportCustomerRow> selectExportCustomers();

    @Select(
            """
        SELECT
            CAST(supplier.id AS CHAR) AS id,
            supplier.supplier_name AS supplierName,
            supplier.contact_name AS contactName,
            supplier.contact_phone AS contactPhone,
            supplier.status AS status
        FROM supplier_info supplier
        WHERE supplier.deleted = 0
        ORDER BY supplier.id DESC
        """)
    List<ExportSupplierRow> selectExportSuppliers();

    List<ExportProductRow> selectExportProducts();

    int updateImportedUser(
            @Param("id") Long id,
            @Param("username") String username,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("status") String status);

    int updateImportedCustomer(
            @Param("id") Long id,
            @Param("companyName") String companyName,
            @Param("memberLevel") String memberLevel,
            @Param("contactName") String contactName,
            @Param("contactPhone") String contactPhone,
            @Param("inviteCode") String inviteCode,
            @Param("status") String status);

    int updateImportedSupplier(
            @Param("id") Long id,
            @Param("supplierName") String supplierName,
            @Param("contactName") String contactName,
            @Param("contactPhone") String contactPhone,
            @Param("status") String status);

    int updateImportedProduct(
            @Param("id") Long id,
            @Param("spuName") String spuName,
            @Param("skuName") String skuName,
            @Param("specText") String specText,
            @Param("basePrice") java.math.BigDecimal basePrice,
            @Param("costPrice") java.math.BigDecimal costPrice,
            @Param("stockQty") Integer stockQty,
            @Param("saleStatus") String saleStatus);

    // 用户菜单权限相关方法
    @Select(
            """
        SELECT CAST(menu_id AS CHAR)
        FROM sys_user_menu
        WHERE user_id = #{userId}
        ORDER BY menu_id ASC
        """)
    List<String> selectUserMenuIds(@Param("userId") Long userId);

    @Delete(
            """
        DELETE FROM sys_user_menu
        WHERE user_id = #{userId}
        """)
    int deleteUserMenus(@Param("userId") Long userId);

    int insertUserMenus(@Param("userId") Long userId, @Param("menuIds") List<Long> menuIds);

    @Select(
            """
        SELECT CAST(id AS CHAR) AS id
        FROM sys_user
        WHERE deleted = 0
        ORDER BY id DESC
        """)
    List<Map<String, Object>> selectUserIds();

    @Update(
            """
        UPDATE sys_role
        SET status = #{status}, updated_time = CURRENT_TIMESTAMP
        WHERE id = #{roleId} AND deleted = 0
        """)
    int updateRoleStatus(@Param("roleId") Long roleId, @Param("status") String status);

    @Update(
            """
        UPDATE sys_menu
        SET status = #{status}, updated_time = CURRENT_TIMESTAMP
        WHERE id = #{menuId} AND deleted = 0
        """)
    int updateMenuStatus(@Param("menuId") Long menuId, @Param("status") String status);
}
