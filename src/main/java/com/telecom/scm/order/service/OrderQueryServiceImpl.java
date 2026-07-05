package com.telecom.scm.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.order.convert.OrderConvert;
import com.telecom.scm.order.dto.response.MerchantOrderDetailResponse;
import com.telecom.scm.order.dto.response.OrderSummaryResponse;
import com.telecom.scm.order.dto.response.OrderTimelineEventResponse;
import com.telecom.scm.order.mapper.MerchantOrderDetailRow;
import com.telecom.scm.order.mapper.OrderAftersaleAuditTimelineRow;
import com.telecom.scm.order.mapper.OrderAftersaleTimelineRow;
import com.telecom.scm.order.mapper.OrderBaseInfoRow;
import com.telecom.scm.order.mapper.OrderFileTimelineRow;
import com.telecom.scm.order.mapper.OrderItemAdjustLogRow;
import com.telecom.scm.order.mapper.OrderOperatorContextRow;
import com.telecom.scm.order.mapper.OrderPaymentTimelineRow;
import com.telecom.scm.order.mapper.OrderQueryMapper;
import com.telecom.scm.order.mapper.OrderShipmentTimelineRow;
import com.telecom.scm.order.mapper.OrderStatusTimelineRow;
import com.telecom.scm.order.mapper.OrderWriteMapper;

