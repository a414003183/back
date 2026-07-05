package com.telecom.scm.aftersale.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.aftersale.dto.response.AftersaleActionResponse;
import com.telecom.scm.aftersale.dto.response.AftersaleSummaryResponse;
import com.telecom.scm.aftersale.entity.AftersaleInfoEntity;
import com.telecom.scm.aftersale.mapper.AftersaleProcessRow;
import com.telecom.scm.aftersale.mapper.AftersaleRow;

/**
 * 售后领域对象转换器。
 *
 * <p>使用 MapStruct 替代手写 getter/setter 转换，复杂类型（Long/BigDecimal/空字符串）通过显式映射处理。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AftersaleConvert {

    AftersaleConvert INSTANCE = Mappers.getMapper(AftersaleConvert.class);

    /** 售后主表实体转换为售后操作响应。 */
    @Mapping(target = "id", expression = "java(String.valueOf(entity.getId()))")
    @Mapping(
            target = "returnTrackingNo",
            source = "returnTrackingNo",
            qualifiedByName = "safeTracking")
    AftersaleActionResponse toAftersaleActionResponse(AftersaleInfoEntity entity);

    /** 售后处理行对象转换为售后操作响应，支持传入计算后的状态。 */
    @Mapping(target = "id", expression = "java(String.valueOf(row.getId()))")
    @Mapping(target = "aftersaleStatus", source = "status")
    @Mapping(
            target = "returnTrackingNo",
            source = "row.returnTrackingNo",
            qualifiedByName = "safeTracking")
    AftersaleActionResponse toAftersaleActionResponse(AftersaleProcessRow row, String status);

    /** 售后行对象转换为列表响应。 */
    @Mapping(
            target = "returnTrackingNo",
            source = "returnTrackingNo",
            qualifiedByName = "safeTracking")
    AftersaleSummaryResponse toAftersaleSummaryResponse(AftersaleRow row);

    /** 售后行对象列表批量转换。 */
    List<AftersaleSummaryResponse> toAftersaleSummaryResponseList(List<AftersaleRow> rows);

    /** 将空或空白物流单号统一映射为 "-"，保证前端展示一致性。 */
    @Named("safeTracking")
    default String safeTracking(String trackingNo) {
        return trackingNo == null || trackingNo.isBlank() ? "-" : trackingNo;
    }
}
