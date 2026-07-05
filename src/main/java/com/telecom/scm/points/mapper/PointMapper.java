package com.telecom.scm.points.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.telecom.scm.points.entity.PointAccountEntity;
import com.telecom.scm.points.entity.PointRecordEntity;
import com.telecom.scm.points.entity.PointRuleEntity;

@Mapper
public interface PointMapper {

    @Select(
            """
        SELECT
            u.id AS userId,
            customer.id AS customerId,
            customer.company_name AS customerName,
            customer.invite_code AS inviteCode
        FROM sys_user u
        JOIN member_account ma ON ma.id = u.member_id
            AND ma.deleted = 0
            AND ma.status = 'ENABLED'
            AND ma.member_type = 'CUSTOMER'
        JOIN customer_info customer ON customer.member_id = ma.id
            AND customer.deleted = 0
            AND customer.status = 'ENABLED'
        WHERE u.deleted = 0
            AND u.status = 'ENABLED'
            AND u.username = #{username}
        LIMIT 1
        """)
    Map<String, Object> selectCustomerContextByUsername(@Param("username") String username);

    @Select(
            """
        SELECT
            id,
            current_points AS currentPoints,
            total_increase AS totalIncrease,
            total_decrease AS totalDecrease,
            status
        FROM point_account
        WHERE customer_id = #{customerId}
        LIMIT 1
        """)
    PointAccountEntity selectPointAccountByCustomerId(@Param("customerId") Long customerId);