@Service
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderQueryMapper orderQueryMapper;
    private final OrderWriteMapper orderWriteMapper;

    public OrderQueryServiceImpl(
            OrderQueryMapper orderQueryMapper, OrderWriteMapper orderWriteMapper) {
        this.orderQueryMapper = orderQueryMapper;
        this.orderWriteMapper = orderWriteMapper;
    }

    @Override
    public PageResult<OrderSummaryResponse> currentCustomerOrders(
            String username, int page, int pageSize) {
        int validatedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int validatedPage = Math.max(page, 1);
        int offset = (validatedPage - 1) * validatedPageSize;
        long total = orderQueryMapper.countCustomerOrders(username);
        List<OrderSummaryResponse> list =
                OrderConvert.INSTANCE.toOrderSummaryResponseList(
                        orderQueryMapper.selectCustomerOrderRows(
                                username, offset, validatedPageSize));
        return PageResult.of(list, total, validatedPage, validatedPageSize);
    }

    @Override
    public PageResult<OrderSummaryResponse> currentMerchantOrders(
            String username, int page, int pageSize) {
        int validatedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int validatedPage = Math.max(page, 1);
        int offset = (validatedPage - 1) * validatedPageSize;
        long total = orderQueryMapper.countMerchantOrders(username);
        List<OrderSummaryResponse> list =
                OrderConvert.INSTANCE.toOrderSummaryResponseList(
                        orderQueryMapper.selectMerchantOrderRows(
                                username, offset, validatedPageSize));
        return PageResult.of(list, total, validatedPage, validatedPageSize);
    }

    @Override
    public MerchantOrderDetailResponse currentMerchantOrderDetail(String username, Long orderId) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        MerchantOrderDetailRow detail =
                orderQueryMapper.selectMerchantOrderDetail(context.getMerchantId(), orderId);
        if (detail == null) {
            throw new BusinessException(404, "订单不存在");
        }

        MerchantOrderDetailResponse response =
                OrderConvert.INSTANCE.toMerchantOrderDetailResponse(detail);
        response.setItems(orderQueryMapper.selectMerchantOrderDetailItems(orderId));
        response.setAdjustLogs(orderQueryMapper.selectOrderItemAdjustLogs(orderId));
        return response;
    }

    @Override
    public List<OrderTimelineEventResponse> currentCustomerOrderTimeline(
            String username, Long orderId) {
        if (orderQueryMapper.selectCustomerOrderAccess(username, orderId) == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return buildOrderTimeline(orderId);
    }

    @Override
    public List<OrderTimelineEventResponse> currentMerchantOrderTimeline(
            String username, Long orderId) {
        if (orderQueryMapper.selectMerchantOrderAccess(username, orderId) == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return buildOrderTimeline(orderId);
    }

    @Override
    public List<OrderTimelineEventResponse> adminOrderTimeline(Long orderId) {
        OrderBaseInfoRow baseInfo = orderQueryMapper.selectOrderBaseInfo(orderId);
        if (baseInfo == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return buildOrderTimeline(orderId);
    }

    @Override
    public Long resolveOrderId(String orderIdOrNo) {
        try {
            return Long.valueOf(orderIdOrNo);
        } catch (NumberFormatException e) {
            Long orderId = orderQueryMapper.selectOrderIdByOrderNo(orderIdOrNo);
            if (orderId == null) {
                throw new BusinessException(404, "订单不存在");
            }
            return orderId;
        }
    }

    private OrderOperatorContextRow requireMerchantContext(String username) {
        OrderOperatorContextRow context =
                orderWriteMapper.selectMerchantContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "商家账号不可用");
        }
        return context;
    }

    private List<OrderTimelineEventResponse> buildOrderTimeline(Long orderId) {
        OrderBaseInfoRow baseInfo = orderQueryMapper.selectOrderBaseInfo(orderId);
        if (baseInfo == null) {
            throw new BusinessException(404, "订单不存在");
        }

        String customerName = baseInfo.getCustomerName();
        String merchantName = baseInfo.getMerchantName();
        List<TimelineDraft> drafts = new ArrayList<>();
        AtomicInteger sequence = new AtomicInteger(0);

        addTimelineEvent(
                drafts,
                sequence,
                "order-created-" + orderId,
                "ORDER_CREATED",
                "订单创建",
                joinDetails("订单号：" + baseInfo.getOrderNo()),
                customerName,
                baseInfo.getCreatedAt());

        List<OrderAftersaleTimelineRow> aftersaleRows =
                orderQueryMapper.selectOrderAftersaleTimelineRows(orderId);
        Map<String, OrderAftersaleTimelineRow> aftersaleMap = new LinkedHashMap<>();
        for (OrderAftersaleTimelineRow aftersaleRow : aftersaleRows) {
            aftersaleMap.put(aftersaleRow.getId(), aftersaleRow);
        }

        for (OrderStatusTimelineRow statusLog :
                orderQueryMapper.selectOrderStatusTimelineRows(orderId)) {
            String operationType = statusLog.getOperationType();
            String eventTime = statusLog.getCreatedAt();
            String operatorName = statusLog.getOperatorName();
            String remark = statusLog.getRemark();
            String statusLogId = statusLog.getId();

            switch (operationType) {
                case "CREATE" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "order-submitted-" + statusLogId,
                                "ORDER_SUBMITTED",
                                "订单提交",
                                defaultText(remark, "客户已提交订单"),
                                operatorName,
                                eventTime);
                case "APPROVE_ORDER" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "order-approved-" + statusLogId,
                                "ORDER_APPROVED",
                                "订单审核通过",
                                defaultText(remark, "商家已确认订单并进入待发货"),
                                operatorName,
                                eventTime);
                case "REJECT_ORDER" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "order-rejected-" + statusLogId,
                                "ORDER_REJECTED",
                                "订单已驳回",
                                defaultText(remark, "订单审核未通过"),
                                operatorName,
                                eventTime);
                case "CONFIRM_RECEIVE" -> {
                    addTimelineEvent(
                            drafts,
                            sequence,
                            "order-received-confirmed-" + statusLogId,
                            "RECEIVED_CONFIRMED",
                            "客户确认收货",
                            defaultText(remark, "客户已确认收货"),
                            operatorName,
                            eventTime);
                    if ("FINISHED".equals(statusLog.getNewStatus())) {
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "order-finished-" + statusLogId,
                                "ORDER_FINISHED",
                                "订单完成",
                                "订单主流程已完成",
                                operatorName,
                                eventTime);
                    }
                }
                default -> {}
            }
        }

        for (OrderPaymentTimelineRow paymentRow :
                orderQueryMapper.selectOrderPaymentTimelineRows(orderId)) {
            String recordType = paymentRow.getRecordType();
            String payMethod = translatePayMethod(paymentRow.getPayMethod());
            String payAmount = formatMoney(paymentRow.getPayAmount());
            String transactionNo = paymentRow.getTransactionNo();
            String payRemark = paymentRow.getRemark();
            String paymentId = paymentRow.getId();

            if ("PAY".equals(recordType)) {
                addTimelineEvent(
                        drafts,
                        sequence,
                        "payment-recorded-" + paymentId,
                        "PAYMENT_RECORDED",
                        "支付登记",
                        joinDetails(
                                "支付方式：" + payMethod,
                                "支付金额：" + payAmount,
                                hasText(transactionNo) ? "交易流水号：" + transactionNo : null,
                                hasText(payRemark) ? "备注：" + payRemark : null),
                        customerName,
                        paymentRow.getCreatedAt());
                continue;
            }
            if ("REFUND".equals(recordType)) {
                OrderAftersaleTimelineRow aftersaleRow =
                        aftersaleMap.get(paymentRow.getAftersaleId());
                addTimelineEvent(
                        drafts,
                        sequence,
                        "refund-completed-" + paymentId,
                        "REFUND_COMPLETED",
                        "退款完成",
                        joinDetails(
                                hasText(aftersaleRow == null ? null : aftersaleRow.getAftersaleNo())
                                        ? "售后单：" + aftersaleRow.getAftersaleNo()
                                        : null,
                                "退款方式：" + payMethod,
                                "退款金额：" + payAmount,
                                hasText(transactionNo) ? "退款流水号：" + transactionNo : null,
                                hasText(payRemark) ? "备注：" + payRemark : null),
                        merchantName,
                        chooseTime(paymentRow.getConfirmedAt(), paymentRow.getCreatedAt()));
            }
        }

        for (OrderFileTimelineRow fileRow : orderQueryMapper.selectOrderFileTimelineRows(orderId)) {
            String bizType = fileRow.getBizType();
            String fileId = fileRow.getId();
            if ("ORDER_CONTRACT".equals(bizType)) {
                addTimelineEvent(
                        drafts,
                        sequence,
                        "contract-uploaded-" + fileId,
                        "CONTRACT_UPLOADED",
                        "合同附件上传",
                        "附件：" + fileRow.getOriginalName(),
                        fileRow.getOperatorName(),
                        fileRow.getUploadTime());
            } else if ("ORDER_PAYMENT".equals(bizType)) {
                addTimelineEvent(
                        drafts,
                        sequence,
                        "payment-voucher-" + fileId,
                        "PAYMENT_VOUCHER_UPLOADED",
                        "支付凭证上传",
                        "附件：" + fileRow.getOriginalName(),
                        fileRow.getOperatorName(),
                        fileRow.getUploadTime());
            }
        }

        List<OrderItemAdjustLogRow> adjustLogs =
                new ArrayList<>(orderQueryMapper.selectOrderItemAdjustLogs(orderId));
        adjustLogs.sort(Comparator.comparing(OrderItemAdjustLogRow::getCreatedAt));
        for (OrderItemAdjustLogRow adjustLog : adjustLogs) {
            addTimelineEvent(
                    drafts,
                    sequence,
                    "order-adjusted-" + adjustLog.getId(),
                    "ORDER_ITEM_ADJUSTED",
                    "订单改单",
                    joinDetails(
                            hasText(adjustLog.getItemName())
                                    ? "商品：" + adjustLog.getItemName()
                                    : null,
                            "数量："
                                    + adjustLog.getOldQuantity()
                                    + " -> "
                                    + adjustLog.getNewQuantity(),
                            "成交单价："
                                    + formatMoney(adjustLog.getOldFinalUnitPrice())
                                    + " -> "
                                    + formatMoney(adjustLog.getNewFinalUnitPrice()),
                            hasText(adjustLog.getRemark()) ? "备注：" + adjustLog.getRemark() : null),
                    adjustLog.getOperatorName(),
                    adjustLog.getCreatedAt());
        }

        OrderShipmentTimelineRow shipmentRow =
                orderQueryMapper.selectOrderShipmentTimelineRow(orderId);
        if (shipmentRow != null) {
            addTimelineEvent(
                    drafts,
                    sequence,
                    "order-shipped-" + shipmentRow.getId(),
                    com.telecom.scm.common.enums.ShipmentStatusEnum.SHIPPED.getCode(),
                    "商家发货",
                    joinDetails(
                            hasText(shipmentRow.getCarrierName())
                                    ? "承运公司：" + shipmentRow.getCarrierName()
                                    : null,
                            hasText(shipmentRow.getTrackingNo())
                                    ? "物流单号：" + shipmentRow.getTrackingNo()
                                    : null,
                            hasText(shipmentRow.getLogisticsRemark())
                                    ? "物流备注：" + shipmentRow.getLogisticsRemark()
                                    : null),
                    defaultText(shipmentRow.getShipOperatorName(), merchantName),
                    shipmentRow.getShipTime());
            addTimelineEvent(
                    drafts,
                    sequence,
                    "order-signed-" + shipmentRow.getId(),
                    com.telecom.scm.common.enums.ShipmentStatusEnum.SIGNED.getCode(),
                    "物流签收",
                    joinDetails(
                            hasText(shipmentRow.getCarrierName())
                                    ? "承运公司：" + shipmentRow.getCarrierName()
                                    : null,
                            hasText(shipmentRow.getTrackingNo())
                                    ? "物流单号：" + shipmentRow.getTrackingNo()
                                    : null),
                    defaultText(shipmentRow.getSignOperatorName(), customerName),
                    shipmentRow.getSignTime());
        }

        for (OrderAftersaleAuditTimelineRow auditLog :
                orderQueryMapper.selectOrderAftersaleAuditTimelineRows(orderId)) {
            OrderAftersaleTimelineRow aftersaleRow = aftersaleMap.get(auditLog.getAftersaleId());
            String actionType = auditLog.getActionType();
            String aftersaleNo = aftersaleRow == null ? null : aftersaleRow.getAftersaleNo();
            String auditId = auditLog.getId();
            String descriptionPrefix = hasText(aftersaleNo) ? "售后单：" + aftersaleNo : null;
            switch (actionType) {
                case "CREATE" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "aftersale-created-" + auditId,
                                "AFTERSALE_CREATED",
                                "售后发起",
                                joinDetails(
                                        descriptionPrefix,
                                        hasText(
                                                        aftersaleRow == null
                                                                ? null
                                                                : aftersaleRow.getAftersaleType())
                                                ? "售后类型："
                                                        + translateAftersaleType(
                                                                aftersaleRow.getAftersaleType())
                                                : null,
                                        "申请金额："
                                                + formatMoney(
                                                        aftersaleRow == null
                                                                ? null
                                                                : aftersaleRow.getApplyAmount()),
                                        hasText(auditLog.getRemark())
                                                ? "备注：" + auditLog.getRemark()
                                                : null),
                                auditLog.getOperatorName(),
                                auditLog.getCreatedAt());
                case "APPROVE" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "aftersale-approved-" + auditId,
                                "AFTERSALE_APPROVED",
                                "售后审核通过",
                                joinDetails(
                                        descriptionPrefix,
                                        "审核金额："
                                                + formatMoney(
                                                        aftersaleRow == null
                                                                ? null
                                                                : aftersaleRow.getApprovedAmount()),
                                        hasText(auditLog.getRemark())
                                                ? "备注：" + auditLog.getRemark()
                                                : null),
                                auditLog.getOperatorName(),
                                auditLog.getCreatedAt());
                case "REJECT" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "aftersale-rejected-" + auditId,
                                "AFTERSALE_REJECTED",
                                "售后已驳回",
                                joinDetails(
                                        descriptionPrefix,
                                        hasText(auditLog.getRemark())
                                                ? "备注：" + auditLog.getRemark()
                                                : null),
                                auditLog.getOperatorName(),
                                auditLog.getCreatedAt());
                case "RETURN_SHIPMENT" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "return-logistics-" + auditId,
                                "RETURN_LOGISTICS_SUBMITTED",
                                "退货物流提交",
                                joinDetails(
                                        descriptionPrefix,
                                        hasText(
                                                        aftersaleRow == null
                                                                ? null
                                                                : aftersaleRow
                                                                        .getReturnTrackingNo())
                                                ? "退货单号：" + aftersaleRow.getReturnTrackingNo()
                                                : null,
                                        hasText(auditLog.getRemark())
                                                ? "备注：" + auditLog.getRemark()
                                                : null),
                                auditLog.getOperatorName(),
                                auditLog.getCreatedAt());
                case "RECEIVE_RETURN" ->
                        addTimelineEvent(
                                drafts,
                                sequence,
                                "return-received-" + auditId,
                                "RETURN_RECEIVED",
                                "商家收到退货",
                                joinDetails(
                                        descriptionPrefix,
                                        hasText(auditLog.getRemark())
                                                ? "备注：" + auditLog.getRemark()
                                                : null),
                                auditLog.getOperatorName(),
                                auditLog.getCreatedAt());
                default -> {}
            }
        }

        drafts.sort(
                Comparator.comparing(
                                TimelineDraft::eventTime, Comparator.nullsLast(String::compareTo))
                        .thenComparingInt(TimelineDraft::sequence));
        return drafts.stream().map(TimelineDraft::response).toList();
    }

    private void addTimelineEvent(
            List<TimelineDraft> drafts,
            AtomicInteger sequence,
            String id,
            String eventType,
            String title,
            String description,
            String operatorName,
            String eventTime) {
        if (!hasText(eventTime)) {
            return;
        }
        drafts.add(
                new TimelineDraft(
                        sequence.getAndIncrement(),
                        eventTime,
                        new OrderTimelineEventResponse(
                                id,
                                eventType,
                                title,
                                description,
                                defaultText(operatorName, "-"),
                                eventTime)));
    }

    private String joinDetails(String... parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (hasText(part)) {
                if (builder.length() > 0) {
                    builder.append("；");
                }
                builder.append(part);
            }
        }
        return builder.toString();
    }

    private String translatePayMethod(String payMethod) {
        return switch (payMethod) {
            case "BANK_TRANSFER" -> "银行转账";
            case "PUBLIC_ACCOUNT" -> "对公打款";
            case "OFFLINE_VOUCHER" -> "线下凭证";
            default -> defaultText(payMethod, "-");
        };
    }

    private String translateAftersaleType(String aftersaleType) {
        return switch (aftersaleType) {
            case "REFUND_ONLY" -> "仅退款";
            case "RETURN_REFUND" -> "退货退款";
            default -> defaultText(aftersaleType, "-");
        };
    }

    private String formatMoney(Object value) {
        if (value == null) {
            return "¥0.00";
        }
        return "¥" + new BigDecimal(String.valueOf(value)).toPlainString();
    }

    private String chooseTime(String preferred, String fallback) {
        return hasText(preferred) ? preferred : fallback;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultText(String value, String fallback) {
        return hasText(value) ? value : fallback;
    }

    private record TimelineDraft(
            int sequence, String eventTime, OrderTimelineEventResponse response) {}
}
