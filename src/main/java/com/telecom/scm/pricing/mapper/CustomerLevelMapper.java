package com.telecom.scm.pricing.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.pricing.entity.CustomerLevelConfigEntity;
import com.telecom.scm.pricing.mapper.row.CustomerGrowthInfoRow;
import com.telecom.scm.pricing.mapper.row.CustomerLevelConfigRow;
import com.telecom.scm.pricing.mapper.row.MemberLevelOptionRow;

@Mapper
public interface CustomerLevelMapper {

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            level_code AS levelCode,
            level_name AS levelName,
            upgrade_threshold_amount AS upgradeThresholdAmount,
            sort_no AS sortNo,
            status AS status,
            remark AS remark,
            DATE_FORMAT(updated_time, '%Y-%m-%d %H:%i:%s') AS updatedAt
        FROM customer_level_config
        WHERE deleted = 0
        ORDER BY upgrade_threshold_amount ASC, sort_no ASC, id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<CustomerLevelConfigRow> selectCustomerLevelConfigs(
            @Param("offset") int offset, @Param("limit") int limit);

    @Select(
            """
        SELECT COUNT(*)
        FROM customer_level_config
        WHERE deleted = 0
        """)
    long countCustomerLevelConfigs();

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            level_code AS levelCode,
            level_name AS levelName,
            upgrade_threshold_amount AS upgradeThresholdAmount,
            sort_no AS sortNo,
            status AS status,
            remark AS remark,
            DATE_FORMAT(updated_time, '%Y-%m-%d %H:%i:%s') AS updatedAt
        FROM customer_level_config
        WHERE deleted = 0
            AND level_code = #{levelCode}
        LIMIT 1
        """)
    CustomerLevelConfigRow selectCustomerLevelConfigByLevelCode(
            @Param("levelCode") String levelCode);

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            level_code AS levelCode,
            level_name AS levelName,
            upgrade_threshold_amount AS upgradeThresholdAmount,
            sort_no AS sortNo,
            status AS status,
            remark AS remark,
            DATE_FORMAT(updated_time, '%Y-%m-%d %H:%i:%s') AS updatedAt
        FROM customer_level_config
        WHERE deleted = 0
        ORDER BY upgrade_threshold_amount ASC, sort_no ASC, id DESC
        """)
    List<CustomerLevelConfigRow> selectAllCustomerLevelConfigs();

    @Select(
            """
        SELECT
            level_code AS value,
            level_name AS label,
            upgrade_threshold_amount AS upgradeThresholdAmount
        FROM customer_level_config
        WHERE deleted = 0
            AND status = 'ENABLED'
        ORDER BY upgrade_threshold_amount ASC, sort_no ASC, id DESC
        """)
    List<MemberLevelOptionRow> selectEnabledCustomerLevelOptions();

    int upsertCustomerLevelConfig(CustomerLevelConfigEntity payload);

    @Select(
            """
        SELECT
            member_level AS memberLevel,
            accumulated_paid_amount AS accumulatedPaidAmount,
            DATE_FORMAT(last_level_upgrade_at, '%Y-%m-%d %H:%i:%s') AS lastLevelUpgradeAt
        FROM customer_info
        WHERE id = #{customerId}
            AND deleted = 0
        LIMIT 1
        """)
    CustomerGrowthInfoRow selectCustomerGrowthInfo(@Param("customerId") Long customerId);

    @Select(
            """
        SELECT COALESCE(SUM(pay_amount), 0)
        FROM order_info
        WHERE deleted = 0
            AND customer_id = #{customerId}
            AND order_status = 'FINISHED'
        """)
    BigDecimal selectFinishedPaidAmountByCustomerId(@Param("customerId") Long customerId);

    @Update(
            """
        UPDATE customer_info
        SET accumulated_paid_amount = #{accumulatedPaidAmount},
            member_level = #{memberLevel},
            last_level_upgrade_at = COALESCE(#{lastLevelUpgradeAt}, last_level_upgrade_at),
            updated_by = #{updatedBy},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{customerId}
            AND deleted = 0
        """)
    int updateCustomerGrowth(
            @Param("customerId") Long customerId,
            @Param("accumulatedPaidAmount") BigDecimal accumulatedPaidAmount,
            @Param("memberLevel") String memberLevel,
            @Param("lastLevelUpgradeAt") java.time.LocalDateTime lastLevelUpgradeAt,
            @Param("updatedBy") Long updatedBy);

    int backfillAccumulatedPaidAmount();

    @Update(
            """
        UPDATE customer_level_config
        SET status = #{status},
            updated_time = CURRENT_TIMESTAMP
        WHERE level_code = #{levelCode}
            AND deleted = 0
        """)
    int updateCustomerLevelStatus(CustomerLevelConfigEntity payload);
}
