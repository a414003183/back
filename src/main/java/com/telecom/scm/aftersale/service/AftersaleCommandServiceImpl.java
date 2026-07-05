package com.telecom.scm.aftersale.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.aftersale.convert.AftersaleConvert;
import com.telecom.scm.aftersale.dto.request.ApproveAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.CreateAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.RefundAftersaleRequest;
import com.telecom.scm.aftersale.dto.request.RegisterReturnShipmentRequest;
import com.telecom.scm.aftersale.dto.request.RejectAftersaleRequest;
import com.telecom.scm.aftersale.dto.response.AftersaleActionResponse;
import com.telecom.scm.aftersale.entity.AftersaleAuditLogEntity;
import com.telecom.scm.aftersale.entity.AftersaleInfoEntity;
import com.telecom.scm.aftersale.entity.AftersaleItemEntity;
import com.telecom.scm.aftersale.entity.RefundRecordEntity;
import com.telecom.scm.aftersale.mapper.AftersaleItemStockRow;
import com.telecom.scm.aftersale.mapper.AftersaleMapper;
import com.telecom.scm.aftersale.mapper.AftersaleOrderItemRow;
import com.telecom.scm.aftersale.mapper.AftersaleOrderRow;
import com.telecom.scm.aftersale.mapper.AftersaleProcessRow;
import com.telecom.scm.common.enums.AftersaleStatusEnum;
import com.telecom.scm.common.enums.PayStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.file.service.FileStorageService;
import com.telecom.scm.member.entity.ProductStockLogEntity;
import com.telecom.scm.order.mapper.OrderCreateContextRow;
import com.telecom.scm.order.mapper.OrderOperatorContextRow;
import com.telecom.scm.points.service.PointLedgerService;

@Service
public class AftersaleCommandServiceImpl implements AftersaleCommandService {

    private static final DateTimeFormatter AFTERSALE_NO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicInteger AFTERSALE_SEQUENCE = new AtomicInteger(0);

    private final AftersaleMapper aftersaleMapper;
    private final PointLedgerService pointLedgerService;
    private final FileStorageService fileStorageService;

