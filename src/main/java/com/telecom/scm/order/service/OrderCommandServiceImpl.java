package com.telecom.scm.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.enums.AftersaleStatusEnum;
import com.telecom.scm.common.enums.OrderStatusEnum;
import com.telecom.scm.common.enums.PayStatusEnum;
import com.telecom.scm.common.enums.ShipmentStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.file.service.FileStorageService;
import com.telecom.scm.member.entity.ProductStockLogEntity;
import com.telecom.scm.order.convert.OrderConvert;
import com.telecom.scm.order.dto.request.AdjustMerchantOrderItemRequest;
import com.telecom.scm.order.dto.request.AdjustMerchantOrderRequest;
import com.telecom.scm.order.dto.request.CreateOrderItemRequest;
import com.telecom.scm.order.dto.request.CreateOrderRequest;
import com.telecom.scm.order.dto.request.MerchantApproveOrderRequest;
import com.telecom.scm.order.dto.request.MerchantShipOrderRequest;
import com.telecom.scm.order.dto.request.RegisterOrderPaymentRequest;
import com.telecom.scm.order.dto.response.OrderActionResponse;
import com.telecom.scm.order.dto.response.OrderCreateResponse;
import com.telecom.scm.order.entity.OrderInfoEntity;
import com.telecom.scm.order.entity.OrderItemAdjustLogEntity;
import com.telecom.scm.order.entity.OrderItemEntity;
import com.telecom.scm.order.entity.OrderStatusLogEntity;
import com.telecom.scm.order.entity.PaymentRecordEntity;
import com.telecom.scm.order.entity.ShipmentInfoEntity;
import com.telecom.scm.order.mapper.MerchantOrderDetailRow;
import com.telecom.scm.order.mapper.OrderCreateContextRow;
import com.telecom.scm.order.mapper.OrderCreateGoodsRow;
import com.telecom.scm.order.mapper.OrderItemForAdjustRow;
import com.telecom.scm.order.mapper.OrderOperatorContextRow;
import com.telecom.scm.order.mapper.OrderProcessRow;
import com.telecom.scm.order.mapper.OrderQueryMapper;
import com.telecom.scm.order.mapper.OrderWriteMapper;
import com.telecom.scm.points.dto.PointDeductionSnapshot;
import com.telecom.scm.points.service.PointLedgerService;
import com.telecom.scm.pricing.service.CustomerLevelService;

@Service
public class OrderCommandServiceImpl implements OrderCommandService {

    private static final DateTimeFormatter ORDER_NO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicInteger ORDER_SEQUENCE = new AtomicInteger(0);

    private final OrderQueryMapper orderQueryMapper;
    private final OrderWriteMapper orderWriteMapper;
    private final PointLedgerService pointLedgerService;
    private final CustomerLevelService customerLevelService;
    private final FileStorageService fileStorageService;

    public OrderCommandServiceImpl(
            OrderQueryMapper orderQueryMapper,
            OrderWriteMapper orderWriteMapper,
            PointLedgerService pointLedgerService,
            CustomerLevelService customerLevelService,
            FileStorageService fileStorageService) {
        this.orderQueryMapper = orderQueryMapper;
        this.orderWriteMapper = orderWriteMapper;
        this.pointLedgerService = pointLedgerService;
        this.customerLevelService = customerLevelService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateResponse createCustomerOrder(
            String username, CreateOrderRequest request, String orderSource) {
        OrderCreateContextRow context = orderWriteMapper.selectCustomerContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "客户账号不可用");
        }

        List<Long> merchantGoodsIds =
                request.items().stream()
                        .map(CreateOrderItemRequest::merchantGoodsId)
                        .distinct()
                        .toList();

        List<OrderCreateGoodsRow> goodsRows =
                orderWriteMapper.selectGoodsForCreate(
                        merchantGoodsIds, context.getCustomerId(), context.getMemberLevel());
        if (goodsRows.size() != merchantGoodsIds.size()) {
            throw new BusinessException(400, "部分所选商品不可用");
        }

