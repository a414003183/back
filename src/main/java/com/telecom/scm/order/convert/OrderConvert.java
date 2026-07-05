package com.telecom.scm.order.convert;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.order.dto.response.MerchantOrderDetailResponse;
import com.telecom.scm.order.dto.response.OrderCreateResponse;
import com.telecom.scm.order.dto.response.OrderSummaryResponse;
import com.telecom.scm.order.entity.OrderInfoEntity;
import com.telecom.scm.order.mapper.MerchantOrderDetailRow;
import com.telecom.scm.order.mapper.OrderRow;

/**
 * 订单领域对象转换器。
 *
 * <p>企业级项目通常使用 MapStruct 替代手写 getter/setter 转换，优点：
 *
 * <ul>
 *   <li>编译期生成代码，性能接近手写
 *   <li>类型安全，字段遗漏在编译期就能发现
 *   <li>避免 BeanUtils 反射带来的性能损耗和空指针风险
 * </ul>
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConvert {

    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);

    /**
     * 将订单实体转换为下单响应。
     *
     * <p>注意：实体中的 Long/BigDecimal 需要显式映射到 DTO 的 String/double。
     */
    @Mapping(target = "id", expression = "java(String.valueOf(orderInfoEntity.getId()))")
    @Mapping(target = "payAmount", source = "payAmount", qualifiedByName = "bigDecimalToDouble")
    @Mapping(
            target = "pointsDeductionAmount",
            source = "pointsDeductionAmount",
            qualifiedByName = "bigDecimalToDouble")
    OrderCreateResponse toOrderCreateResponse(OrderInfoEntity orderInfoEntity);

    /**
     * 订单行对象转换为列表响应。
     *
     * <p>orderSource 为空时默认填充 WEB_MALL。
     */
    @Mapping(target = "orderSource", source = "orderSource", defaultValue = "WEB_MALL")
    OrderSummaryResponse toOrderSummaryResponse(OrderRow orderRow);

    List<OrderSummaryResponse> toOrderSummaryResponseList(List<OrderRow> orderRows);

    /**
     * 商家订单详情行对象转换为详情响应。
     *
     * <p>items 与 adjustLogs 由调用方单独设置。
     */
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "adjustLogs", ignore = true)
    MerchantOrderDetailResponse toMerchantOrderDetailResponse(MerchantOrderDetailRow row);

    @Named("bigDecimalToDouble")
    default double bigDecimalToDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}
