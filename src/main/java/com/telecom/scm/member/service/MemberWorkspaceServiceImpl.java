package com.telecom.scm.member.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.member.convert.MerchantConvert;
import com.telecom.scm.member.convert.WorkspaceConvert;
import com.telecom.scm.member.dto.request.SaveCustomerProfileRequest;
import com.telecom.scm.member.dto.request.SaveMerchantProfileRequest;
import com.telecom.scm.member.dto.request.SaveSupplierProfileRequest;
import com.telecom.scm.member.dto.response.MerchantReportOverviewResponse;
import com.telecom.scm.member.dto.response.MerchantReportResponse;
import com.telecom.scm.member.entity.CustomerProfileEntity;
import com.telecom.scm.member.entity.MemberAddressEntity;
import com.telecom.scm.member.entity.MerchantProfileEntity;
import com.telecom.scm.member.entity.SupplierProfileEntity;
import com.telecom.scm.member.mapper.MemberWorkspaceMapper;
import com.telecom.scm.member.mapper.row.CustomerContextRow;
import com.telecom.scm.member.mapper.row.CustomerProfileRow;
import com.telecom.scm.member.mapper.row.MerchantContextRow;
import com.telecom.scm.member.mapper.row.MerchantCustomerRankingRow;
import com.telecom.scm.member.mapper.row.MerchantProductRankingRow;
import com.telecom.scm.member.mapper.row.MerchantProfileRow;
import com.telecom.scm.member.mapper.row.MerchantReportSummaryRow;
import com.telecom.scm.member.mapper.row.MerchantShipmentRow;
import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplierCooperationRow;
import com.telecom.scm.member.mapper.row.SupplierProfileRow;
import com.telecom.scm.member.mapper.row.SupplierStockRow;

@Service
public class MemberWorkspaceServiceImpl implements MemberWorkspaceService {

    private final MemberWorkspaceMapper memberWorkspaceMapper;

    public MemberWorkspaceServiceImpl(MemberWorkspaceMapper memberWorkspaceMapper) {
        this.memberWorkspaceMapper = memberWorkspaceMapper;
    }