        Map<Long, OrderCreateGoodsRow> goodsMap =
                goodsRows.stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        OrderCreateGoodsRow::getMerchantGoodsId, item -> item));

        Long merchantId = null;
        BigDecimal goodsAmount = BigDecimal.ZERO;
        BigDecimal costAmount = BigDecimal.ZERO;
        List<OrderItemEntity> orderItems = new java.util.ArrayList<>();

        for (CreateOrderItemRequest requestItem : request.items()) {
            OrderCreateGoodsRow goods = goodsMap.get(requestItem.merchantGoodsId());
            if (goods == null || !goods.getSkuId().equals(requestItem.skuId())) {
                throw new BusinessException(400, "无效的订单商品");
            }
            if (!Boolean.TRUE.equals(goods.getAuthorized())) {
                throw new BusinessException(400, "所选商品不在商家授权范围内");
            }
            if (goods.getStockQty() == null || goods.getStockQty() < requestItem.quantity()) {
                throw new BusinessException(400, "所选商品库存不足");
            }
            if (merchantId == null) {
                merchantId = goods.getMerchantId();
            } else if (!merchantId.equals(goods.getMerchantId())) {
                throw new BusinessException(400, "仅支持单商家订单");
            }

            BigDecimal finalUnitPrice =
                    goods.getCustomerPrice() != null
                            ? goods.getCustomerPrice()
                            : (goods.getMemberPrice() == null
                                    ? goods.getSalePrice()
                                    : goods.getMemberPrice());
            BigDecimal quantity = BigDecimal.valueOf(requestItem.quantity());
            BigDecimal finalAmount = finalUnitPrice.multiply(quantity);
            BigDecimal itemCostAmount = goods.getCostPrice().multiply(quantity);
            BigDecimal profitAmount = finalAmount.subtract(itemCostAmount);
            goodsAmount = goodsAmount.add(finalAmount);
            costAmount = costAmount.add(itemCostAmount);

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setMerchantGoodsId(goods.getMerchantGoodsId());
            orderItem.setSkuId(goods.getSkuId());
            orderItem.setSpuName(goods.getSpuName());
            orderItem.setSkuName(goods.getSkuName());
            orderItem.setSpecText(goods.getSpecText());
            orderItem.setQuantity(requestItem.quantity());
            orderItem.setBasePrice(goods.getSalePrice());
            orderItem.setMemberPrice(goods.getMemberPrice());
            orderItem.setCustomerPrice(goods.getCustomerPrice());
            orderItem.setFinalUnitPrice(finalUnitPrice);
            orderItem.setFinalAmount(finalAmount);
            orderItem.setCostPrice(goods.getCostPrice());
            orderItem.setProfitAmount(profitAmount);
            orderItems.add(orderItem);
        }

        if (merchantId == null) {
            throw new BusinessException(400, "商品不能为空");
        }

        BigDecimal profitAmount = goodsAmount.subtract(costAmount);
        BigDecimal freightAmount = BigDecimal.ZERO;
        for (CreateOrderItemRequest requestItem : request.items()) {
            OrderCreateGoodsRow goods = goodsMap.get(requestItem.merchantGoodsId());
            if (goods != null) {
                freightAmount =
                        freightAmount.add(
                                goods.getFreightAmount() == null
                                        ? BigDecimal.ZERO
                                        : goods.getFreightAmount());
            }
        }
        BigDecimal orderAmount = goodsAmount.add(freightAmount);
        PointDeductionSnapshot pointDeduction =
                pointLedgerService.resolveOrderDeduction(
                        context.getCustomerId(),
                        orderAmount,
                        Boolean.TRUE.equals(request.usePoints()));
        BigDecimal payAmount = orderAmount.subtract(pointDeduction.deductionAmount());
        String orderNo = buildOrderNo();

        OrderInfoEntity orderInfo = new OrderInfoEntity();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setCustomerId(context.getCustomerId());
        orderInfo.setMerchantId(merchantId);
        orderInfo.setOrderSource(orderSource);
        orderInfo.setOrderStatus(OrderStatusEnum.WAIT_PAY);
        orderInfo.setPayStatus(PayStatusEnum.UNPAID);
        orderInfo.setShipmentStatus(ShipmentStatusEnum.WAIT_SHIP);
        orderInfo.setAftersaleStatus(AftersaleStatusEnum.NONE);
        orderInfo.setGoodsAmount(goodsAmount);
        orderInfo.setFreightAmount(freightAmount);
        orderInfo.setDiscountAmount(pointDeduction.deductionAmount());
        orderInfo.setUsedPoints((long) pointDeduction.usedPoints());
        orderInfo.setPointsDeductionAmount(pointDeduction.deductionAmount());
        orderInfo.setPayAmount(payAmount);
        orderInfo.setCostAmount(costAmount);
        orderInfo.setProfitAmount(profitAmount);
        orderInfo.setPayMethod(request.payMethod());
        orderInfo.setCustomerRemark(request.customerRemark());
        orderInfo.setReceiverName(request.receiverName());
        orderInfo.setReceiverPhone(request.receiverPhone());
        orderInfo.setReceiverProvince(request.receiverProvince());
        orderInfo.setReceiverCity(request.receiverCity());
        orderInfo.setReceiverDistrict(request.receiverDistrict());
        orderInfo.setReceiverAddress(request.receiverAddress());
        orderInfo.setCreatedBy(context.getUserId());
        orderInfo.setUpdatedBy(context.getUserId());
        orderWriteMapper.insertOrderInfo(orderInfo);
        Long orderId = orderInfo.getId();
        orderItems.forEach(item -> item.setOrderId(orderId));
        orderWriteMapper.insertOrderItems(orderItems);

        OrderStatusLogEntity statusLog = new OrderStatusLogEntity();
        statusLog.setOrderId(orderId);
        statusLog.setOldStatus(null);
        statusLog.setNewStatus(OrderStatusEnum.WAIT_PAY.getCode());
        statusLog.setOperationType("CREATE");
        statusLog.setOperatorId(context.getUserId());
        statusLog.setOperatorName(context.getCustomerName());
        statusLog.setRemark("customer submitted order");
        orderWriteMapper.insertOrderStatusLog(statusLog);

        fileStorageService.bindFileIfPresent(
                request.contractFileId(), "ORDER_CONTRACT", orderId, context.getUserId());
        pointLedgerService.consumeOrderPoints(
                orderId, context.getCustomerId(), pointDeduction.usedPoints(), orderNo);

        return OrderConvert.INSTANCE.toOrderCreateResponse(orderInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderActionResponse registerCustomerPayment(
            String username, Long orderId, RegisterOrderPaymentRequest request) {
        OrderCreateContextRow context = orderWriteMapper.selectCustomerContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "客户账号不可用");
        }
        OrderProcessRow order =
                orderWriteMapper.selectCustomerOrderForAction(orderId, context.getCustomerId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatusEnum.WAIT_PAY.getCode().equals(order.getOrderStatus())
                || !PayStatusEnum.UNPAID.getCode().equals(order.getPayStatus())) {
            throw new BusinessException(400, "当前订单不支持支付登记");
        }

        PaymentRecordEntity paymentRecord = new PaymentRecordEntity();
        paymentRecord.setOrderId(orderId);
        paymentRecord.setRecordType("PAY");
        paymentRecord.setPayMethod(request.payMethod());
        paymentRecord.setPayAmount(order.getPayAmount());
        paymentRecord.setVoucherFileId(request.voucherFileId());
        paymentRecord.setTransactionNo(request.transactionNo());
        paymentRecord.setPayStatus(PayStatusEnum.PAID_REGISTERED.getCode());
        paymentRecord.setConfirmedBy(null);
        paymentRecord.setRemark(request.remark());
        orderWriteMapper.insertPaymentRecord(paymentRecord);
        fileStorageService.bindFileIfPresent(
                request.voucherFileId(), "ORDER_PAYMENT", orderId, context.getUserId());

        int updated =
                orderWriteMapper.updateOrderState(
                        orderId,
                        OrderStatusEnum.PENDING_AUDIT.getCode(),
                        PayStatusEnum.PAID_REGISTERED.getCode(),
                        null,
                        context.getUserId());
        if (updated == 0) {
            throw new BusinessException(500, "订单状态更新失败，请稍后重试");
        }
        insertStatusLog(
                orderId,
                order.getOrderStatus(),
                OrderStatusEnum.PENDING_AUDIT.getCode(),
                "PAYMENT_REGISTER",
                context.getUserId(),
                context.getCustomerName(),
                "客户已提交支付凭证");
        return buildActionResponse(
                orderId,
                order.getOrderNo(),
                OrderStatusEnum.PENDING_AUDIT.getCode(),
                PayStatusEnum.PAID_REGISTERED.getCode(),
                order.getTrackingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderActionResponse confirmCustomerReceive(String username, Long orderId) {
        OrderCreateContextRow context = orderWriteMapper.selectCustomerContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "客户账号不可用");
        }
        OrderProcessRow order =
                orderWriteMapper.selectCustomerOrderForAction(orderId, context.getCustomerId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatusEnum.WAIT_RECEIVE.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单无法确认收货");
        }

        orderWriteMapper.signShipment(orderId, context.getUserId());
        orderWriteMapper.updateOrderState(
                orderId,
                OrderStatusEnum.FINISHED.getCode(),
                null,
                ShipmentStatusEnum.SIGNED.getCode(),
                context.getUserId());
        insertStatusLog(
                orderId,
                order.getOrderStatus(),
                OrderStatusEnum.FINISHED.getCode(),
                "CONFIRM_RECEIVE",
                context.getUserId(),
                context.getCustomerName(),
                "customer confirmed receive");
        pointLedgerService.rewardOrderCompletion(
                orderId, context.getCustomerId(), order.getPayAmount(), order.getOrderNo());
        customerLevelService.refreshCustomerLevelOnOrderFinished(
                context.getCustomerId(), context.getUserId(), username, order.getOrderNo());
        return buildActionResponse(
                orderId,
                order.getOrderNo(),
                OrderStatusEnum.FINISHED.getCode(),
                order.getPayStatus(),
                order.getTrackingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderActionResponse approveMerchantOrder(
            String username, Long orderId, MerchantApproveOrderRequest request) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        OrderProcessRow order =
                orderWriteMapper.selectMerchantOrderForAction(orderId, context.getMerchantId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatusEnum.PENDING_AUDIT.getCode().equals(order.getOrderStatus())
                || !PayStatusEnum.PAID_REGISTERED.getCode().equals(order.getPayStatus())) {
            throw new BusinessException(400, "当前订单不支持审核操作");
        }

        deductStockForApprovedOrder(orderId, context.getUserId(), context.getMerchantId());
        orderWriteMapper.updateLatestPaymentRecordForOrder(
                orderId, "CONFIRMED", context.getUserId(), request.remark());
        orderWriteMapper.updateOrderState(
                orderId,
                OrderStatusEnum.WAIT_SHIP.getCode(),
                PayStatusEnum.PAID.getCode(),
                ShipmentStatusEnum.WAIT_SHIP.getCode(),
                context.getUserId());
        insertStatusLog(
                orderId,
                order.getOrderStatus(),
                OrderStatusEnum.WAIT_SHIP.getCode(),
                "APPROVE_ORDER",
                context.getUserId(),
                context.getOperatorName(),
                request.remark());
        return buildActionResponse(
                orderId,
                order.getOrderNo(),
                OrderStatusEnum.WAIT_SHIP.getCode(),
                PayStatusEnum.PAID.getCode(),
                order.getTrackingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderActionResponse adjustMerchantOrder(
            String username, Long orderId, AdjustMerchantOrderRequest request) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        OrderProcessRow order =
                orderWriteMapper.selectMerchantOrderForAction(orderId, context.getMerchantId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatusEnum.PENDING_AUDIT.getCode().equals(order.getOrderStatus())
                || !PayStatusEnum.PAID_REGISTERED.getCode().equals(order.getPayStatus())) {
            throw new BusinessException(400, "当前订单不支持调价操作");
        }

        MerchantOrderDetailRow detail =
                orderQueryMapper.selectMerchantOrderDetail(context.getMerchantId(), orderId);
        if (detail == null) {
            throw new BusinessException(404, "订单详情不存在");
        }

        Map<Long, AdjustMerchantOrderItemRequest> requestItemMap = new HashMap<>();
        for (AdjustMerchantOrderItemRequest item : request.items()) {
            if (requestItemMap.put(item.orderItemId(), item) != null) {
                throw new BusinessException(400, "订单商品调整重复");
            }
        }

        List<OrderItemForAdjustRow> currentItems =
                orderWriteMapper.selectOrderItemsForAdjust(orderId);
        if (currentItems.isEmpty()) {
            throw new BusinessException(400, "订单商品不存在");
        }
        boolean stockDeducted = hasOrderStockDeduction(orderId);
        if (!stockDeducted) {
            validateAdjustedOrderStock(currentItems, requestItemMap, context.getMerchantId());
        }

        boolean changed = false;
        BigDecimal goodsAmount = BigDecimal.ZERO;
        BigDecimal costAmount = BigDecimal.ZERO;
        BigDecimal profitAmount = BigDecimal.ZERO;

        for (OrderItemForAdjustRow currentItem : currentItems) {
            Long orderItemId = Long.valueOf(currentItem.getOrderItemId());
            Long skuId = currentItem.getSkuId();
            int oldQuantity = currentItem.getQuantity() == null ? 0 : currentItem.getQuantity();
            BigDecimal oldFinalUnitPrice =
                    currentItem.getFinalUnitPrice() == null
                            ? BigDecimal.ZERO
                            : currentItem.getFinalUnitPrice();
            BigDecimal oldFinalAmount =
                    currentItem.getFinalAmount() == null
                            ? BigDecimal.ZERO
                            : currentItem.getFinalAmount();
            BigDecimal costPrice =
                    currentItem.getCostPrice() == null
                            ? BigDecimal.ZERO
                            : currentItem.getCostPrice();

            AdjustMerchantOrderItemRequest requestedItem = requestItemMap.remove(orderItemId);
            int newQuantity = requestedItem == null ? oldQuantity : requestedItem.quantity();
            BigDecimal newFinalUnitPrice =
                    requestedItem == null ? oldFinalUnitPrice : requestedItem.finalUnitPrice();
            BigDecimal newFinalAmount = newFinalUnitPrice.multiply(BigDecimal.valueOf(newQuantity));
            BigDecimal itemCostAmount = costPrice.multiply(BigDecimal.valueOf(newQuantity));
            BigDecimal newProfitAmount = newFinalAmount.subtract(itemCostAmount);

            if (requestedItem != null
                    && (newQuantity != oldQuantity
                            || newFinalUnitPrice.compareTo(oldFinalUnitPrice) != 0)) {
                changed = true;
                if (stockDeducted) {
                    adjustOrderItemStock(
                            orderId,
                            skuId,
                            oldQuantity,
                            newQuantity,
                            context.getUserId(),
                            context.getMerchantId());
                }
                orderWriteMapper.updateOrderItemForAdjust(
                        orderId,
                        orderItemId,
                        newQuantity,
                        newFinalUnitPrice,
                        newFinalAmount,
                        newProfitAmount);
                OrderItemAdjustLogEntity adjustLog = new OrderItemAdjustLogEntity();
                adjustLog.setOrderId(orderId);
                adjustLog.setOrderItemId(orderItemId);
                adjustLog.setOldQuantity(oldQuantity);
                adjustLog.setNewQuantity(newQuantity);
                adjustLog.setOldFinalUnitPrice(oldFinalUnitPrice);
                adjustLog.setNewFinalUnitPrice(newFinalUnitPrice);
                adjustLog.setOldFinalAmount(oldFinalAmount);
                adjustLog.setNewFinalAmount(newFinalAmount);
                adjustLog.setOperatorId(context.getUserId());
                adjustLog.setOperatorName(context.getOperatorName());
                adjustLog.setRemark(request.remark());
                orderWriteMapper.insertOrderItemAdjustLog(adjustLog);
            }
            goodsAmount = goodsAmount.add(newFinalAmount);
            costAmount = costAmount.add(itemCostAmount);
            profitAmount = profitAmount.add(newProfitAmount);
        }

        if (!requestItemMap.isEmpty()) {
            throw new BusinessException(400, "部分订单商品无效");
        }
        if (!changed) {
            throw new BusinessException(400, "未检测到订单商品变化");
        }

        BigDecimal freightAmount =
                detail.getFreightAmount() == null ? BigDecimal.ZERO : detail.getFreightAmount();
        BigDecimal discountAmount =
                detail.getDiscountAmount() == null ? BigDecimal.ZERO : detail.getDiscountAmount();
        BigDecimal payAmount = goodsAmount.add(freightAmount).subtract(discountAmount);
        orderWriteMapper.updateOrderAmounts(
                orderId, goodsAmount, costAmount, profitAmount, payAmount, context.getUserId());
        insertStatusLog(
                orderId,
                order.getOrderStatus(),
                order.getOrderStatus(),
                "ADJUST_ORDER",
                context.getUserId(),
                context.getOperatorName(),
                request.remark());
        return buildActionResponse(
                orderId,
                order.getOrderNo(),
                order.getOrderStatus(),
                order.getPayStatus(),
                order.getTrackingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderActionResponse shipMerchantOrder(
            String username, Long orderId, MerchantShipOrderRequest request) {
        OrderOperatorContextRow context = requireMerchantContext(username);
        OrderProcessRow order =
                orderWriteMapper.selectMerchantOrderForAction(orderId, context.getMerchantId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatusEnum.WAIT_SHIP.getCode().equals(order.getOrderStatus())
                || !PayStatusEnum.PAID.getCode().equals(order.getPayStatus())) {
            throw new BusinessException(400, "当前订单不支持发货操作");
        }

        ShipmentInfoEntity shipmentInfo = new ShipmentInfoEntity();
        shipmentInfo.setOrderId(orderId);
        shipmentInfo.setShipStatus(ShipmentStatusEnum.SHIPPED.getCode());
        shipmentInfo.setCarrierName(request.carrierName());
        shipmentInfo.setTrackingNo(request.trackingNo());
        shipmentInfo.setProofFileId(request.proofFileId());
        shipmentInfo.setShipTime(LocalDateTime.now());
        shipmentInfo.setLogisticsRemark(request.logisticsRemark());
        shipmentInfo.setCreatedBy(context.getUserId());
        shipmentInfo.setUpdatedBy(context.getUserId());
        orderWriteMapper.upsertShipmentInfo(shipmentInfo);
        fileStorageService.bindFileIfPresent(
                request.proofFileId(), "ORDER_SHIPMENT", orderId, context.getUserId());

        orderWriteMapper.updateOrderState(
                orderId,
                OrderStatusEnum.WAIT_RECEIVE.getCode(),
                null,
                ShipmentStatusEnum.SHIPPED.getCode(),
                context.getUserId());
        insertStatusLog(
                orderId,
                order.getOrderStatus(),
                OrderStatusEnum.WAIT_RECEIVE.getCode(),
                "SHIP_ORDER",
                context.getUserId(),
                context.getOperatorName(),
                "merchant shipped order");
        return buildActionResponse(
                orderId,
                order.getOrderNo(),
                OrderStatusEnum.WAIT_RECEIVE.getCode(),
                order.getPayStatus(),
                request.trackingNo());
    }

    // --- Private helper methods ---

    private void insertStatusLog(
            Long orderId,
            String oldStatus,
            String newStatus,
            String operationType,
            Long operatorId,
            String operatorName,
            String remark) {
        OrderStatusLogEntity statusLog = new OrderStatusLogEntity();
        statusLog.setOrderId(orderId);
        statusLog.setOldStatus(oldStatus);
        statusLog.setNewStatus(newStatus);
        statusLog.setOperationType(operationType);
        statusLog.setOperatorId(operatorId);
        statusLog.setOperatorName(operatorName);
        statusLog.setRemark(remark);
        orderWriteMapper.insertOrderStatusLog(statusLog);
    }

    private OrderActionResponse buildActionResponse(
            Long orderId,
            String orderNo,
            String orderStatus,
            String payStatus,
            String logisticsNo) {
        return new OrderActionResponse(
                String.valueOf(orderId),
                orderNo,
                orderStatus,
                payStatus,
                logisticsNo == null ? "-" : logisticsNo);
    }

    private OrderOperatorContextRow requireMerchantContext(String username) {
        OrderOperatorContextRow context =
                orderWriteMapper.selectMerchantContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "商家账号不可用");
        }
        return context;
    }

    private void deductStockForApprovedOrder(Long orderId, Long userId, Long merchantId) {
        if (hasOrderStockDeduction(orderId)) {
            return;
        }
        Map<Long, Integer> requiredQtyBySku = new HashMap<>();
        for (OrderItemForAdjustRow item : orderWriteMapper.selectOrderItemsForAdjust(orderId)) {
            Long skuId = item.getSkuId();
            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            requiredQtyBySku.merge(skuId, quantity, Integer::sum);
        }
        for (Map.Entry<Long, Integer> entry : requiredQtyBySku.entrySet()) {
            Long skuId = entry.getKey();
            int quantity = entry.getValue();
            Integer beforeQty = orderWriteMapper.selectSkuStockQty(skuId, merchantId);
            int affected = orderWriteMapper.decreaseStock(skuId, quantity, userId, merchantId);
            if (affected == 0) {
                throw new BusinessException(400, "库存已变化，无法审核订单");
            }
            Integer afterQty = orderWriteMapper.selectSkuStockQty(skuId, merchantId);
            insertStockLog(
                    skuId,
                    "OUT",
                    quantity,
                    beforeQty,
                    afterQty,
                    "ORDER",
                    orderId,
                    userId,
                    "商家审核订单并扣减库存");
        }
    }

    private boolean hasOrderStockDeduction(Long orderId) {
        return orderWriteMapper.countOrderStockDeductionLogs(orderId) > 0;
    }

    private void validateAdjustedOrderStock(
            List<OrderItemForAdjustRow> currentItems,
            Map<Long, AdjustMerchantOrderItemRequest> requestItemMap,
            Long merchantId) {
        Map<Long, Integer> requiredQtyBySku = new HashMap<>();
        for (OrderItemForAdjustRow currentItem : currentItems) {
            Long orderItemId = Long.valueOf(currentItem.getOrderItemId());
            Long skuId = currentItem.getSkuId();
            int oldQuantity = currentItem.getQuantity() == null ? 0 : currentItem.getQuantity();
            AdjustMerchantOrderItemRequest requestedItem = requestItemMap.get(orderItemId);
            int finalQuantity = requestedItem == null ? oldQuantity : requestedItem.quantity();
            requiredQtyBySku.merge(skuId, finalQuantity, Integer::sum);
        }
        for (Map.Entry<Long, Integer> entry : requiredQtyBySku.entrySet()) {
            Integer currentStock = orderWriteMapper.selectSkuStockQty(entry.getKey(), merchantId);
            if (currentStock == null || currentStock < entry.getValue()) {
                throw new BusinessException(400, "库存已变化，无法保存订单调整");
            }
        }
    }

    private void adjustOrderItemStock(
            Long orderId,
            Long skuId,
            int oldQuantity,
            int newQuantity,
            Long userId,
            Long merchantId) {
        int delta = newQuantity - oldQuantity;
        if (delta == 0) {
            return;
        }
        Integer beforeQty = orderWriteMapper.selectSkuStockQty(skuId, merchantId);
        if (delta > 0) {
            int affected = orderWriteMapper.decreaseStock(skuId, delta, userId, merchantId);
            if (affected == 0) {
                throw new BusinessException(400, "库存已变化，无法增加订单数量");
            }
            Integer afterQty = orderWriteMapper.selectSkuStockQty(skuId, merchantId);
            insertStockLog(
                    skuId,
                    "OUT",
                    delta,
                    beforeQty,
                    afterQty,
                    "ORDER_ADJUST",
                    orderId,
                    userId,
                    "商家增加订单数量");
            return;
        }
        int restoreQty = Math.abs(delta);
        orderWriteMapper.increaseStock(skuId, restoreQty, userId, merchantId);
        Integer afterQty = orderWriteMapper.selectSkuStockQty(skuId, merchantId);
        insertStockLog(
                skuId,
                "IN",
                restoreQty,
                beforeQty,
                afterQty,
                "ORDER_ADJUST",
                orderId,
                userId,
                "merchant decreased order quantity");
    }

    private void insertStockLog(
            Long skuId,
            String changeType,
            int changeQty,
            Integer beforeQty,
            Integer afterQty,
            String bizType,
            Long orderId,
            Long userId,
            String remark) {
        ProductStockLogEntity stockLog = new ProductStockLogEntity();
        stockLog.setSkuId(skuId);
        stockLog.setChangeType(changeType);
        stockLog.setChangeQty(changeQty);
        stockLog.setBeforeQty(beforeQty == null ? 0 : beforeQty);
        stockLog.setAfterQty(afterQty == null ? 0 : afterQty);
        stockLog.setBizType(bizType);
        stockLog.setBizId(orderId);
        stockLog.setRemark(remark);
        stockLog.setOperatedBy(userId);
        orderWriteMapper.insertProductStockLog(stockLog);
    }

    private String buildOrderNo() {
        int sequence = ORDER_SEQUENCE.updateAndGet(current -> (current + 1) % 1000);
        return "SCM"
                + LocalDateTime.now().format(ORDER_NO_FORMATTER)
                + String.format("%03d", sequence);
    }
}
