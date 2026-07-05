package com.telecom.scm.order.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.file.service.FileStorageService;
import com.telecom.scm.order.convert.OrderDocumentConvert;
import com.telecom.scm.order.dto.response.OrderContractResponse;
import com.telecom.scm.order.mapper.OrderDocumentMapper;
import com.telecom.scm.order.mapper.OrderQuoteItemRow;
import com.telecom.scm.order.mapper.OrderQuoteSummaryRow;

@Service
public class OrderDocumentServiceImpl implements OrderDocumentService {

    private final OrderDocumentMapper orderDocumentMapper;
    private final FileStorageService fileStorageService;

    public OrderDocumentServiceImpl(
            OrderDocumentMapper orderDocumentMapper, FileStorageService fileStorageService) {
        this.orderDocumentMapper = orderDocumentMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> exportCustomerQuote(String username, Long orderId) {
        OrderQuoteSummaryRow summary =
                orderDocumentMapper.selectCustomerQuoteSummary(username, orderId);
        return buildQuoteResponse(summary, orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> exportMerchantQuote(String username, Long orderId) {
        OrderQuoteSummaryRow summary =
                orderDocumentMapper.selectMerchantQuoteSummary(username, orderId);
        return buildQuoteResponse(summary, orderId);
    }

    @Override
    public List<OrderContractResponse> customerOrderContracts(String username, Long orderId) {
        requireCustomerAccess(username, orderId);
        return buildContractRows(orderId);
    }

    @Override
    public OrderContractResponse bindCustomerOrderContract(
            String username, Long orderId, Long fileId) {
        requireCustomerAccess(username, orderId);
        Long existingFileId = orderDocumentMapper.selectOrderContractFileId(orderId, fileId);
        if (existingFileId == null) {
            fileStorageService.bindFileIfPresent(
                    fileId, "ORDER_CONTRACT", orderId, fileStorageService.requireUserId(username));
        }
        return buildContractRows(orderId).stream()
                .filter(item -> String.valueOf(fileId).equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(404, "order contract file not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> downloadCustomerOrderContract(
            String username, Long orderId, Long fileId) {
        requireCustomerAccess(username, orderId);
        return downloadContract(orderId, fileId);
    }

    @Override
    public List<OrderContractResponse> merchantOrderContracts(String username, Long orderId) {
        requireMerchantAccess(username, orderId);
        return buildContractRows(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> downloadMerchantOrderContract(
            String username, Long orderId, Long fileId) {
        requireMerchantAccess(username, orderId);
        return downloadContract(orderId, fileId);
    }

    private ResponseEntity<byte[]> buildQuoteResponse(OrderQuoteSummaryRow summary, Long orderId) {
        if (summary == null) {
            throw new BusinessException(404, "order quote not found");
        }

        List<OrderQuoteItemRow> items = orderDocumentMapper.selectQuoteItems(orderId);
        if (items.isEmpty()) {
            throw new BusinessException(404, "order quote items not found");
        }

        String orderNo = summary.getOrderNo();
        byte[] content = buildQuoteCsv(summary, items).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("quote-" + orderNo + ".csv")
                                .build()
                                .toString())
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(content);
    }

    private String buildQuoteCsv(OrderQuoteSummaryRow summary, List<OrderQuoteItemRow> items) {
        List<String> lines = new ArrayList<>();
        lines.add("报价单,通讯设备供应链管理系统");
        lines.add("订单号," + escapeCsv(summary.getOrderNo()));
        lines.add("客户名称," + escapeCsv(summary.getCustomerName()));
        lines.add("商家名称," + escapeCsv(summary.getMerchantName()));
        lines.add("收货人," + escapeCsv(summary.getReceiverName()));
        lines.add("联系电话," + escapeCsv(summary.getReceiverPhone()));
        lines.add("收货地址," + escapeCsv(summary.getReceiverAddress()));
        lines.add("下单时间," + escapeCsv(summary.getCreatedAt()));
        lines.add("");
        lines.add("序号,SPU,SKU,规格,数量,单价,金额");

        for (int index = 0; index < items.size(); index++) {
            OrderQuoteItemRow item = items.get(index);
            lines.add(
                    String.join(
                            ",",
                            String.valueOf(index + 1),
                            escapeCsv(item.getSpuName()),
                            escapeCsv(item.getSkuName()),
                            escapeCsv(item.getSpecText()),
                            escapeCsv(item.getQuantity()),
                            escapeCsv(item.getFinalUnitPrice()),
                            escapeCsv(item.getFinalAmount())));
        }

        lines.add("");
        lines.add("商品金额," + escapeCsv(summary.getGoodsAmount()));
        lines.add("运费," + escapeCsv(summary.getFreightAmount()));
        lines.add("应付金额," + escapeCsv(summary.getPayAmount()));
        return "\uFEFF" + String.join("\r\n", lines);
    }

    private List<OrderContractResponse> buildContractRows(Long orderId) {
        return OrderDocumentConvert.INSTANCE.toOrderContractResponseList(
                orderDocumentMapper.selectOrderContractRows(orderId));
    }

    private ResponseEntity<byte[]> downloadContract(Long orderId, Long fileId) {
        Long contractFileId = orderDocumentMapper.selectOrderContractFileId(orderId, fileId);
        if (contractFileId == null) {
            throw new BusinessException(404, "order contract file not found");
        }

        FileStorageService.DownloadPayload payload = fileStorageService.loadFile(fileId);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(payload.originalName())
                                .build()
                                .toString())
                .contentType(MediaType.parseMediaType(payload.contentType()))
                .body(payload.content());
    }

    private void requireCustomerAccess(String username, Long orderId) {
        if (orderDocumentMapper.selectCustomerOrderAccess(username, orderId) == null) {
            throw new BusinessException(404, "order not found");
        }
    }

    private void requireMerchantAccess(String username, Long orderId) {
        if (orderDocumentMapper.selectMerchantOrderAccess(username, orderId) == null) {
            throw new BusinessException(404, "order not found");
        }
    }

    private String escapeCsv(Object value) {
        String cell = value == null ? "" : String.valueOf(value);
        String escaped = cell.replace("\"", "\"\"");
        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")
                || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
