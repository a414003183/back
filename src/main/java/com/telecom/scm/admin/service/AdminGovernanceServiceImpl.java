package com.telecom.scm.admin.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.telecom.scm.admin.dto.response.ImportDataResponse;
import com.telecom.scm.admin.dto.response.ImportExportOverviewResponse;
import com.telecom.scm.admin.dto.response.MessageResponse;
import com.telecom.scm.admin.mapper.AdminGovernanceMapper;
import com.telecom.scm.admin.mapper.ImportExportLogRow;
import com.telecom.scm.admin.mapper.LoginLogRow;
import com.telecom.scm.admin.mapper.MenuRow;
import com.telecom.scm.admin.mapper.OperationLogRow;
import com.telecom.scm.admin.mapper.RoleRow;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.enums.SaleStatusEnum;
import com.telecom.scm.common.exception.BusinessException;

@Service
public class AdminGovernanceServiceImpl implements AdminGovernanceService {

    private static final Map<String, ImportSchema> IMPORT_SCHEMAS =
            Map.of(
                    "users",
                            new ImportSchema(
                                    List.of(
                                            "id",
                                            "username",
                                            "userType",
                                            "phone",
                                            "email",
                                            "status"),
                                    Set.of("id", "username")),
                    "customers",
                            new ImportSchema(
                                    List.of(
                                            "id",
                                            "companyName",
                                            "memberLevel",
                                            "contactName",
                                            "contactPhone",
                                            "inviteCode",
                                            "status"),
                                    Set.of("id", "companyName", "contactName", "contactPhone")),
                    "suppliers",
                            new ImportSchema(
                                    List.of(
                                            "id",
                                            "supplierName",
                                            "contactName",
                                            "contactPhone",
                                            "status"),
                                    Set.of("id", "supplierName", "contactName", "contactPhone")),
                    "products",
                            new ImportSchema(
                                    List.of(
                                            "id",
                                            "spuName",
                                            "skuName",
                                            "specText",
                                            "basePrice",
                                            "costPrice",
                                            "stockQty",
                                            "saleStatus"),
                                    Set.of(
                                            "id",
                                            "spuName",
                                            "skuName",
                                            "basePrice",
                                            "costPrice",
                                            "stockQty")));

    private static final Map<String, Map<String, String>> HEADER_TRANSLATIONS =
            Map.of(
                    "users",
                            Map.of(
                                    "id", "ID",
                                    "username", "用户名",
                                    "userType", "用户类型",
                                    "phone", "手机号",
                                    "email", "邮箱",
                                    "status", "状态"),
                    "customers",
                            Map.of(
                                    "id", "ID",
                                    "companyName", "公司名称",
                                    "memberLevel", "会员等级",
                                    "contactName", "联系人姓名",
                                    "contactPhone", "联系人手机号",
                                    "inviteCode", "邀请码",
                                    "status", "状态"),
                    "suppliers",
                            Map.of(
                                    "id", "ID",
                                    "supplierName", "供应商名称",
                                    "contactName", "联系人姓名",
                                    "contactPhone", "联系人手机号",
                                    "status", "状态"),
                    "products",
                            Map.of(
                                    "id", "ID",
                                    "spuName", "SPU名称",
                                    "skuName", "SKU名称",
                                    "specText", "规格",
                                    "basePrice", "基础价格",
                                    "costPrice", "成本价格",
                                    "stockQty", "库存数量",
                                    "saleStatus", "销售状态"));

    private final AdminGovernanceMapper adminGovernanceMapper;

    public AdminGovernanceServiceImpl(AdminGovernanceMapper adminGovernanceMapper) {
        this.adminGovernanceMapper = adminGovernanceMapper;
    }

    @Override
    public PageResult<RoleRow> roles(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<RoleRow> list = adminGovernanceMapper.selectRoleRows(offset, pageSize);
        long total = adminGovernanceMapper.countRoleRows();
        return PageResult.of(list, total, page, pageSize);
    }

    @Transactional
    @Override
    public MessageResponse updateRoleStatus(Long roleId, String status) {
        String normalized = normalizeStatus(status);
        adminGovernanceMapper.updateRoleStatus(roleId, normalized);
        return MessageResponse.of("role status updated");
    }

