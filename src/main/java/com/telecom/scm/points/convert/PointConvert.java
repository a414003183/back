package com.telecom.scm.points.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.points.entity.PointAccountEntity;
import com.telecom.scm.points.entity.PointRecordEntity;

/**
 * 积分领域对象转换器。
 *
 * <p>使用 MapStruct 替代手写 getter/setter 构造 PointRecordEntity 和 PointAccountEntity。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PointConvert {

    PointConvert INSTANCE = Mappers.getMapper(PointConvert.class);

    /** 构建积分流水记录。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "remark", source = "remark")
    PointRecordEntity toPointRecordEntity(
            Long pointAccountId,
            Long customerId,
            String changeType,
            String sourceType,
            Long sourceId,
            Integer changePoints,
            Integer balanceAfter,
            String remark);

    /** 构建初始积分账户。 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "currentPoints", constant = "0")
    @Mapping(target = "totalIncrease", constant = "0")
    @Mapping(target = "totalDecrease", constant = "0")
    @Mapping(target = "status", source = "status")
    PointAccountEntity toPointAccountEntity(Long customerId, AccountStatusEnum status);
}
