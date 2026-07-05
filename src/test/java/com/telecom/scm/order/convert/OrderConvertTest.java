package com.telecom.scm.order.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.telecom.scm.common.enums.OrderStatusEnum;
import com.telecom.scm.common.enums.PayStatusEnum;
import com.telecom.scm.order.dto.response.OrderCreateResponse;
import com.telecom.scm.order.dto.response.OrderSummaryResponse;
import com.telecom.scm.order.entity.OrderInfoEntity;
import com.telecom.scm.order.mapper.OrderRow;

/**
 * OrderConvert 单元测试示例。
 *
 * <p>企业级项目中，转换器逻辑虽然简单，也应该有单测覆盖，防止字段映射遗漏或类型转换错误。
 */
class OrderConvertTest {

    @Test
    @DisplayName("应正确把 OrderInfoEntity 转换为 OrderCreateResponse")
    void shouldConvertOrderInfoToOrderCreateResponse() {
        // given
        OrderInfoEntity entity = new OrderInfoEntity();
        entity.setId(10086L);
        entity.setOrderNo("SCM20260615000001001");
        entity.setPayAmount(new BigDecimal("199.99"));
        entity.setUsedPoints(500L);
        entity.setPointsDeductionAmount(new BigDecimal("5.00"));
        entity.setOrderStatus(OrderStatusEnum.WAIT_PAY);
        entity.setPayStatus(PayStatusEnum.UNPAID);

        // when
        OrderCreateResponse response = OrderConvert.INSTANCE.toOrderCreateResponse(entity);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("10086");
        assertThat(response.orderNo()).isEqualTo("SCM20260615000001001");
        assertThat(response.payAmount()).isCloseTo(199.99, within(0.001));
        assertThat(response.usedPoints()).isEqualTo(500);
        assertThat(response.pointsDeductionAmount()).isCloseTo(5.00, within(0.001));
        assertThat(response.orderStatus()).isEqualTo(OrderStatusEnum.WAIT_PAY.getCode());
        assertThat(response.payStatus()).isEqualTo(PayStatusEnum.UNPAID.getCode());
    }

    @Test
    @DisplayName("当金额为 null 时，应转换为 0.0")
    void shouldConvertNullAmountToZero() {
        // given
        OrderInfoEntity entity = new OrderInfoEntity();
        entity.setId(1L);
        entity.setOrderNo("SCM001");

        // when
        OrderCreateResponse response = OrderConvert.INSTANCE.toOrderCreateResponse(entity);

        // then
        assertThat(response.payAmount()).isEqualTo(0.0);
        assertThat(response.pointsDeductionAmount()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("应正确把 OrderRow 转换为 OrderSummaryResponse，并填充默认订单来源")
    void shouldConvertOrderRowToOrderSummaryResponse() {
        // given
        OrderRow row = new OrderRow();
        row.setId("10010");
        row.setOrderNo("SCM20260615000002002");
        row.setCustomerName("测试客户");
        row.setMerchantName("测试商家");
        row.setAmount(299.99);
        row.setStatus("WAIT_PAY");
        row.setPayStatus("UNPAID");
        row.setLogisticsNo("-");
        row.setCreatedAt("2026-06-15 10:00:00");
        row.setAftersaleStatus("NONE");
        row.setOrderSource(null);

        // when
        OrderSummaryResponse response = OrderConvert.INSTANCE.toOrderSummaryResponse(row);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("10010");
        assertThat(response.orderNo()).isEqualTo("SCM20260615000002002");
        assertThat(response.customerName()).isEqualTo("测试客户");
        assertThat(response.merchantName()).isEqualTo("测试商家");
        assertThat(response.amount()).isCloseTo(299.99, within(0.001));
        assertThat(response.orderSource()).isEqualTo("WEB_MALL");
    }

    @Test
    @DisplayName("应批量转换 OrderRow 列表")
    void shouldConvertOrderRowList() {
        // given
        OrderRow row = new OrderRow();
        row.setId("1");
        row.setOrderNo("SCM001");
        row.setAmount(100.0);
        row.setOrderSource("APP_MALL");

        // when
        List<OrderSummaryResponse> responses =
                OrderConvert.INSTANCE.toOrderSummaryResponseList(List.of(row));

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).orderSource()).isEqualTo("APP_MALL");
    }
}
