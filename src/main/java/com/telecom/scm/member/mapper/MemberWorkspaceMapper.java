package com.telecom.scm.member.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.member.entity.CustomerProfileEntity;
import com.telecom.scm.member.entity.MemberAddressEntity;
import com.telecom.scm.member.entity.MerchantProfileEntity;
import com.telecom.scm.member.entity.SupplierProfileEntity;
import com.telecom.scm.member.mapper.row.CustomerContextRow;
import com.telecom.scm.member.mapper.row.CustomerProfileRow;
import com.telecom.scm.member.mapper.row.MerchantContextRow;
import com.telecom.scm.member.mapper.row.MerchantCustomerRankingRow;
import com.telecom.scm.member.mapper.row.MerchantProductRankingRow;
import com.telecom.scm.member.mapper.row.MerchantProfileRow;
import com.telecom.scm.member.mapper.row.MerchantReportSummaryRow;
import com.telecom.scm.member.mapper.row.MerchantShipmentRow;
import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplierCooperationRow;
import com.telecom.scm.member.mapper.row.SupplierProfileRow;
import com.telecom.scm.member.mapper.row.SupplierStockRow;

@Mapper
public interface MemberWorkspaceMapper {

    CustomerProfileRow selectCustomerProfile(@Param("username") String username);

    MerchantProfileRow selectMerchantProfile(@Param("username") String username);

    SupplierProfileRow selectSupplierProfile(@Param("username") String username);

    List<SupplierStockRow> selectSupplierStockRows(
            @Param("username") String username,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countSupplierStockRows(@Param("username") String username);

    List<SupplierCooperationRow> selectSupplierCooperationRows(
            @Param("username") String username,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countSupplierCooperationRows(@Param("username") String username);

    List<MerchantShipmentRow> selectMerchantShipmentRows(
            @Param("username") String username,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countMerchantShipmentRows(@Param("username") String username);

    @Select(
            """
        SELECT merchant.id
        FROM member_account ma
        JOIN sys_user u ON u.member_id = ma.id
        JOIN merchant_info merchant ON merchant.member_id = ma.id
        WHERE u.deleted = 0
            AND u.status = 'ENABLED'
            AND u.username = #{username}
            AND ma.deleted = 0
            AND ma.status = 'ENABLED'
            AND merchant.deleted = 0
        LIMIT 1
        """)
    Long selectMerchantIdByUsername(@Param("username") String username);

    MerchantReportSummaryRow selectMerchantReportSummary(
            @Param("merchantId") Long merchantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    List<MerchantCustomerRankingRow> selectMerchantCustomerRanking(
            @Param("merchantId") Long merchantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    List<MerchantProductRankingRow> selectMerchantProductRanking(
            @Param("merchantId") Long merchantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Select(
            """
        SELECT
            u.id AS userId,
            member_account.id AS memberId,
            customer.id AS customerId,
            customer.member_level AS memberLevel,
            customer.accumulated_paid_amount AS accumulatedPaidAmount
        FROM sys_user u
        JOIN member_account member_account ON member_account.id = u.member_id
            AND member_account.deleted = 0
            AND member_account.status = 'ENABLED'
            AND member_account.member_type = 'CUSTOMER'
        JOIN customer_info customer ON customer.member_id = member_account.id
            AND customer.deleted = 0
            AND customer.status = 'ENABLED'
        WHERE u.deleted = 0
            AND u.status = 'ENABLED'
            AND u.username = #{username}
        LIMIT 1
        """)
    CustomerContextRow selectCustomerContextByUsername(@Param("username") String username);

    @Select(
            """
        SELECT
            u.id AS userId,
            member_account.id AS memberId,
            customer.id AS customerId,
            customer.member_level AS memberLevel,
            customer.accumulated_paid_amount AS accumulatedPaidAmount
        FROM member_account member_account
        JOIN customer_info customer ON customer.member_id = member_account.id
            AND customer.deleted = 0
            AND customer.status = 'ENABLED'
        LEFT JOIN sys_user u ON u.member_id = member_account.id
            AND u.deleted = 0
            AND u.status = 'ENABLED'
        WHERE member_account.deleted = 0
            AND member_account.status = 'ENABLED'
            AND member_account.member_type = 'CUSTOMER'
            AND member_account.id = #{memberId}
        LIMIT 1
        """)
    CustomerContextRow selectCustomerContextByMemberId(@Param("memberId") Long memberId);

    @Update(
            """
        UPDATE customer_info
        SET company_name = #{companyName},
            contact_name = #{contactName},
            contact_phone = #{contactPhone},
            invoice_title = #{invoiceTitle},
            tax_no = #{taxNo},
            bank_name = #{bankName},
            bank_account = #{bankAccount},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{customerId}
            AND deleted = 0
        """)
    int updateCustomerProfile(CustomerProfileEntity payload);

    @Update(
            """
        UPDATE member_address
        SET province = #{receiverProvince},
            city = #{receiverCity},
            district = #{receiverDistrict},
            detail_address = #{receiverAddress},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE member_id = #{memberId}
            AND address_type = 'RECEIVE'
            AND is_default = 1
            AND deleted = 0
        """)
    int updateDefaultReceiveAddress(MemberAddressEntity payload);

    int insertDefaultReceiveAddress(MemberAddressEntity payload);

    MerchantContextRow selectMerchantContextByUsername(@Param("username") String username);

    @Update(
            """
        UPDATE merchant_info
        SET shop_name = #{shopName},
            license_no = #{licenseNo},
            contact_name = #{contactName},
            contact_phone = #{contactPhone},
            shop_desc = #{shopDesc},
            status = #{status},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{merchantId}
            AND deleted = 0
        """)
    int updateMerchantProfile(MerchantProfileEntity payload);

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

    @Update(
            """
        UPDATE supplier_info
        SET supplier_name = #{supplierName},
            contact_name = #{contactName},
            contact_phone = #{contactPhone},
            supply_desc = #{supplyDesc},
            qualification_file_id = #{qualificationFileId},
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{supplierId}
            AND deleted = 0
        """)
    int updateSupplierProfile(SupplierProfileEntity payload);
}
