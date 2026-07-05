package com.telecom.scm.member.service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.member.dto.request.ApplyMerchantAuthorizationRequest;
import com.telecom.scm.member.dto.request.ApplyMerchantRelationRequest;
import com.telecom.scm.member.dto.request.ImportMerchantSupplyRequest;
import com.telecom.scm.member.dto.request.SaveSupplierAuthorizationRequest;
import com.telecom.scm.member.dto.request.SaveSupplierRelationRequest;
import com.telecom.scm.member.dto.response.OperationMessageResponse;
import com.telecom.scm.member.dto.response.SupplierOptionsResponse;
import com.telecom.scm.member.mapper.row.MerchantGoodsRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyCatalogRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyRelationRow;
import com.telecom.scm.member.mapper.row.PlatformSupplierRow;
import com.telecom.scm.member.mapper.row.SupplierAuthorizationRow;
import com.telecom.scm.member.mapper.row.SupplierRelationRow;

public interface SupplyCollaborationService {
    PageResult<SupplierRelationRow> supplierRelations(
            String username, int page, int pageSize, String status);

    PageResult<SupplierAuthorizationRow> supplierAuthorizations(
            String username, int page, int pageSize);

    SupplierOptionsResponse supplierOptions(String username);

    OperationMessageResponse saveSupplierRelation(
            String username, SaveSupplierRelationRequest request);

    OperationMessageResponse saveSupplierAuthorization(
            String username, SaveSupplierAuthorizationRequest request);

    PageResult<MerchantSupplyRelationRow> merchantSupplyRelations(
            String username, int page, int pageSize);

    PageResult<MerchantSupplyCatalogRow> merchantSupplyCatalog(
            String username, int page, int pageSize);

    PageResult<PlatformSupplierRow> platformSuppliers(String keyword, int page, int pageSize);

    OperationMessageResponse applyMerchantRelation(
            String username, ApplyMerchantRelationRequest request);

    OperationMessageResponse handleSupplierRelation(
            String username, Long relationId, String status, String remark);

    OperationMessageResponse applyMerchantAuthorization(
            String username, ApplyMerchantAuthorizationRequest request);

    OperationMessageResponse handleSupplierAuthorization(
            String username, Long authorizationId, String status, String remark);

    MerchantGoodsRow importMerchantSupply(String username, ImportMerchantSupplyRequest request);
}
