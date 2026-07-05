package com.telecom.scm.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.telecom.scm.common.base.BaseEntity;

/**
 * 实体类规范验证测试。
 *
 * <p>验证：实体继承 BaseEntity、Lombok 生成 getter/setter、可序列化。
 */
class OrderInfoEntityTest {

    @Test
    @DisplayName("OrderInfoEntity 应继承 BaseEntity")
    void shouldExtendBaseEntity() {
        OrderInfoEntity entity = new OrderInfoEntity();
        assertThat(entity).isInstanceOf(BaseEntity.class);
    }

    @Test
    @DisplayName("应通过 Lombok 生成的 setter/getter 读写业务字段和基类字段")
    void shouldUseLombokGettersAndSetters() {
        // given
        OrderInfoEntity entity = new OrderInfoEntity();

        // when
        entity.setId(123L);
        entity.setOrderNo("SCM20260615000003003");
        entity.setPayAmount(new BigDecimal("99.99"));
        entity.setCreatedBy(1L);
        entity.setDeleted(0);
        entity.setVersion(1);

        // then
        assertThat(entity.getId()).isEqualTo(123L);
        assertThat(entity.getOrderNo()).isEqualTo("SCM20260615000003003");
        assertThat(entity.getPayAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(entity.getCreatedBy()).isEqualTo(1L);
        assertThat(entity.getDeleted()).isZero();
        assertThat(entity.getVersion()).isEqualTo(1);
    }
}
