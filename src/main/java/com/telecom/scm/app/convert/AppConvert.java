package com.telecom.scm.app.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.app.dto.response.CustomerAddressResponse;
import com.telecom.scm.app.dto.response.CustomerOrderDetailResponse;
import com.telecom.scm.app.dto.response.CustomerProfileResponse;
import com.telecom.scm.app.mapper.CustomerAddressRow;
import com.telecom.scm.app.mapper.CustomerOrderDetailRow;
import com.telecom.scm.app.mapper.CustomerOrderItemRow;
import com.telecom.scm.app.mapper.CustomerProfileRow;

/**
 * App 模块领域对象转换器。
 *
 * <p>统一使用 MapStruct 将 Row/Entity 转换为 Response DTO，避免 Service 中手写构造器。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppConvert {

    AppConvert INSTANCE = Mappers.getMapper(AppConvert.class);

    /** 客户资料行 + 默认地址 -> 客户资料响应。 */
    @Mapping(target = "defaultAddress", source = "defaultAddress")
    CustomerProfileResponse toCustomerProfileResponse(
            CustomerProfileRow profile, CustomerAddressResponse defaultAddress);

    /**
     * 地址行 -> 地址响应。
     *
     * <p>对可能为空的字符串字段兜底为空字符串，保持与原手写转换一致。
     */
    @Mapping(target = "receiverName", source = "receiverName", defaultValue = "")
    @Mapping(target = "receiverPhone", source = "receiverPhone", defaultValue = "")
    @Mapping(target = "receiverProvince", source = "receiverProvince", defaultValue = "")
    @Mapping(target = "receiverCity", source = "receiverCity", defaultValue = "")
    @Mapping(target = "receiverDistrict", source = "receiverDistrict", defaultValue = "")
    @Mapping(target = "receiverAddress", source = "receiverAddress", defaultValue = "")
    CustomerAddressResponse toCustomerAddressResponse(CustomerAddressRow address);

    /** 订单详情行 + 订单商品行列表 -> 客户订单详情响应。 */
    @Mapping(target = "items", source = "items")
    CustomerOrderDetailResponse toCustomerOrderDetailResponse(
            CustomerOrderDetailRow detail, List<CustomerOrderItemRow> items);
}
