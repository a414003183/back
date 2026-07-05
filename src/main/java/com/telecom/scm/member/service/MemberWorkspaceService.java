package com.telecom.scm.member.service;

import org.springframework.http.ResponseEntity;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.SaveCustomerProfileRequest;
import com.telecom.scm.member.dto.request.SaveMerchantProfileRequest;
import com.telecom.scm.member.dto.request.SaveSupplierProfileRequest;
import com.telecom.scm.member.dto.response.MerchantReportResponse;
import com.telecom.scm.member.mapper.row.CustomerProfileRow;
import com.telecom.scm.member.mapper.row.MerchantProfileRow;
import com.telecom.scm.member.mapper.row.MerchantShipmentRow;
import com.telecom.scm.member.mapper.row.SupplierCooperationRow;
import com.telecom.scm.member.mapper.row.SupplierProfileRow;
import com.telecom.scm.member.mapper.row.SupplierStockRow;

public interface MemberWorkspaceService {
    CustomerProfileRow customerProfile(String username);

    CustomerProfileRow saveCustomerProfile(String username, SaveCustomerProfileRequest request);

    MerchantProfileRow merchantProfile(String username);

    MerchantProfileRow saveMerchantProfile(String username, SaveMerchantProfileRequest request);

    SupplierProfileRow supplierProfile(String username);

    SupplierProfileRow saveSupplierProfile(String username, SaveSupplierProfileRequest request);

    PageResult<SupplierStockRow> supplierStocks(String username, int page, int pageSize);

    PageResult<SupplierCooperationRow> supplierCooperation(String username, int page, int pageSize);

    PageResult<MerchantShipmentRow> merchantShipments(String username, int page, int pageSize);

    MerchantReportResponse merchantReports(
            String username, boolean includeProfitFields, String startDate, String endDate);

    ResponseEntity<byte[]> exportMerchantReport(
            String username,
            boolean includeProfitFields,
            String type,
            String startDate,
            String endDate);

    String getCustomerLevelByUsername(String username);
}