    @Insert(
            """
        INSERT INTO point_account (
            customer_id,
            current_points,
            total_increase,
            total_decrease,
            status
        ) VALUES (
            #{customerId},
            0,
            0,
            0,
            'ENABLED'
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPointAccount(PointAccountEntity payload);

    @Update(
            """
        UPDATE point_account
        SET current_points = current_points + #{points},
            total_increase = total_increase + #{points},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{pointAccountId}
        """)
    int increasePoints(@Param("pointAccountId") Long pointAccountId, @Param("points") int points);

    @Update(
            """
        UPDATE point_account
        SET current_points = current_points - #{points},
            total_decrease = total_decrease + #{points},
            updated_time = CURRENT_TIMESTAMP
        WHERE id = #{pointAccountId}
        """)
    int decreasePoints(@Param("pointAccountId") Long pointAccountId, @Param("points") int points);

    @Insert(
            """
        INSERT INTO point_record (
            point_account_id,
            customer_id,
            change_type,
            source_type,
            source_id,
            change_points,
            balance_after,
            remark
        ) VALUES (
            #{pointAccountId},
            #{customerId},
            #{changeType},
            #{sourceType},
            #{sourceId},
            #{changePoints},
            #{balanceAfter},
            #{remark}
        )
        """)
    int insertPointRecord(PointRecordEntity payload);

    @Select(
            """
        SELECT COUNT(1)
        FROM point_record
        WHERE customer_id = #{customerId}
            AND source_type = #{sourceType}
            AND source_id = #{sourceId}
        """)
    long countPointRecordsBySource(
            @Param("customerId") Long customerId,
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId);

    @Select(
            """
        SELECT
            CAST(id AS CHAR) AS id,
            source_type AS sourceType,
            change_type AS changeType,
            change_points AS changePoints,
            balance_after AS balanceAfter,
            remark AS remark,
            DATE_FORMAT(created_time, '%Y-%m-%d %H:%i') AS createdAt
        FROM point_record
        WHERE customer_id = #{customerId}
        ORDER BY created_time DESC, id DESC
        LIMIT 50
        """)
    List<Map<String, Object>> selectPointRecordsByCustomerId(@Param("customerId") Long customerId);

    @Select(
            """
        <script>
        SELECT
            CAST(id AS CHAR) AS id,
            source_type AS sourceType,
            change_type AS changeType,
            change_points AS changePoints,
            balance_after AS balanceAfter,
            remark AS remark,
            DATE_FORMAT(created_time, '%Y-%m-%d %H:%i') AS createdAt
        FROM point_record
        WHERE customer_id = #{customerId}
        <if test='changeType != null and changeType != ""'>
            AND change_type = #{changeType}
        </if>
        <if test='startDate != null and startDate != ""'>
            AND DATE(created_time) &gt;= #{startDate}
        </if>
        <if test='endDate != null and endDate != ""'>
            AND DATE(created_time) &lt;= #{endDate}
        </if>
        ORDER BY created_time DESC, id DESC
        LIMIT #{offset}, #{pageSize}
        </script>
        """)
    List<Map<String, Object>> selectPointRecordsByCustomerIdPaged(
            @Param("customerId") Long customerId,
            @Param("changeType") String changeType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    @Select(
            """
        <script>
        SELECT COUNT(1)
        FROM point_record
        WHERE customer_id = #{customerId}
        <if test='changeType != null and changeType != ""'>
            AND change_type = #{changeType}
        </if>
        <if test='startDate != null and startDate != ""'>
            AND DATE(created_time) &gt;= #{startDate}
        </if>
        <if test='endDate != null and endDate != ""'>
            AND DATE(created_time) &lt;= #{endDate}
        </if>
        </script>
        """)
    long countPointRecordsByCustomerId(
            @Param("customerId") Long customerId,
            @Param("changeType") String changeType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    @Select(
            """
        SELECT
            rule_type AS ruleType,
            value_type AS valueType,
            rule_value AS ruleValue,
            deduction_ratio AS deductionRatio,
            max_deduction_ratio AS maxDeductionRatio,
            is_deduction_enabled AS deductionEnabled
        FROM point_rule
        WHERE deleted = 0
            AND status = 'ENABLED'
            AND rule_type = #{ruleType}
        ORDER BY id DESC
        LIMIT 1
        """)
    PointRuleEntity selectPointRule(@Param("ruleType") String ruleType);

    @Select(
            """
        SELECT COALESCE(SUM(change_points), 0)
        FROM point_record
        WHERE customer_id = #{customerId}
            AND change_type = 'INCREASE'
            AND source_type = 'ORDER'
            AND source_id = #{orderId}
        """)
    Integer sumOrderRewardPoints(
            @Param("customerId") Long customerId, @Param("orderId") Long orderId);

    @Select(
            """
        SELECT COALESCE(SUM(pr.change_points), 0)
        FROM point_record pr
        JOIN aftersale_info aftersale ON aftersale.id = pr.source_id
            AND aftersale.deleted = 0
        WHERE pr.customer_id = #{customerId}
            AND pr.change_type = 'DECREASE'
            AND pr.source_type = 'REFUND_RECLAIM'
            AND aftersale.order_id = #{orderId}
        """)
    Integer sumRefundReclaimPointsByOrder(
            @Param("customerId") Long customerId, @Param("orderId") Long orderId);

    @Select(
            """
        SELECT COALESCE(SUM(pr.change_points), 0)
        FROM point_record pr
        JOIN aftersale_info aftersale ON aftersale.id = pr.source_id
            AND aftersale.deleted = 0
        WHERE pr.customer_id = #{customerId}
            AND pr.change_type = 'INCREASE'
            AND pr.source_type = 'REFUND_RETURN'
            AND aftersale.order_id = #{orderId}
        """)
    Integer sumRefundReturnPointsByOrder(
            @Param("customerId") Long customerId, @Param("orderId") Long orderId);

    @Select(
            """
        SELECT referrer_customer_id AS referrerCustomerId
        FROM customer_referral
        WHERE referred_customer_id = #{customerId}
            AND status = 'BOUND'
        LIMIT 1
        """)
    Long selectReferrerCustomerId(@Param("customerId") Long customerId);

    @Select(
            """
        SELECT
            customer.invite_code AS inviteCode,
            customer.company_name AS customerName,
            referrer_customer.company_name AS referrerName
        FROM customer_info customer
        LEFT JOIN customer_referral referral ON referral.referred_customer_id = customer.id
            AND referral.status = 'BOUND'
        LEFT JOIN customer_info referrer_customer ON referrer_customer.id = referral.referrer_customer_id
            AND referrer_customer.deleted = 0
        WHERE customer.id = #{customerId}
            AND customer.deleted = 0
        LIMIT 1
        """)
    Map<String, Object> selectReferralSummary(@Param("customerId") Long customerId);

    @Select(
            """
        SELECT
            CAST(referral.id AS CHAR) AS id,
            referred.company_name AS referredCustomerName,
            DATE_FORMAT(referral.bind_time, '%Y-%m-%d %H:%i') AS bindTime,
            referral.status AS status
        FROM customer_referral referral
        JOIN customer_info referred ON referred.id = referral.referred_customer_id
            AND referred.deleted = 0
        WHERE referral.referrer_customer_id = #{customerId}
        ORDER BY referral.bind_time DESC
        """)
    List<Map<String, Object>> selectReferralRows(@Param("customerId") Long customerId);

    @Select(
            """
        SELECT COALESCE(SUM(change_points), 0)
        FROM point_record
        WHERE customer_id = #{customerId}
            AND source_type = 'REFERRAL'
            AND change_type = 'INCREASE'
        """)
    Integer sumReferralBonusPoints(@Param("customerId") Long customerId);

    @Select(
            """
        SELECT COALESCE(SUM(order_item.final_amount * mg.rebate_rate / 100), 0)
        FROM order_item order_item
        JOIN merchant_goods mg ON mg.id = order_item.merchant_goods_id
            AND mg.deleted = 0
        WHERE order_item.order_id = #{orderId}
        """)
    BigDecimal selectOrderRebatePoints(@Param("orderId") Long orderId);
}
