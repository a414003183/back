package com.telecom.scm.order.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.order.dto.response.OrderContractResponse;
import com.telecom.scm.order.mapper.OrderContractRow;

/** 订单单据/文档转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDocumentConvert {

    OrderDocumentConvert INSTANCE = Mappers.getMapper(OrderDocumentConvert.class);

    /** 订单合同行对象转换为合同响应。 */
    @Mapping(target = "downloadUrl", expression = "java(\"/api/files/\" + row.getId())")
    OrderContractResponse toOrderContractResponse(OrderContractRow row);

    List<OrderContractResponse> toOrderContractResponseList(List<OrderContractRow> rows);
}
