package com.telecom.scm.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.telecom.scm.common.enums.OrderStatusEnum;
import com.telecom.scm.common.enums.PayStatusEnum;
import com.telecom.scm.common.enums.ShipmentStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.file.service.FileStorageService;
import com.telecom.scm.order.dto.request.CreateOrderItemRequest;
import com.telecom.scm.order.dto.request.CreateOrderRequest;
import com.telecom.scm.order.dto.response.OrderCreateResponse;
import com.telecom.scm.order.entity.OrderInfoEntity;
import com.telecom.scm.order.mapper.OrderCreateContextRow;
import com.telecom.scm.order.mapper.OrderCreateGoodsRow;
import com.telecom.scm.order.mapper.OrderQueryMapper;
import com.telecom.scm.order.mapper.OrderWriteMapper;
import com.telecom.scm.points.dto.PointDeductionSnapshot;
import com.telecom.scm.points.service.PointLedgerService;
import com.telecom.scm.pricing.service.CustomerLevelService;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceImplTest {

    @Mock private OrderQueryMapper orderQueryMapper;

    @Mock private OrderWriteMapper orderWriteMapper;

    @Mock private PointLedgerService pointLedgerService;

    @Mock private CustomerLevelService customerLevelService;

    @Mock private FileStorageService fileStorageService;

    @InjectMocks private OrderCommandServiceImpl orderCommandService;

    @Test
    @DisplayName("正常下单流程应创建订单并返回订单信息")
    void shouldCreateCustomerOrderSuccessfully() {
        // given
        String username = "customer";
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(1L, 10L, 2);
        CreateOrderRequest request =
                new CreateOrderRequest(
                        "张三",
                        "13800000000",
                        "广东省",
                        "深圳市",
                        "南山区",
                        "科技园",
                        "BANK_TRANSFER",
                        "请尽快发货",
                        null,
                        false,
                        List.of(itemRequest));

        OrderCreateContextRow context = new OrderCreateContextRow();
        context.setUserId(100L);
        context.setCustomerId(1000L);
        context.setCustomerName("Test Customer");
        context.setMemberLevel("GOLD");

        OrderCreateGoodsRow goods = new OrderCreateGoodsRow();
        goods.setMerchantGoodsId(1L);
        goods.setMerchantId(200L);
        goods.setSkuId(10L);
        goods.setSpuName("手机");
        goods.setSkuName("黑色 128G");
        goods.setSpecText("黑色/128G");
        goods.setSalePrice(new BigDecimal("150.00"));
        goods.setMemberPrice(new BigDecimal("120.00"));
        goods.setCustomerPrice(new BigDecimal("100.00"));
        goods.setCostPrice(new BigDecimal("80.00"));
        goods.setStockQty(10);
        goods.setAuthorized(true);
        goods.setFreightAmount(new BigDecimal("10.00"));

        when(orderWriteMapper.selectCustomerContextByUsername(username)).thenReturn(context);
        when(orderWriteMapper.selectGoodsForCreate(List.of(1L), 1000L, "GOLD"))
                .thenReturn(List.of(goods));
        when(pointLedgerService.resolveOrderDeduction(1000L, new BigDecimal("210.00"), false))
                .thenReturn(new PointDeductionSnapshot(0, BigDecimal.ZERO));

        doAnswer(
                        invocation -> {
                            OrderInfoEntity entity = invocation.getArgument(0);
                            entity.setId(12345L);
                            return 1;
                        })
                .when(orderWriteMapper)
                .insertOrderInfo(any(OrderInfoEntity.class));

        // when
        OrderCreateResponse response =
                orderCommandService.createCustomerOrder(username, request, "WEB_MALL");

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("12345");
        assertThat(response.orderNo()).startsWith("SCM");
        assertThat(response.payAmount()).isCloseTo(210.00, within(0.001));
        assertThat(response.usedPoints()).isZero();
        assertThat(response.pointsDeductionAmount()).isCloseTo(0.0, within(0.001));
        assertThat(response.orderStatus()).isEqualTo(OrderStatusEnum.WAIT_PAY.getCode());
        assertThat(response.payStatus()).isEqualTo(PayStatusEnum.UNPAID.getCode());

        verify(orderWriteMapper)
                .insertOrderInfo(
                        argThat(
                                order ->
                                        order.getOrderNo() != null
                                                && order.getCustomerId().equals(1000L)
                                                && order.getMerchantId().equals(200L)
                                                && "WEB_MALL".equals(order.getOrderSource())
                                                && OrderStatusEnum.WAIT_PAY.equals(
                                                        order.getOrderStatus())
                                                && PayStatusEnum.UNPAID.equals(order.getPayStatus())
                                                && ShipmentStatusEnum.WAIT_SHIP.equals(
                                                        order.getShipmentStatus())
                                                && order.getPayAmount()
                                                                .compareTo(new BigDecimal("210.00"))
                                                        == 0
                                                && order.getGoodsAmount()
                                                                .compareTo(new BigDecimal("200.00"))
                                                        == 0
                                                && order.getFreightAmount()
                                                                .compareTo(new BigDecimal("10.00"))
                                                        == 0));
        verify(orderWriteMapper).insertOrderItems(anyList());
        verify(orderWriteMapper).insertOrderStatusLog(any());
        verify(fileStorageService)
                .bindFileIfPresent(eq((Long) null), eq("ORDER_CONTRACT"), eq(12345L), eq(100L));
        verify(pointLedgerService).consumeOrderPoints(12345L, 1000L, 0, response.orderNo());
    }

    @Test
    @DisplayName("库存不足时应抛出 BusinessException")
    void shouldThrowBusinessExceptionWhenStockInsufficient() {
        // given
        String username = "customer";
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(1L, 10L, 5);
        CreateOrderRequest request =
                new CreateOrderRequest(
                        "张三",
                        "13800000000",
                        "广东省",
                        "深圳市",
                        "南山区",
                        "科技园",
                        "BANK_TRANSFER",
                        null,
                        null,
                        false,
                        List.of(itemRequest));

        OrderCreateContextRow context = new OrderCreateContextRow();
        context.setUserId(100L);
        context.setCustomerId(1000L);
        context.setCustomerName("Test Customer");
        context.setMemberLevel("GOLD");

        OrderCreateGoodsRow goods = new OrderCreateGoodsRow();
        goods.setMerchantGoodsId(1L);
        goods.setMerchantId(200L);
        goods.setSkuId(10L);
        goods.setSpuName("手机");
        goods.setSkuName("黑色 128G");
        goods.setSpecText("黑色/128G");
        goods.setSalePrice(new BigDecimal("150.00"));
        goods.setMemberPrice(new BigDecimal("120.00"));
        goods.setCustomerPrice(new BigDecimal("100.00"));
        goods.setCostPrice(new BigDecimal("80.00"));
        goods.setStockQty(2);
        goods.setAuthorized(true);

        when(orderWriteMapper.selectCustomerContextByUsername(username)).thenReturn(context);
        when(orderWriteMapper.selectGoodsForCreate(List.of(1L), 1000L, "GOLD"))
                .thenReturn(List.of(goods));

        // when / then
        assertThatThrownBy(
                        () ->
                                orderCommandService.createCustomerOrder(
                                        username, request, "WEB_MALL"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("所选商品库存不足")
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo(400));
    }
}
