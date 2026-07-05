package com.telecom.scm.member.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AuthorizationStatusEnum;
import com.telecom.scm.common.enums.CooperationStatusEnum;
import com.telecom.scm.common.enums.RegistrationStatusEnum;
import com.telecom.scm.common.enums.SaleStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.member.convert.SupplierConvert;
import com.telecom.scm.member.dto.request.ApplyMerchantAuthorizationRequest;
import com.telecom.scm.member.dto.request.ApplyMerchantRelationRequest;
import com.telecom.scm.member.dto.request.ImportMerchantSupplyRequest;
import com.telecom.scm.member.dto.request.SaveSupplierAuthorizationRequest;
import com.telecom.scm.member.dto.request.SaveSupplierRelationRequest;
import com.telecom.scm.member.dto.response.OperationMessageResponse;
import com.telecom.scm.member.dto.response.SupplierOptionsResponse;
import com.telecom.scm.member.entity.MerchantGoodsEntity;
import com.telecom.scm.member.entity.MerchantSupplierRelationEntity;
import com.telecom.scm.member.entity.SupplierGoodsAuthorizationEntity;
import com.telecom.scm.member.mapper.MerchantGoodsMapper;
import com.telecom.scm.member.mapper.SupplyCollaborationMapper;
import com.telecom.scm.member.mapper.row.ImportedMerchantGoodsRow;
import com.telecom.scm.member.mapper.row.MerchantContextRow;
import com.telecom.scm.member.mapper.row.MerchantGoodsRow;
import com.telecom.scm.member.mapper.row.MerchantImportAuthorizationRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyCatalogRow;
import com.telecom.scm.member.mapper.row.MerchantSupplyRelationRow;
import com.telecom.scm.member.mapper.row.PlatformSupplierRow;
import com.telecom.scm.member.mapper.row.SupplierAuthorizationBriefRow;
import com.telecom.scm.member.mapper.row.SupplierAuthorizationRow;
import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplierInfoRow;
import com.telecom.scm.member.mapper.row.SupplierOwnedSkuRow;
import com.telecom.scm.member.mapper.row.SupplierRelationDetailRow;
import com.telecom.scm.member.mapper.row.SupplierRelationRow;

@Service
public class SupplyCollaborationServiceImpl implements SupplyCollaborationService {

    private final SupplyCollaborationMapper supplyCollaborationMapper;
    private final MerchantGoodsMapper merchantGoodsMapper;

    public SupplyCollaborationServiceImpl(
            SupplyCollaborationMapper supplyCollaborationMapper,
            MerchantGoodsMapper merchantGoodsMapper) {
        this.supplyCollaborationMapper = supplyCollaborationMapper;
        this.merchantGoodsMapper = merchantGoodsMapper;
    }