    @Override
    public PageResult<MenuRow> menus(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<MenuRow> list = adminGovernanceMapper.selectMenuRows(offset, pageSize);
        long total = adminGovernanceMapper.countMenuRows();
        return PageResult.of(list, total, page, pageSize);
    }

    @Transactional
    @Override
    public MessageResponse updateMenuStatus(Long menuId, String status) {
        String normalized = normalizeStatus(status);
        adminGovernanceMapper.updateMenuStatus(menuId, normalized);
        return MessageResponse.of("menu status updated");
    }

    @Override
    public List<String> roleMenuIds(Long roleId) {
        return adminGovernanceMapper.selectRoleMenuIds(roleId);
    }

    @Transactional
    @Override
    public List<String> assignRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
            throw new BusinessException(400, "roleId is required");
        }
        adminGovernanceMapper.deleteRoleMenus(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            adminGovernanceMapper.insertRoleMenus(roleId, menuIds.stream().distinct().toList());
        }
        return roleMenuIds(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult<LoginLogRow> loginLogs(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<LoginLogRow> list = adminGovernanceMapper.selectLoginLogs(offset, pageSize);
        long total = adminGovernanceMapper.countLoginLogs();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public PageResult<OperationLogRow> operationLogs(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<OperationLogRow> list = adminGovernanceMapper.selectOperationLogs(offset, pageSize);
        long total = adminGovernanceMapper.countOperationLogs();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportExportOverviewResponse importExportOverview() {
        return new ImportExportOverviewResponse(
                List.of(
                        new ImportExportOverviewResponse.SupportedType("users", "系统用户"),
                        new ImportExportOverviewResponse.SupportedType("customers", "客户档案"),
                        new ImportExportOverviewResponse.SupportedType("suppliers", "供应商档案"),
                        new ImportExportOverviewResponse.SupportedType("products", "商品档案")));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult<ImportExportLogRow> importExportLogs(String type, int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<ImportExportLogRow> list =
                adminGovernanceMapper.selectImportExportLogsByType(type, offset, pageSize);
        long total = adminGovernanceMapper.countImportExportLogsByType(type);
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportDataResponse importData(String type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "import file is required");
        }

        ImportFile parsedFile = parseImportRows(file);
        ImportFile translatedFile = translateImportFile(type, parsedFile);
        validateImportHeaders(type, translatedFile.headers());
        validateImportRows(type, translatedFile.rows());
        ImportSummary summary =
                switch (type) {
                    case "users" -> importUsers(translatedFile.rows());
                    case "customers" -> importCustomers(translatedFile.rows());
                    case "suppliers" -> importSuppliers(translatedFile.rows());
                    case "products" -> importProducts(translatedFile.rows());
                    default -> throw new BusinessException(400, "unsupported import type");
                };

        return new ImportDataResponse(
                type,
                file.getOriginalFilename(),
                summary.totalRows(),
                summary.successCount(),
                summary.skippedCount(),
                summary.messages());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> exportCsv(String type, boolean templateOnly) {
        CsvPayload payload =
                switch (type) {
                    case "users" ->
                            new CsvPayload(
                                    "users.csv",
                                    List.of(
                                            "id",
                                            "username",
                                            "userType",
                                            "phone",
                                            "email",
                                            "status"),
                                    templateOnly
                                            ? List.of()
                                            : adminGovernanceMapper.selectExportUsers());
                    case "customers" ->
                            new CsvPayload(
                                    "customers.csv",
                                    List.of(
                                            "id",
                                            "companyName",
                                            "memberLevel",
                                            "contactName",
                                            "contactPhone",
                                            "inviteCode",
                                            "status"),
                                    templateOnly
                                            ? List.of()
                                            : adminGovernanceMapper.selectExportCustomers());
                    case "suppliers" ->
                            new CsvPayload(
                                    "suppliers.csv",
                                    List.of(
                                            "id",
                                            "supplierName",
                                            "contactName",
                                            "contactPhone",
                                            "status"),
                                    templateOnly
                                            ? List.of()
                                            : adminGovernanceMapper.selectExportSuppliers());
                    case "products" ->
                            new CsvPayload(
                                    "products.csv",
                                    List.of(
                                            "id",
                                            "spuName",
                                            "skuName",
                                            "specText",
                                            "basePrice",
                                            "costPrice",
                                            "stockQty",
                                            "saleStatus"),
                                    templateOnly
                                            ? List.of()
                                            : adminGovernanceMapper.selectExportProducts());
                    default -> throw new BusinessException(400, "unsupported export type");
                };

        Map<String, String> translation = HEADER_TRANSLATIONS.getOrDefault(type, Map.of());
        List<String> labels =
                payload.keys().stream().map(key -> translation.getOrDefault(key, key)).toList();

        byte[] content =
                buildCsv(payload.keys(), labels, payload.rows()).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(payload.fileName())
                                .build()
                                .toString())
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(content);
    }

    @Override
    public List<String> userMenuIds(Long userId) {
        return adminGovernanceMapper.selectUserMenuIds(userId);
    }

    @Transactional
    @Override
    public List<String> assignUserMenus(Long userId, List<Long> menuIds, Long roleId) {
        if (userId == null) {
            throw new BusinessException(400, "userId is required");
        }
        // 删除用户已有菜单权限
        adminGovernanceMapper.deleteUserMenus(userId);
        // 如果没有传入菜单权限，则继承角色的全部权限
        List<Long> finalMenuIds;
        if (menuIds == null || menuIds.isEmpty()) {
            // 角色菜单是String列表，需要转换为Long列表
            List<String> roleMenuIdStrings = adminGovernanceMapper.selectRoleMenuIds(roleId);
            finalMenuIds = roleMenuIdStrings.stream().map(Long::parseLong).toList();
        } else {
            finalMenuIds = menuIds.stream().distinct().toList();
        }
        if (!finalMenuIds.isEmpty()) {
            adminGovernanceMapper.insertUserMenus(userId, finalMenuIds);
        }
        return userMenuIds(userId);
    }

    private String buildCsv(List<String> keys, List<String> labels, List<?> rows) {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", labels));
        for (Object row : rows) {
            List<String> values = new ArrayList<>();
            for (String key : keys) {
                Object value = readProperty(row, key);
                String cell = value == null ? "" : String.valueOf(value);
                values.add(escapeCsv(cell));
            }
            lines.add(String.join(",", values));
        }
        return "\uFEFF" + String.join("\r\n", lines);
    }

    private Object readProperty(Object bean, String propertyName) {
        String getterName =
                "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        try {
            Method method = bean.getClass().getMethod(getterName);
            return method.invoke(bean);
        } catch (NoSuchMethodException exception) {
            throw new BusinessException(500, "missing getter for property: " + propertyName);
        } catch (ReflectiveOperationException exception) {
            throw new BusinessException(500, "failed to read property: " + propertyName);
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

    private ImportSummary importUsers(List<Map<String, String>> rows) {
        int successCount = 0;
        List<String> messages = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            Map<String, String> row = rows.get(index);
            Long id = parseLong(row.get("id"));
            if (id == null) {
                addMessage(messages, "第" + (index + 2) + "行缺少有效 id，已跳过");
                continue;
            }

            int affected =
                    adminGovernanceMapper.updateImportedUser(
                            id,
                            row.get("username"),
                            row.get("phone"),
                            row.get("email"),
                            normalizeStatus(row.get("status")));
            if (affected > 0) {
                successCount++;
            } else {
                addMessage(messages, "第" + (index + 2) + "行用户不存在，已跳过");
            }
        }
        return new ImportSummary(rows.size(), successCount, rows.size() - successCount, messages);
    }

    private ImportSummary importCustomers(List<Map<String, String>> rows) {
        int successCount = 0;
        List<String> messages = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            Map<String, String> row = rows.get(index);
            Long id = parseLong(row.get("id"));
            if (id == null) {
                addMessage(messages, "第" + (index + 2) + "行缺少有效 id，已跳过");
                continue;
            }

            int affected =
                    adminGovernanceMapper.updateImportedCustomer(
                            id,
                            row.get("companyName"),
                            defaultText(row.get("memberLevel"), "NORMAL"),
                            row.get("contactName"),
                            row.get("contactPhone"),
                            row.get("inviteCode"),
                            normalizeStatus(row.get("status")));
            if (affected > 0) {
                successCount++;
            } else {
                addMessage(messages, "第" + (index + 2) + "行客户档案不存在，已跳过");
            }
        }
        return new ImportSummary(rows.size(), successCount, rows.size() - successCount, messages);
    }

    private ImportSummary importSuppliers(List<Map<String, String>> rows) {
        int successCount = 0;
        List<String> messages = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            Map<String, String> row = rows.get(index);
            Long id = parseLong(row.get("id"));
            if (id == null) {
                addMessage(messages, "第" + (index + 2) + "行缺少有效 id，已跳过");
                continue;
            }

            int affected =
                    adminGovernanceMapper.updateImportedSupplier(
                            id,
                            row.get("supplierName"),
                            row.get("contactName"),
                            row.get("contactPhone"),
                            normalizeStatus(row.get("status")));
            if (affected > 0) {
                successCount++;
            } else {
                addMessage(messages, "第" + (index + 2) + "行供应商档案不存在，已跳过");
            }
        }
        return new ImportSummary(rows.size(), successCount, rows.size() - successCount, messages);
    }

    private ImportSummary importProducts(List<Map<String, String>> rows) {
        int successCount = 0;
        List<String> messages = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            Map<String, String> row = rows.get(index);
            Long id = parseLong(row.get("id"));
            if (id == null) {
                addMessage(messages, "第" + (index + 2) + "行缺少有效 id，已跳过");
                continue;
            }

            try {
                int affected =
                        adminGovernanceMapper.updateImportedProduct(
                                id,
                                row.get("spuName"),
                                row.get("skuName"),
                                row.get("specText"),
                                parseDecimal(row.get("basePrice")),
                                parseDecimal(row.get("costPrice")),
                                parseInteger(row.get("stockQty")),
                                defaultText(row.get("saleStatus"), SaleStatusEnum.ON.getCode()));
                if (affected > 0) {
                    successCount++;
                } else {
                    addMessage(messages, "第" + (index + 2) + "行商品不存在，已跳过");
                }
            } catch (NumberFormatException exception) {
                addMessage(messages, "第" + (index + 2) + "行商品数字字段格式错误，已跳过");
            }
        }
        return new ImportSummary(rows.size(), successCount, rows.size() - successCount, messages);
    }

    private void validateImportHeaders(String type, List<String> headers) {
        ImportSchema schema = IMPORT_SCHEMAS.get(type);
        if (schema == null) {
            throw new BusinessException(400, "unsupported import type");
        }
        if (!schema.headers().equals(headers.stream().map(String::trim).toList())) {
            throw new BusinessException(
                    400, "import headers do not match template for type " + type);
        }
    }

    private void validateImportRows(String type, List<Map<String, String>> rows) {
        ImportSchema schema = IMPORT_SCHEMAS.get(type);
        if (schema == null) {
            throw new BusinessException(400, "unsupported import type");
        }
        for (int index = 0; index < rows.size(); index++) {
            Map<String, String> row = rows.get(index);
            for (String field : schema.requiredFields()) {
                String value = row.get(field);
                if (value == null || value.isBlank()) {
                    throw new BusinessException(
                            400, "row " + (index + 2) + " missing required field: " + field);
                }
            }
        }
    }

    private ImportFile parseImportRows(MultipartFile file) {
        String extension = fileExtension(file.getOriginalFilename());
        return switch (extension) {
            case "csv" -> parseCsvRows(file);
            case "xlsx", "xls" -> parseExcelRows(file);
            default -> throw new BusinessException(400, "only csv/xls/xlsx import is supported");
        };
    }

    private ImportFile parseCsvRows(MultipartFile file) {
        try {
            String content =
                    new String(file.getBytes(), StandardCharsets.UTF_8).replace("\uFEFF", "");
            List<String> lines =
                    Arrays.stream(content.split("\\r?\\n"))
                            .filter(line -> !line.isBlank())
                            .toList();
            if (lines.isEmpty()) {
                return new ImportFile(List.of(), List.of());
            }

            List<String> headers = parseCsvLine(lines.get(0));
            List<Map<String, String>> rows = new ArrayList<>();
            for (int index = 1; index < lines.size(); index++) {
                List<String> values = parseCsvLine(lines.get(index));
                rows.add(buildRow(headers, values));
            }
            return new ImportFile(headers.stream().map(String::trim).toList(), rows);
        } catch (IOException exception) {
            throw new BusinessException(500, "failed to read csv import file");
        }
    }

    private ImportFile parseExcelRows(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                return new ImportFile(List.of(), List.of());
            }

            DataFormatter formatter = new DataFormatter();
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                return new ImportFile(List.of(), List.of());
            }

            List<String> headers = new ArrayList<>();
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                headers.add(formatter.formatCellValue(headerRow.getCell(cellIndex)).trim());
            }

            List<Map<String, String>> rows = new ArrayList<>();
            for (int rowIndex = sheet.getFirstRowNum() + 1;
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                List<String> values = new ArrayList<>();
                boolean hasValue = false;
                for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
                    String value = formatter.formatCellValue(row.getCell(cellIndex)).trim();
                    if (!value.isBlank()) {
                        hasValue = true;
                    }
                    values.add(value);
                }
                if (hasValue) {
                    rows.add(buildRow(headers, values));
                }
            }
            return new ImportFile(headers.stream().map(String::trim).toList(), rows);
        } catch (Exception exception) {
            throw new BusinessException(400, "failed to parse excel import file");
        }
    }

    private Map<String, String> buildRow(List<String> headers, List<String> values) {
        Map<String, String> row = new LinkedHashMap<>();
        for (int index = 0; index < headers.size(); index++) {
            String header = headers.get(index);
            if (header == null || header.isBlank()) {
                continue;
            }
            row.put(header.trim(), index < values.size() ? values.get(index).trim() : "");
        }
        return row;
    }

    private List<String> parseCsvLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            char currentChar = line.charAt(index);
            if (currentChar == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
                continue;
            }
            if (currentChar == ',' && !quoted) {
                cells.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(currentChar);
        }
        cells.add(current.toString());
        return cells;
    }

    private String fileExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.valueOf(value.trim());
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.valueOf(value.trim());
    }

    private java.math.BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) {
            return java.math.BigDecimal.ZERO;
        }
        return new java.math.BigDecimal(value.trim());
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return AccountStatusEnum.ENABLED.getCode();
        }
        return status.trim().toUpperCase();
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private void addMessage(List<String> messages, String message) {
        if (messages.size() < 20) {
            messages.add(message);
        }
    }

    private record CsvPayload(String fileName, List<String> keys, List<?> rows) {}

    private ImportFile translateImportFile(String type, ImportFile file) {
        Map<String, String> translation = HEADER_TRANSLATIONS.get(type);
        if (translation == null) {
            return file;
        }

        Map<String, String> reverseTranslation = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : translation.entrySet()) {
            reverseTranslation.put(entry.getValue(), entry.getKey());
        }

        List<String> translatedHeaders = new ArrayList<>();
        for (String header : file.headers()) {
            translatedHeaders.add(reverseTranslation.getOrDefault(header, header));
        }

        List<Map<String, String>> translatedRows = new ArrayList<>();
        for (Map<String, String> row : file.rows()) {
            Map<String, String> translatedRow = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String translatedKey =
                        reverseTranslation.getOrDefault(entry.getKey(), entry.getKey());
                translatedRow.put(translatedKey, entry.getValue());
            }
            translatedRows.add(translatedRow);
        }

        return new ImportFile(translatedHeaders, translatedRows);
    }

    private record ImportSummary(
            int totalRows, int successCount, int skippedCount, List<String> messages) {}

    private record ImportSchema(List<String> headers, Set<String> requiredFields) {}

    private record ImportFile(List<String> headers, List<Map<String, String>> rows) {}
}
