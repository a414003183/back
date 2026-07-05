package com.telecom.scm.common.base;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import com.telecom.scm.order.entity.OrderInfoEntity;

@ExtendWith(MockitoExtension.class)
class DefaultEntityMetaObjectHandlerTest {

    private DefaultEntityMetaObjectHandler handler;

    @BeforeAll
    static void initTableInfo() {
        Configuration configuration = new Configuration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, OrderInfoEntity.class);
    }

    @BeforeEach
    void setUp() {
        handler = new DefaultEntityMetaObjectHandler();
    }

    @AfterEach
    void tearDown() {
        CurrentUserHolder.clear();
    }

    @Test
    @DisplayName("CurrentUserHolder 应在线程间隔离")
    void shouldIsolateCurrentUserHolderByThread() throws Exception {
        // given
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        long[] expectedIds = {1L, 2L, 3L};
        AtomicReference<Throwable> error = new AtomicReference<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            final long userId = expectedIds[i];
            new Thread(
                            () -> {
                                try {
                                    CurrentUserHolder.setUserId(userId);
                                    Thread.sleep(50);
                                    assertThat(CurrentUserHolder.getUserId()).isEqualTo(userId);
                                } catch (Throwable t) {
                                    error.set(t);
                                } finally {
                                    CurrentUserHolder.clear();
                                    latch.countDown();
                                }
                            })
                    .start();
        }
        latch.await();

        // then
        assertThat(CurrentUserHolder.getUserId()).isNull();
        assertThat(error.get()).isNull();
    }

    @Test
    @DisplayName("insertFill 应自动填充 BaseEntity 的公共字段")
    void shouldFillBaseEntityFieldsOnInsert() {
        // given
        CurrentUserHolder.setUserId(42L);
        OrderInfoEntity entity = new OrderInfoEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        // when
        handler.insertFill(metaObject);

        // then
        assertThat(entity.getCreatedTime()).isNotNull();
        assertThat(entity.getUpdatedTime()).isNotNull();
        assertThat(entity.getDeleted()).isZero();
        assertThat(entity.getVersion()).isZero();
        assertThat(entity.getCreatedBy()).isEqualTo(42L);
        assertThat(entity.getUpdatedBy()).isEqualTo(42L);
    }

    @Test
    @DisplayName("当 CurrentUserHolder 为空时，insertFill 不应填充 createdBy / updatedBy")
    void shouldNotFillOperatorWhenCurrentUserIsEmpty() {
        // given
        OrderInfoEntity entity = new OrderInfoEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        // when
        handler.insertFill(metaObject);

        // then
        assertThat(entity.getCreatedTime()).isNotNull();
        assertThat(entity.getCreatedBy()).isNull();
        assertThat(entity.getUpdatedBy()).isNull();
    }

    @Test
    @DisplayName("updateFill 应自动填充 updatedTime 和 updatedBy")
    void shouldFillUpdatedFieldsOnUpdate() {
        // given
        CurrentUserHolder.setUserId(99L);
        OrderInfoEntity entity = new OrderInfoEntity();
        entity.setCreatedTime(LocalDateTime.now().minusDays(1));
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        // when
        handler.updateFill(metaObject);

        // then
        assertThat(entity.getUpdatedTime()).isNotNull();
        assertThat(entity.getUpdatedTime()).isAfter(entity.getCreatedTime());
        assertThat(entity.getUpdatedBy()).isEqualTo(99L);
    }
}