    @Override
    public PageResult<SupplierRelationRow> supplierRelations(
            String username, int page, int pageSize, String status) {
        SupplierContextRow context = requireSupplierContext(username);
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        Long supplierId = context.getSupplierId();
        long total = supplyCollaborationMapper.countSupplierRelationRows(supplierId, status);
        List<SupplierRelationRow> rows =
                supplyCollaborationMapper.selectSupplierRelationRows(
                        supplierId, status, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public PageResult<SupplierAuthorizationRow> supplierAuthorizations(
            String username, int page, int pageSize) {
        SupplierContextRow context = requireSupplierContext(username);
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        Long supplierId = context.getSupplierId();
        long total = supplyCollaborationMapper.countSupplierAuthorizationRows(supplierId);
        List<SupplierAuthorizationRow> rows =
                supplyCollaborationMapper.selectSupplierAuthorizationRows(
                        supplierId, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public SupplierOptionsResponse supplierOptions(String username) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        return SupplierConvert.INSTANCE.toSupplierOptionsResponse(
                supplyCollaborationMapper.selectMerchantOptions(),
                supplyCollaborationMapper.selectSupplierSkuOptions(supplierId));
    }

    @Transactional
    @Override
    public OperationMessageResponse saveSupplierRelation(
            String username, SaveSupplierRelationRequest request) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        Long operatorId = context.getUserId();
        String status = normalizeRelationStatus(request.status());

        SupplierRelationDetailRow existing =
                supplyCollaborationMapper.selectSupplierRelationByPair(
                        supplierId, request.merchantId());
        if (existing == null || existing.getId() == null) {
            MerchantSupplierRelationEntity insertParam = new MerchantSupplierRelationEntity();
            insertParam.setMerchantId(request.merchantId());
            insertParam.setSupplierId(supplierId);
            insertParam.setStatus(status);
            insertParam.setRemark(normalizeText(request.remark()));
            insertParam.setCreatedBy(operatorId);
            insertParam.setUpdatedBy(operatorId);
            insertParam.setCooperationStartAt(
                    CooperationStatusEnum.ACTIVE.getCode().equals(status)
                            ? LocalDateTime.now()
                            : null);
            insertParam.setCooperationEndAt(
                    CooperationStatusEnum.ENDED.getCode().equals(status)
                            ? LocalDateTime.now()
                            : null);
            supplyCollaborationMapper.insertSupplierRelation(insertParam);
        } else {
            MerchantSupplierRelationEntity updateParam = new MerchantSupplierRelationEntity();
            updateParam.setRelationId(existing.getId());
            updateParam.setStatus(status);
            updateParam.setRemark(normalizeText(request.remark()));
            updateParam.setUpdatedBy(operatorId);
            if (CooperationStatusEnum.ACTIVE.getCode().equals(status)
                    && !CooperationStatusEnum.ACTIVE.getCode().equals(existing.getStatus())) {
                updateParam.setCooperationStartAt(LocalDateTime.now());
                updateParam.setCooperationEndAt(null);
            } else if (CooperationStatusEnum.ENDED.getCode().equals(status)) {
                updateParam.setCooperationStartAt(null);
                updateParam.setCooperationEndAt(LocalDateTime.now());
            } else {
                updateParam.setCooperationStartAt(
                        CooperationStatusEnum.ACTIVE.getCode().equals(status)
                                ? LocalDateTime.now()
                                : null);
                updateParam.setCooperationEndAt(
                        CooperationStatusEnum.ENDED.getCode().equals(status)
                                ? LocalDateTime.now()
                                : null);
            }
            supplyCollaborationMapper.updateSupplierRelation(updateParam);
        }

        if (CooperationStatusEnum.ENDED.getCode().equals(status)) {
            supplyCollaborationMapper.revokeAuthorizationsByRelation(
                    supplierId, request.merchantId(), operatorId);
            supplyCollaborationMapper.offSaleMerchantGoodsByRelation(
                    supplierId, request.merchantId(), operatorId);
        }
        return OperationMessageResponse.of("supplier relation saved");
    }

    @Transactional
    @Override
    public OperationMessageResponse saveSupplierAuthorization(
            String username, SaveSupplierAuthorizationRequest request) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        Long operatorId = context.getUserId();
        String authStatus = normalizeAuthorizationStatus(request.authStatus());
        SupplierRelationDetailRow relation =
                supplyCollaborationMapper.selectSupplierRelationByPair(
                        supplierId, request.merchantId());
        if (relation == null
                || relation.getId() == null
                || !CooperationStatusEnum.ACTIVE.getCode().equals(relation.getStatus())) {
            throw new BusinessException(400, "supplier relation is not active");
        }
        SupplierOwnedSkuRow sku =
                supplyCollaborationMapper.selectSupplierOwnedSku(
                        supplierId, request.supplierSkuId());
        if (sku == null || sku.getSupplierSkuId() == null) {
            throw new BusinessException(404, "supplier sku not found");
        }

        SupplierAuthorizationBriefRow existing =
                supplyCollaborationMapper.selectSupplierAuthorizationByPair(
                        supplierId, request.merchantId(), request.supplierSkuId());
        if (existing == null || existing.getId() == null) {
            SupplierGoodsAuthorizationEntity insertParam = new SupplierGoodsAuthorizationEntity();
            insertParam.setSupplierId(supplierId);
            insertParam.setMerchantId(request.merchantId());
            insertParam.setSupplierSkuId(request.supplierSkuId());
            insertParam.setAuthStatus(authStatus);
            insertParam.setAuthorizedPrice(request.authorizedPrice());
            insertParam.setAllocatedStockQty(
                    request.allocatedStockQty() != null ? request.allocatedStockQty() : 0);
            insertParam.setAuthorizedAt(
                    AuthorizationStatusEnum.ACTIVE.getCode().equals(authStatus)
                            ? LocalDateTime.now()
                            : null);
            insertParam.setRevokedAt(
                    AuthorizationStatusEnum.REVOKED.getCode().equals(authStatus)
                            ? LocalDateTime.now()
                            : null);
            insertParam.setRemark(normalizeText(request.remark()));
            insertParam.setCreatedBy(operatorId);
            insertParam.setUpdatedBy(operatorId);
            supplyCollaborationMapper.insertSupplierAuthorization(insertParam);
        } else {
            SupplierGoodsAuthorizationEntity updateParam = new SupplierGoodsAuthorizationEntity();
            updateParam.setAuthorizationId(existing.getId());
            updateParam.setAuthStatus(authStatus);
            updateParam.setAuthorizedPrice(request.authorizedPrice());
            updateParam.setAllocatedStockQty(
                    request.allocatedStockQty() != null ? request.allocatedStockQty() : 0);
            updateParam.setRemark(normalizeText(request.remark()));
            updateParam.setUpdatedBy(operatorId);
            if (AuthorizationStatusEnum.ACTIVE.getCode().equals(authStatus)) {
                updateParam.setAuthorizedAt(LocalDateTime.now());
                updateParam.setRevokedAt(null);
            }
            if (AuthorizationStatusEnum.REVOKED.getCode().equals(authStatus)) {
                updateParam.setRevokedAt(LocalDateTime.now());
            }
            supplyCollaborationMapper.updateSupplierAuthorization(updateParam);
        }

        if (AuthorizationStatusEnum.ACTIVE.getCode().equals(authStatus)) {
            supplyCollaborationMapper.updateImportedGoodsByAuthorization(
                    supplierId,
                    request.merchantId(),
                    request.supplierSkuId(),
                    request.authorizedPrice(),
                    request.allocatedStockQty(),
                    operatorId);
        } else {
            supplyCollaborationMapper.offSaleMerchantGoodsByAuthorization(
                    supplierId, request.merchantId(), request.supplierSkuId(), operatorId);
        }
        return OperationMessageResponse.of("supplier authorization saved");
    }

    @Override
    public PageResult<MerchantSupplyRelationRow> merchantSupplyRelations(
            String username, int page, int pageSize) {
        MerchantContextRow context = requireMerchantContext(username);
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        Long merchantId = context.getMerchantId();
        long total = supplyCollaborationMapper.countMerchantSupplyRelationRows(merchantId);
        List<MerchantSupplyRelationRow> rows =
                supplyCollaborationMapper.selectMerchantSupplyRelationRows(
                        merchantId, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public PageResult<MerchantSupplyCatalogRow> merchantSupplyCatalog(
            String username, int page, int pageSize) {
        MerchantContextRow context = requireMerchantContext(username);
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        Long merchantId = context.getMerchantId();
        long total =
                supplyCollaborationMapper.countMerchantSupplyCatalogWithRelationRows(merchantId);
        List<MerchantSupplyCatalogRow> rows =
                supplyCollaborationMapper.selectMerchantSupplyCatalogWithRelationRows(
                        merchantId, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public PageResult<PlatformSupplierRow> platformSuppliers(
            String keyword, int page, int pageSize) {
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        long total = supplyCollaborationMapper.countPlatformSuppliers(keyword);
        List<PlatformSupplierRow> rows =
                supplyCollaborationMapper.selectPlatformSuppliers(keyword, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Transactional
    @Override
    public OperationMessageResponse applyMerchantRelation(
            String username, ApplyMerchantRelationRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        Long merchantId = context.getMerchantId();
        Long operatorId = context.getUserId();

        SupplierInfoRow supplier =
                supplyCollaborationMapper.selectSupplierById(request.supplierId());
        if (supplier == null || supplier.getId() == null) {
            throw new BusinessException(404, "supplier not found");
        }

        SupplierRelationDetailRow existing =
                supplyCollaborationMapper.selectSupplierRelationByPair(
                        request.supplierId(), merchantId);
        if (existing != null && existing.getId() != null) {
            String currentStatus = existing.getStatus();
            if (CooperationStatusEnum.ACTIVE.getCode().equals(currentStatus)) {
                throw new BusinessException(400, "relation already exists and is active");
            }
            if (RegistrationStatusEnum.PENDING.getCode().equals(currentStatus)) {
                throw new BusinessException(400, "relation application is pending");
            }
            // 如果是REJECTED或ENDED，允许重新申请
        }

        if (existing == null || existing.getId() == null) {
            MerchantSupplierRelationEntity insertParam = new MerchantSupplierRelationEntity();
            insertParam.setMerchantId(merchantId);
            insertParam.setSupplierId(request.supplierId());
            insertParam.setStatus(RegistrationStatusEnum.PENDING.getCode());
            insertParam.setRemark(normalizeText(request.remark()));
            insertParam.setCreatedBy(operatorId);
            insertParam.setUpdatedBy(operatorId);
            supplyCollaborationMapper.insertSupplierRelation(insertParam);
        } else {
            MerchantSupplierRelationEntity updateParam = new MerchantSupplierRelationEntity();
            updateParam.setRelationId(existing.getId());
            updateParam.setStatus(RegistrationStatusEnum.PENDING.getCode());
            updateParam.setRemark(normalizeText(request.remark()));
            updateParam.setUpdatedBy(operatorId);
            supplyCollaborationMapper.updateSupplierRelation(updateParam);
        }

        return OperationMessageResponse.of("relation application submitted");
    }

    @Transactional
    @Override
    public OperationMessageResponse handleSupplierRelation(
            String username, Long relationId, String status, String remark) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        Long operatorId = context.getUserId();

        SupplierRelationDetailRow relation =
                supplyCollaborationMapper.selectSupplierRelationById(relationId);
        if (relation == null || relation.getId() == null) {
            throw new BusinessException(404, "relation not found");
        }
        if (!supplierId.equals(relation.getSupplierId())) {
            throw new BusinessException(403, "not authorized to handle this relation");
        }
        String currentStatus = relation.getStatus();
        if (!RegistrationStatusEnum.PENDING.getCode().equals(currentStatus)
                && !CooperationStatusEnum.ACTIVE.getCode().equals(currentStatus)) {
            throw new BusinessException(400, "relation cannot be handled");
        }
        if (RegistrationStatusEnum.PENDING.getCode().equals(currentStatus)
                && !CooperationStatusEnum.ACTIVE.getCode().equals(status)
                && !RegistrationStatusEnum.REJECTED.getCode().equals(status)) {
            throw new BusinessException(400, "pending relation can only be approved or rejected");
        }
        if (CooperationStatusEnum.ACTIVE.getCode().equals(currentStatus)
                && !CooperationStatusEnum.ENDED.getCode().equals(status)) {
            throw new BusinessException(400, "active relation can only be ended");
        }

        MerchantSupplierRelationEntity updateParam = new MerchantSupplierRelationEntity();
        updateParam.setRelationId(relationId);
        updateParam.setStatus(status);
        updateParam.setRemark(normalizeText(remark));
        updateParam.setUpdatedBy(operatorId);

        if (CooperationStatusEnum.ACTIVE.getCode().equals(status)) {
            updateParam.setCooperationStartAt(LocalDateTime.now());
            updateParam.setCooperationEndAt(null);
        } else if (RegistrationStatusEnum.REJECTED.getCode().equals(status)) {
            updateParam.setCooperationStartAt(null);
            updateParam.setCooperationEndAt(null);
        } else if (CooperationStatusEnum.ENDED.getCode().equals(status)) {
            updateParam.setCooperationEndAt(LocalDateTime.now());
        }

        supplyCollaborationMapper.updateSupplierRelation(updateParam);

        return OperationMessageResponse.of("relation " + status.toLowerCase());
    }

    @Transactional
    @Override
    public OperationMessageResponse applyMerchantAuthorization(
            String username, ApplyMerchantAuthorizationRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        Long merchantId = context.getMerchantId();
        Long operatorId = context.getUserId();

        SupplierRelationDetailRow relation =
                supplyCollaborationMapper.selectSupplierRelationByPair(
                        request.supplierId(), merchantId);
        if (relation == null
                || relation.getId() == null
                || !CooperationStatusEnum.ACTIVE.getCode().equals(relation.getStatus())) {
            throw new BusinessException(400, "合作关系不存在或未生效");
        }

        SupplierOwnedSkuRow sku =
                supplyCollaborationMapper.selectSupplierOwnedSku(
                        request.supplierId(), request.supplierSkuId());
        if (sku == null || sku.getSupplierSkuId() == null) {
            throw new BusinessException(404, "供应商商品不存在");
        }

        SupplierAuthorizationBriefRow existing =
                supplyCollaborationMapper.selectSupplierAuthorizationByPair(
                        request.supplierId(), merchantId, request.supplierSkuId());
        if (existing != null && existing.getId() != null) {
            String currentStatus = existing.getAuthStatus();
            if (AuthorizationStatusEnum.ACTIVE.getCode().equals(currentStatus)) {
                throw new BusinessException(400, "授权已存在且生效");
            }
            if (RegistrationStatusEnum.PENDING.getCode().equals(currentStatus)) {
                throw new BusinessException(400, "授权申请待审核中");
            }
        }

        if (existing == null || existing.getId() == null) {
            SupplierGoodsAuthorizationEntity insertParam = new SupplierGoodsAuthorizationEntity();
            insertParam.setSupplierId(request.supplierId());
            insertParam.setMerchantId(merchantId);
            insertParam.setSupplierSkuId(request.supplierSkuId());
            insertParam.setAuthStatus(RegistrationStatusEnum.PENDING.getCode());
            insertParam.setAuthorizedPrice(sku.getBasePrice());
            insertParam.setAllocatedStockQty(0);
            insertParam.setRemark(normalizeText(request.remark()));
            insertParam.setCreatedBy(operatorId);
            insertParam.setUpdatedBy(operatorId);
            supplyCollaborationMapper.insertSupplierAuthorization(insertParam);
        } else {
            SupplierGoodsAuthorizationEntity updateParam = new SupplierGoodsAuthorizationEntity();
            updateParam.setAuthorizationId(existing.getId());
            updateParam.setAuthStatus(RegistrationStatusEnum.PENDING.getCode());
            updateParam.setAuthorizedPrice(sku.getBasePrice());
            updateParam.setAllocatedStockQty(0);
            updateParam.setRemark(normalizeText(request.remark()));
            updateParam.setUpdatedBy(operatorId);
            supplyCollaborationMapper.updateSupplierAuthorization(updateParam);
        }

        return OperationMessageResponse.of("authorization application submitted");
    }

    @Transactional
    @Override
    public OperationMessageResponse handleSupplierAuthorization(
            String username, Long authorizationId, String status, String remark) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        Long operatorId = context.getUserId();

        SupplierAuthorizationBriefRow authorization =
                supplyCollaborationMapper.selectSupplierAuthorizationById(authorizationId);
        if (authorization == null || authorization.getId() == null) {
            throw new BusinessException(404, "authorization not found");
        }
        if (!supplierId.equals(authorization.getSupplierId())) {
            throw new BusinessException(403, "not authorized to handle this authorization");
        }
        if (!RegistrationStatusEnum.PENDING.getCode().equals(authorization.getAuthStatus())) {
            throw new BusinessException(400, "authorization is not pending");
        }

        SupplierGoodsAuthorizationEntity updateParam = new SupplierGoodsAuthorizationEntity();
        updateParam.setAuthorizationId(authorizationId);
        updateParam.setAuthStatus(status);
        updateParam.setAuthorizedPrice(authorization.getAuthorizedPrice());
        updateParam.setRemark(normalizeText(remark));
        updateParam.setUpdatedBy(operatorId);

        if (CooperationStatusEnum.ACTIVE.getCode().equals(status)) {
            updateParam.setAuthorizedAt(LocalDateTime.now());
            updateParam.setRevokedAt(null);
        } else if (AuthorizationStatusEnum.REVOKED.getCode().equals(status)) {
            updateParam.setAuthorizedAt(null);
            updateParam.setRevokedAt(LocalDateTime.now());
        }

        supplyCollaborationMapper.updateSupplierAuthorization(updateParam);

        return OperationMessageResponse.of("authorization " + status.toLowerCase());
    }

    @Transactional
    @Override
    public MerchantGoodsRow importMerchantSupply(
            String username, ImportMerchantSupplyRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        Long merchantId = context.getMerchantId();
        Long operatorId = context.getUserId();
        MerchantImportAuthorizationRow authorization =
                supplyCollaborationMapper.selectMerchantImportAuthorization(
                        merchantId, request.supplierSkuId());
        if (authorization == null || authorization.getSupplierId() == null) {
            throw new BusinessException(404, "authorized supplier sku not found");
        }
        if (!CooperationStatusEnum.ACTIVE.getCode().equals(authorization.getRelationStatus())) {
            throw new BusinessException(400, "supplier relation is not active");
        }
        if (!AuthorizationStatusEnum.ACTIVE.getCode().equals(authorization.getAuthStatus())) {
            throw new BusinessException(400, "supplier sku authorization is not active");
        }
        ImportedMerchantGoodsRow imported =
                supplyCollaborationMapper.selectImportedMerchantGoods(
                        merchantId, request.supplierSkuId());
        if (imported != null && imported.getMerchantGoodsId() != null) {
            throw new BusinessException(400, "supplier sku has already been imported");
        }

        MerchantGoodsEntity insertParam = new MerchantGoodsEntity();
        insertParam.setMerchantId(merchantId);
        insertParam.setSupplierId(authorization.getSupplierId());
        insertParam.setSupplierSkuId(request.supplierSkuId());
        insertParam.setSalePrice(request.salePrice());
        insertParam.setCurrentCostPrice(authorization.getAuthorizedPrice());
        insertParam.setStockQty(authorization.getAllocatedStockQty());
        insertParam.setRebateRate(
                request.rebateRate() == null ? BigDecimal.ZERO : request.rebateRate());
        insertParam.setSaleStatus(
                request.saleStatus() != null ? request.saleStatus() : SaleStatusEnum.OFF);
        insertParam.setCreatedBy(operatorId);
        insertParam.setUpdatedBy(operatorId);
        supplyCollaborationMapper.insertMerchantGoods(insertParam);
        Long merchantGoodsId = insertParam.getId();
        return merchantGoodsMapper.selectMerchantGoodById(merchantId, merchantGoodsId);
    }

    private SupplierContextRow requireSupplierContext(String username) {
        SupplierContextRow context =
                supplyCollaborationMapper.selectSupplierContextByUsername(username);
        if (context == null || context.getSupplierId() == null) {
            throw new BusinessException(403, "supplier account is unavailable");
        }
        return context;
    }

    private MerchantContextRow requireMerchantContext(String username) {
        MerchantContextRow context =
                supplyCollaborationMapper.selectMerchantContextByUsername(username);
        if (context == null || context.getMerchantId() == null) {
            throw new BusinessException(403, "merchant account is unavailable");
        }
        return context;
    }

    private String normalizeRelationStatus(String status) {
        if (status == null || status.isBlank()) {
            return CooperationStatusEnum.ACTIVE.getCode();
        }
        String normalized = status.trim().toUpperCase();
        if (!CooperationStatusEnum.ACTIVE.getCode().equals(normalized)
                && !CooperationStatusEnum.ENDED.getCode().equals(normalized)
                && !RegistrationStatusEnum.PENDING.getCode().equals(normalized)
                && !RegistrationStatusEnum.REJECTED.getCode().equals(normalized)) {
            throw new BusinessException(
                    400, "relation status must be ACTIVE, PENDING, REJECTED or ENDED");
        }
        return normalized;
    }

    private String normalizeAuthorizationStatus(String status) {
        if (status == null || status.isBlank()) {
            return AuthorizationStatusEnum.ACTIVE.getCode();
        }
        String normalized = status.trim().toUpperCase();
        if (!AuthorizationStatusEnum.ACTIVE.getCode().equals(normalized)
                && !AuthorizationStatusEnum.REVOKED.getCode().equals(normalized)) {
            throw new BusinessException(400, "authorization status must be ACTIVE or REVOKED");
        }
        return normalized;
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