    public AftersaleCommandServiceImpl(
            AftersaleMapper aftersaleMapper,
            PointLedgerService pointLedgerService,
            FileStorageService fileStorageService) {
        this.aftersaleMapper = aftersaleMapper;
        this.pointLedgerService = pointLedgerService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AftersaleActionResponse createCustomerAftersale(
            String username, CreateAftersaleRequest request) {
        OrderCreateContextRow context = requireCustomerContext(username);
        AftersaleOrderRow order =
                aftersaleMapper.selectCustomerOrderForApply(
                        request.orderId(), context.getCustomerId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!supportsAftersale(order)) {
            throw new BusinessException(400, "当前订单不支持售后");
        }
        if (aftersaleMapper.countActiveAftersalesByOrderId(order.getId()) > 0) {
            throw new BusinessException(400, "当前订单已存在进行中的售后申请");
        }
        if (aftersaleMapper.countFinishedAftersalesByOrderId(order.getId()) > 0) {
            throw new BusinessException(400, "当前订单已完成售后，不可重复申请");
        }
        if (request.applyAmount().compareTo(order.getPayAmount()) > 0) {
            throw new BusinessException(400, "申请金额不能超过订单实付金额 " + order.getPayAmount() + " 元");
        }

        boolean needReturn = "RETURN_REFUND".equals(request.aftersaleType());
        if (!needReturn && !"REFUND_ONLY".equals(request.aftersaleType())) {
            throw new BusinessException(400, "不支持的售后类型");
        }

        String aftersaleNo = buildAftersaleNo();
        AftersaleInfoEntity aftersaleInfo = new AftersaleInfoEntity();
        aftersaleInfo.setAftersaleNo(aftersaleNo);
        aftersaleInfo.setOrderId(order.getId());
        aftersaleInfo.setCustomerId(order.getCustomerId());
        aftersaleInfo.setMerchantId(order.getMerchantId());
        aftersaleInfo.setAftersaleType(request.aftersaleType());
        aftersaleInfo.setReasonType(request.reasonType());
        aftersaleInfo.setReasonDesc(request.reasonDesc());
        aftersaleInfo.setApplyAmount(request.applyAmount());
        aftersaleInfo.setApprovedAmount(BigDecimal.ZERO);
        aftersaleInfo.setAftersaleStatus(AftersaleStatusEnum.WAIT_AUDIT);
        aftersaleInfo.setNeedReturn(needReturn);
        aftersaleInfo.setReturnTrackingNo(null);
        aftersaleInfo.setRemark(null);
        aftersaleInfo.setCreatedBy(context.getUserId());
        aftersaleInfo.setUpdatedBy(context.getUserId());
        aftersaleMapper.insertAftersaleInfo(aftersaleInfo);
        Long aftersaleId = aftersaleInfo.getId();
        fileStorageService.bindFileIfPresent(
                request.attachmentFileId(), "AFTERSALE_APPLY", aftersaleId, context.getUserId());

        List<AftersaleOrderItemRow> sourceItems =
                aftersaleMapper.selectOrderItemsForAftersale(order.getId());
        aftersaleMapper.insertAftersaleItems(
                buildAftersaleItems(aftersaleId, sourceItems, request.applyAmount()));
        aftersaleMapper.updateOrderAftersaleStatus(
                order.getId(), AftersaleStatusEnum.WAIT_AUDIT.getCode(), context.getUserId());
        insertAuditLog(
                aftersaleId,
                "CREATE",
                null,
                AftersaleStatusEnum.WAIT_AUDIT.getCode(),
                context.getUserId(),
                context.getCustomerName(),
                "customer created aftersale");
        return AftersaleConvert.INSTANCE.toAftersaleActionResponse(aftersaleInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AftersaleActionResponse registerCustomerReturnShipment(
            String username, Long aftersaleId, RegisterReturnShipmentRequest request) {
        OrderCreateContextRow context = requireCustomerContext(username);
        AftersaleProcessRow aftersale =
                aftersaleMapper.selectCustomerAftersaleForAction(
                        aftersaleId, context.getCustomerId());
        if (aftersale == null) {
            throw new BusinessException(404, "售后记录不存在");
        }
        if (!aftersale.isNeedReturn()
                || !AftersaleStatusEnum.WAIT_RETURN
                        .getCode()
                        .equals(aftersale.getAftersaleStatus())) {
            throw new BusinessException(400, "当前售后不支持登记退货物流");
        }

        aftersaleMapper.registerReturnShipment(
                aftersaleId, request.returnTrackingNo(), request.remark(), context.getUserId());
        fileStorageService.bindFileIfPresent(
                request.proofFileId(), "AFTERSALE_RETURN", aftersaleId, context.getUserId());
        aftersaleMapper.updateOrderAftersaleStatus(
                aftersale.getOrderId(),
                AftersaleStatusEnum.WAIT_RECEIVE.getCode(),
                context.getUserId());
        insertAuditLog(
                aftersaleId,
                "RETURN_SHIPMENT",
                AftersaleStatusEnum.WAIT_RETURN.getCode(),
                AftersaleStatusEnum.WAIT_RECEIVE.getCode(),
                context.getUserId(),
                context.getCustomerName(),
                request.remark());
        return new AftersaleActionResponse(
                String.valueOf(aftersaleId),
                aftersale.getAftersaleNo(),
                AftersaleStatusEnum.WAIT_RECEIVE.getCode(),
                request.returnTrackingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AftersaleActionResponse approveMerchantAftersale(
            String username, Long aftersaleId, ApproveAftersaleRequest request) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        AftersaleProcessRow aftersale =
                aftersaleMapper.selectMerchantAftersaleForAction(
                        aftersaleId, context.getMerchantId());
        if (aftersale == null) {
            throw new BusinessException(404, "售后记录不存在");
        }
        if (!AftersaleStatusEnum.WAIT_AUDIT.getCode().equals(aftersale.getAftersaleStatus())) {
            throw new BusinessException(400, "当前售后不支持审核操作");
        }

        BigDecimal approvedAmount =
                request.approvedAmount() == null
                        ? aftersale.getApplyAmount()
                        : request.approvedAmount();
        if (approvedAmount.compareTo(aftersale.getOrderPayAmount()) > 0) {
            throw new BusinessException(
                    400, "审核金额不能超过订单实付金额 " + aftersale.getOrderPayAmount() + " 元");
        }
        String nextStatus =
                aftersale.isNeedReturn()
                        ? AftersaleStatusEnum.WAIT_RETURN.getCode()
                        : AftersaleStatusEnum.WAIT_REFUND.getCode();
        aftersaleMapper.approveAftersale(
                aftersaleId, approvedAmount, nextStatus, request.remark(), context.getUserId());
        aftersaleMapper.updateOrderAftersaleStatus(
                aftersale.getOrderId(), nextStatus, context.getUserId());
        insertAuditLog(
                aftersaleId,
                "APPROVE",
                AftersaleStatusEnum.WAIT_AUDIT.getCode(),
                nextStatus,
                context.getUserId(),
                context.getOperatorName(),
                request.remark());
        return AftersaleConvert.INSTANCE.toAftersaleActionResponse(aftersale, nextStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AftersaleActionResponse rejectMerchantAftersale(
            String username, Long aftersaleId, RejectAftersaleRequest request) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        AftersaleProcessRow aftersale =
                aftersaleMapper.selectMerchantAftersaleForAction(
                        aftersaleId, context.getMerchantId());
        if (aftersale == null) {
            throw new BusinessException(404, "售后记录不存在");
        }
        if (!AftersaleStatusEnum.WAIT_AUDIT.getCode().equals(aftersale.getAftersaleStatus())) {
            throw new BusinessException(400, "当前售后不支持驳回操作");
        }

        aftersaleMapper.rejectAftersale(aftersaleId, request.remark(), context.getUserId());
        aftersaleMapper.updateOrderAftersaleStatus(
                aftersale.getOrderId(),
                AftersaleStatusEnum.REJECTED.getCode(),
                context.getUserId());
        insertAuditLog(
                aftersaleId,
                "REJECT",
                AftersaleStatusEnum.WAIT_AUDIT.getCode(),
                AftersaleStatusEnum.REJECTED.getCode(),
                context.getUserId(),
                context.getOperatorName(),
                request.remark());
        return AftersaleConvert.INSTANCE.toAftersaleActionResponse(
                aftersale, AftersaleStatusEnum.REJECTED.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AftersaleActionResponse receiveMerchantReturn(String username, Long aftersaleId) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        AftersaleProcessRow aftersale =
                aftersaleMapper.selectMerchantAftersaleForAction(
                        aftersaleId, context.getMerchantId());
        if (aftersale == null) {
            throw new BusinessException(404, "售后记录不存在");
        }
        if (!AftersaleStatusEnum.WAIT_RECEIVE.getCode().equals(aftersale.getAftersaleStatus())) {
            throw new BusinessException(400, "当前售后不支持确认收货");
        }

        aftersaleMapper.receiveReturn(aftersaleId, context.getUserId());
        aftersaleMapper.updateOrderAftersaleStatus(
                aftersale.getOrderId(),
                AftersaleStatusEnum.WAIT_REFUND.getCode(),
                context.getUserId());
        insertAuditLog(
                aftersaleId,
                "RECEIVE_RETURN",
                AftersaleStatusEnum.WAIT_RECEIVE.getCode(),
                AftersaleStatusEnum.WAIT_REFUND.getCode(),
                context.getUserId(),
                context.getOperatorName(),
                "商家已确认收货");
        return AftersaleConvert.INSTANCE.toAftersaleActionResponse(
                aftersale, AftersaleStatusEnum.WAIT_REFUND.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AftersaleActionResponse refundMerchantAftersale(
            String username, Long aftersaleId, RefundAftersaleRequest request) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        AftersaleProcessRow aftersale =
                aftersaleMapper.selectMerchantAftersaleForAction(
                        aftersaleId, context.getMerchantId());
        if (aftersale == null) {
            throw new BusinessException(404, "售后记录不存在");
        }
        if (aftersaleMapper.countRefundRecordsByAftersaleId(aftersaleId) > 0) {
            throw new BusinessException(400, "当前售后已完成退款");
        }
        if (!AftersaleStatusEnum.WAIT_REFUND.getCode().equals(aftersale.getAftersaleStatus())) {
            throw new BusinessException(400, "当前售后不支持退款操作");
        }

        RefundRecordEntity refundRecord = new RefundRecordEntity();
        refundRecord.setOrderId(aftersale.getOrderId());
        refundRecord.setAftersaleId(aftersaleId);
        refundRecord.setPayMethod(request.payMethod());
        refundRecord.setPayAmount(
                aftersale.getApprovedAmount() == null
                        ? aftersale.getApplyAmount()
                        : aftersale.getApprovedAmount());
        refundRecord.setVoucherFileId(request.voucherFileId());
        refundRecord.setTransactionNo(request.transactionNo());
        refundRecord.setConfirmedBy(context.getUserId());
        refundRecord.setRemark(request.remark());
        aftersaleMapper.insertRefundRecord(refundRecord);
        fileStorageService.bindFileIfPresent(
                request.voucherFileId(), "AFTERSALE_REFUND", aftersaleId, context.getUserId());

        if (aftersale.isNeedReturn()) {
            restoreStockFromAftersale(
                    aftersaleId,
                    aftersale.getOrderId(),
                    aftersale.getMerchantId(),
                    context.getUserId());
        }

        aftersaleMapper.finishAftersale(aftersaleId, request.remark(), context.getUserId());
        aftersaleMapper.updateOrderAftersaleStatus(
                aftersale.getOrderId(),
                AftersaleStatusEnum.FINISHED.getCode(),
                context.getUserId());
        pointLedgerService.applyRefundPointChanges(
                aftersaleId,
                aftersale.getOrderId(),
                aftersale.getCustomerId(),
                aftersale.getApprovedAmount() == null
                        ? aftersale.getApplyAmount()
                        : aftersale.getApprovedAmount(),
                aftersale.getOrderPayAmount(),
                aftersale.getOrderUsedPoints(),
                aftersale.getOrderNo());
        insertAuditLog(
                aftersaleId,
                "REFUND",
                AftersaleStatusEnum.WAIT_REFUND.getCode(),
                AftersaleStatusEnum.FINISHED.getCode(),
                context.getUserId(),
                context.getOperatorName(),
                request.remark());
        return AftersaleConvert.INSTANCE.toAftersaleActionResponse(
                aftersale, AftersaleStatusEnum.FINISHED.getCode());
    }

    private void restoreStockFromAftersale(
            Long aftersaleId, Long orderId, Long merchantId, Long updatedBy) {
        if (!hasOrderStockDeduction(orderId)) {
            return;
        }
        if (aftersaleMapper.countStockRestoreLogsByAftersaleId(aftersaleId) > 0) {
            throw new BusinessException(400, "当前售后已退回库存，不可重复操作");
        }
        List<AftersaleItemStockRow> items = aftersaleMapper.selectAftersaleItemStocks(aftersaleId);
        for (AftersaleItemStockRow item : items) {
            Long skuId = item.getSkuId();
            int quantity = item.getQuantity();
            Integer beforeQty = aftersaleMapper.selectSkuStockQty(skuId, merchantId);
            aftersaleMapper.restoreStock(skuId, merchantId, quantity, updatedBy);
            Integer afterQty = aftersaleMapper.selectSkuStockQty(skuId, merchantId);

            ProductStockLogEntity stockLog = new ProductStockLogEntity();
            stockLog.setSkuId(skuId);
            stockLog.setChangeType("RETURN");
            stockLog.setChangeQty(quantity);
            stockLog.setBeforeQty(beforeQty == null ? 0 : beforeQty);
            stockLog.setAfterQty(afterQty == null ? 0 : afterQty);
            stockLog.setBizType("AFTERSALE");
            stockLog.setBizId(aftersaleId);
            stockLog.setRemark("aftersale stock rollback");
            stockLog.setOperatedBy(updatedBy);
            aftersaleMapper.insertProductStockLog(stockLog);
        }
    }

    private boolean supportsAftersale(AftersaleOrderRow order) {
        if (PayStatusEnum.PAID.getCode().equals(order.getPayStatus())) {
            return true;
        }
        return PayStatusEnum.PAID_REGISTERED.getCode().equals(order.getPayStatus())
                && hasOrderStockDeduction(order.getId());
    }

    private boolean hasOrderStockDeduction(Long orderId) {
        return aftersaleMapper.countOrderStockDeductionLogs(orderId) > 0;
    }

    private List<AftersaleItemEntity> buildAftersaleItems(
            Long aftersaleId, List<AftersaleOrderItemRow> sourceItems, BigDecimal applyAmount) {
        if (sourceItems.isEmpty()) {
            throw new BusinessException(400, "订单商品不存在");
        }
        BigDecimal totalAmount =
                sourceItems.stream()
                        .map(AftersaleOrderItemRow::getFinalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal allocated = BigDecimal.ZERO;
        List<AftersaleItemEntity> items = new ArrayList<>();
        for (int index = 0; index < sourceItems.size(); index++) {
            AftersaleOrderItemRow sourceItem = sourceItems.get(index);
            BigDecimal itemApplyAmount;
            if (index == sourceItems.size() - 1) {
                itemApplyAmount = applyAmount.subtract(allocated);
            } else {
                itemApplyAmount =
                        applyAmount
                                .multiply(sourceItem.getFinalAmount())
                                .divide(totalAmount, 2, RoundingMode.DOWN);
                allocated = allocated.add(itemApplyAmount);
            }

            AftersaleItemEntity item = new AftersaleItemEntity();
            item.setAftersaleId(aftersaleId);
            item.setOrderItemId(sourceItem.getOrderItemId());
            item.setSkuId(sourceItem.getSkuId());
            item.setSpuName(sourceItem.getSpuName());
            item.setSkuName(sourceItem.getSkuName());
            item.setSpecText(sourceItem.getSpecText());
            item.setQuantity(sourceItem.getQuantity());
            item.setApplyAmount(itemApplyAmount);
            items.add(item);
        }
        return items;
    }

    private OrderCreateContextRow requireCustomerContext(String username) {
        OrderCreateContextRow context = aftersaleMapper.selectCustomerContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "客户账号不可用");
        }
        return context;
    }

    private OrderOperatorContextRow requireMerchantContext(String username) {
        OrderOperatorContextRow context = aftersaleMapper.selectMerchantContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "商家账号不可用");
        }
        return context;
    }

    private void insertAuditLog(
            Long aftersaleId,
            String actionType,
            String oldStatus,
            String newStatus,
            Long operatorId,
            String operatorName,
            String remark) {
        AftersaleAuditLogEntity auditLog = new AftersaleAuditLogEntity();
        auditLog.setAftersaleId(aftersaleId);
        auditLog.setActionType(actionType);
        auditLog.setOldStatus(oldStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setOperatorId(operatorId);
        auditLog.setOperatorName(operatorName);
        auditLog.setRemark(remark);
        aftersaleMapper.insertAftersaleAuditLog(auditLog);
    }

    private String buildAftersaleNo() {
        int sequence = AFTERSALE_SEQUENCE.updateAndGet(current -> (current + 1) % 1000);
        return "AS"
                + LocalDateTime.now().format(AFTERSALE_NO_FORMATTER)
                + String.format("%03d", sequence);
    }
}