    @Override
    public CustomerProfileRow customerProfile(String username) {
        CustomerProfileRow data = memberWorkspaceMapper.selectCustomerProfile(username);
        if (data == null || data.getCompanyName() == null) {
            throw new BusinessException(404, "customer profile not found");
        }
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerProfileRow saveCustomerProfile(
            String username, SaveCustomerProfileRequest request) {
        CustomerContextRow context =
                memberWorkspaceMapper.selectCustomerContextByUsername(username);
        if (context == null || context.getCustomerId() == null) {
            throw new BusinessException(403, "customer account is unavailable");
        }

        CustomerProfileEntity profileParam = new CustomerProfileEntity();
        profileParam.setCustomerId(context.getCustomerId());
        profileParam.setCompanyName(request.companyName().trim());
        profileParam.setContactName(request.contactName().trim());
        profileParam.setContactPhone(request.contactPhone().trim());
        profileParam.setInvoiceTitle(normalizeText(request.invoiceTitle()));
        profileParam.setTaxNo(normalizeText(request.taxNo()));
        profileParam.setBankName(normalizeText(request.bankName()));
        profileParam.setBankAccount(normalizeText(request.bankAccount()));
        profileParam.setUpdatedBy(context.getUserId());
        memberWorkspaceMapper.updateCustomerProfile(profileParam);

        MemberAddressEntity addressParam = new MemberAddressEntity();
        addressParam.setMemberId(context.getMemberId());
        addressParam.setReceiverProvince(request.receiverProvince().trim());
        addressParam.setReceiverCity(request.receiverCity().trim());
        addressParam.setReceiverDistrict(request.receiverDistrict().trim());
        addressParam.setReceiverAddress(request.receiverAddress().trim());
        addressParam.setUpdatedBy(context.getUserId());
        int affected = memberWorkspaceMapper.updateDefaultReceiveAddress(addressParam);
        if (affected == 0) {
            MemberAddressEntity insertAddressParam = new MemberAddressEntity();
            insertAddressParam.setMemberId(context.getMemberId());
            insertAddressParam.setContactName(request.contactName().trim());
            insertAddressParam.setContactPhone(request.contactPhone().trim());
            insertAddressParam.setReceiverProvince(request.receiverProvince().trim());
            insertAddressParam.setReceiverCity(request.receiverCity().trim());
            insertAddressParam.setReceiverDistrict(request.receiverDistrict().trim());
            insertAddressParam.setReceiverAddress(request.receiverAddress().trim());
            insertAddressParam.setUpdatedBy(context.getUserId());
            memberWorkspaceMapper.insertDefaultReceiveAddress(insertAddressParam);
        }
        return customerProfile(username);
    }

    @Override
    public MerchantProfileRow merchantProfile(String username) {
        MerchantProfileRow data = memberWorkspaceMapper.selectMerchantProfile(username);
        if (data == null || data.getMerchantId() == null) {
            throw new BusinessException(404, "merchant profile not found");
        }
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantProfileRow saveMerchantProfile(
            String username, SaveMerchantProfileRequest request) {
        MerchantContextRow context =
                memberWorkspaceMapper.selectMerchantContextByUsername(username);
        if (context == null || context.getMerchantId() == null) {
            throw new BusinessException(403, "merchant account is unavailable");
        }

        MerchantProfileEntity param = new MerchantProfileEntity();
        param.setMerchantId(context.getMerchantId());
        param.setUpdatedBy(context.getUserId());
        param.setShopName(request.shopName().trim());
        param.setLicenseNo(normalizeText(request.licenseNo()));
        param.setContactName(request.contactName().trim());
        param.setContactPhone(request.contactPhone().trim());
        param.setShopDesc(normalizeText(request.shopDesc()));
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        memberWorkspaceMapper.updateMerchantProfile(param);
        return merchantProfile(username);
    }

    @Override
    public SupplierProfileRow supplierProfile(String username) {
        SupplierProfileRow data = memberWorkspaceMapper.selectSupplierProfile(username);
        if (data == null || data.getSupplierId() == null) {
            throw new BusinessException(404, "supplier profile not found");
        }
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SupplierProfileRow saveSupplierProfile(
            String username, SaveSupplierProfileRequest request) {
        SupplierContextRow context =
                memberWorkspaceMapper.selectSupplierContextByUsername(username);
        if (context == null || context.getSupplierId() == null) {
            throw new BusinessException(403, "supplier account is unavailable");
        }

        SupplierProfileEntity param = new SupplierProfileEntity();
        param.setSupplierId(context.getSupplierId());
        param.setUpdatedBy(context.getUserId());
        param.setSupplierName(request.supplierName().trim());
        param.setContactName(request.contactName().trim());
        param.setContactPhone(request.contactPhone().trim());
        param.setSupplyDesc(normalizeText(request.supplyDesc()));
        param.setQualificationFileId(request.qualificationFileId());
        memberWorkspaceMapper.updateSupplierProfile(param);
        return supplierProfile(username);
    }

    @Override
    public PageResult<SupplierStockRow> supplierStocks(String username, int page, int pageSize) {
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        long total = memberWorkspaceMapper.countSupplierStockRows(username);
        List<SupplierStockRow> rows =
                memberWorkspaceMapper.selectSupplierStockRows(username, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public PageResult<SupplierCooperationRow> supplierCooperation(
            String username, int page, int pageSize) {
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        long total = memberWorkspaceMapper.countSupplierCooperationRows(username);
        List<SupplierCooperationRow> rows =
                memberWorkspaceMapper.selectSupplierCooperationRows(username, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult<MerchantShipmentRow> merchantShipments(
            String username, int page, int pageSize) {
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        long total = memberWorkspaceMapper.countMerchantShipmentRows(username);
        List<MerchantShipmentRow> rows =
                memberWorkspaceMapper.selectMerchantShipmentRows(username, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public MerchantReportResponse merchantReports(
            String username, boolean includeProfitFields, String startDate, String endDate) {
        Long merchantId = requireMerchantId(username);
        MerchantReportDateRange dateRange = normalizeMerchantReportDateRange(startDate, endDate);
        return buildMerchantReportData(merchantId, includeProfitFields, dateRange);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> exportMerchantReport(
            String username,
            boolean includeProfitFields,
            String type,
            String startDate,
            String endDate) {
        Long merchantId = requireMerchantId(username);
        MerchantReportDateRange dateRange = normalizeMerchantReportDateRange(startDate, endDate);
        List<?> rows;
        List<String> headers;
        String fileName;
        String exportDate = LocalDate.now().toString();

        switch (type) {
            case "customers" -> {
                rows =
                        memberWorkspaceMapper.selectMerchantCustomerRanking(
                                merchantId, dateRange.startTime(), dateRange.endTime());
                headers =
                        new ArrayList<>(
                                List.of(
                                        "customerName",
                                        "orderCount",
                                        "grossAmount",
                                        "refundAmount",
                                        "netAmount"));
                if (includeProfitFields) {
                    headers.add("grossProfit");
                    headers.add("netProfit");
                }
                fileName = "merchant-customer-report-" + exportDate + ".csv";
            }
            case "products" -> {
                rows =
                        memberWorkspaceMapper.selectMerchantProductRanking(
                                merchantId, dateRange.startTime(), dateRange.endTime());
                headers =
                        new ArrayList<>(
                                List.of(
                                        "productName",
                                        "saleQty",
                                        "returnQty",
                                        "grossAmount",
                                        "refundAmount",
                                        "netAmount"));
                if (includeProfitFields) {
                    headers.add("grossProfit");
                    headers.add("netProfit");
                }
                fileName = "merchant-product-report-" + exportDate + ".csv";
            }
            default -> throw new BusinessException(400, "unsupported report export type");
        }

        byte[] content = buildCsv(headers, rows).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(content);
    }

    private MerchantReportResponse buildMerchantReportData(
            Long merchantId, boolean includeProfitFields, MerchantReportDateRange dateRange) {
        MerchantReportSummaryRow summary =
                memberWorkspaceMapper.selectMerchantReportSummary(
                        merchantId, dateRange.startTime(), dateRange.endTime());
        MerchantReportOverviewResponse overview =
                MerchantConvert.INSTANCE.toMerchantReportOverviewResponse(summary);
        overview.setNetSales(overview.getGrossSales().subtract(overview.getRefundAmount()));
        if (includeProfitFields) {
            overview.setNetProfit(
                    overview.getGrossProfit().subtract(overview.getProfitAdjustment()));
        } else {
            overview.setGrossProfit(null);
            overview.setProfitAdjustment(null);
            overview.setNetProfit(null);
        }

        List<MerchantCustomerRankingRow> customerRanking =
                memberWorkspaceMapper.selectMerchantCustomerRanking(
                        merchantId, dateRange.startTime(), dateRange.endTime());
        List<MerchantProductRankingRow> productRanking =
                memberWorkspaceMapper.selectMerchantProductRanking(
                        merchantId, dateRange.startTime(), dateRange.endTime());
        if (!includeProfitFields) {
            customerRanking.forEach(
                    item -> {
                        item.setGrossProfit(null);
                        item.setNetProfit(null);
                    });
            productRanking.forEach(
                    item -> {
                        item.setGrossProfit(null);
                        item.setNetProfit(null);
                    });
        }
        return WorkspaceConvert.INSTANCE.toMerchantReportResponse(
                overview, customerRanking, productRanking);
    }

    private Long requireMerchantId(String username) {
        Long merchantId = memberWorkspaceMapper.selectMerchantIdByUsername(username);
        if (merchantId == null) {
            throw new BusinessException(403, "merchant account is unavailable");
        }
        return merchantId;
    }

    private MerchantReportDateRange normalizeMerchantReportDateRange(
            String startDate, String endDate) {
        LocalDate start = parseDate(startDate, "startDate");
        LocalDate end = parseDate(endDate, "endDate");
        if (start != null && end != null && start.isAfter(end)) {
            throw new BusinessException(400, "startDate cannot be later than endDate");
        }
        return new MerchantReportDateRange(
                start == null ? null : start.atStartOfDay(),
                end == null ? null : end.plusDays(1).atStartOfDay());
    }

    private LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new BusinessException(400, fieldName + " must use yyyy-MM-dd");
        }
    }

    private String buildCsv(List<String> headers, List<?> rows) {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", headers));
        for (Object row : rows) {
            List<String> values = new ArrayList<>();
            for (String header : headers) {
                Object value = getProperty(row, header);
                String cell = value == null ? "" : String.valueOf(value);
                values.add(escapeCsv(cell));
            }
            lines.add(String.join(",", values));
        }
        return "\uFEFF" + String.join("\r\n", lines);
    }

    private Object getProperty(Object target, String propertyName) {
        if (target == null || propertyName == null || propertyName.isBlank()) {
            return null;
        }
        String methodName =
                "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private String escapeCsv(String value) {
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")
                || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private BigDecimal toDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private record MerchantReportDateRange(LocalDateTime startTime, LocalDateTime endTime) {}

    @Override
    public String getCustomerLevelByUsername(String username) {
        CustomerContextRow context =
                memberWorkspaceMapper.selectCustomerContextByUsername(username);
        return context != null ? context.getMemberLevel() : null;
    }
}
