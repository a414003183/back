package com.telecom.scm.member.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.member.convert.SupplierConvert;
import com.telecom.scm.member.dto.request.SaveSupplierProductRequest;
import com.telecom.scm.member.dto.response.SupplierProductOptionsResponse;
import com.telecom.scm.member.entity.ProductSkuEntity;
import com.telecom.scm.member.entity.ProductSpuEntity;
import com.telecom.scm.member.mapper.SupplierProductMapper;
import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplierOwnedProductRow;
import com.telecom.scm.member.mapper.row.SupplierProductRow;

@Service
public class SupplierProductServiceImpl implements SupplierProductService {

    private static final DateTimeFormatter CODE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicInteger CODE_SEQUENCE = new AtomicInteger(0);

    private final SupplierProductMapper supplierProductMapper;

    public SupplierProductServiceImpl(SupplierProductMapper supplierProductMapper) {
        this.supplierProductMapper = supplierProductMapper;
    }

    @Override
    public PageResult<SupplierProductRow> products(String username, int page, int pageSize) {
        SupplierContextRow context = requireSupplierContext(username);
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        Long supplierId = context.getSupplierId();
        long total = supplierProductMapper.countSupplierProducts(supplierId);
        List<SupplierProductRow> rows =
                supplierProductMapper.selectSupplierProducts(supplierId, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public SupplierProductOptionsResponse options() {
        return SupplierConvert.INSTANCE.toSupplierProductOptionsResponse(
                supplierProductMapper.selectBrandOptions(),
                supplierProductMapper.selectCategoryOptions());
    }

    @Transactional
    @Override
    public SupplierProductRow createProduct(String username, SaveSupplierProductRequest request) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        Long operatorId = context.getUserId();

        ProductSpuEntity spuParam = new ProductSpuEntity();
        spuParam.setSupplierId(supplierId);
        spuParam.setBrandId(request.brandId());
        spuParam.setCategoryId(request.categoryId());
        spuParam.setSpuCode(buildCode("SPU"));
        spuParam.setSpuName(request.spuName().trim());
        spuParam.setMainImageId(request.mainImageId());
        spuParam.setImageIds(request.imageIds() != null ? toJsonArray(request.imageIds()) : null);
        spuParam.setKeywords(normalizeText(request.keywords()));
        spuParam.setDetailContent(normalizeText(request.detailContent()));
        spuParam.setDescription(normalizeText(request.description()));
        spuParam.setOperatorId(operatorId);
        supplierProductMapper.insertSpu(spuParam);

        ProductSkuEntity skuParam = new ProductSkuEntity();
        skuParam.setSpuId(spuParam.getId());
        skuParam.setSkuCode(buildCode("SKU"));
        skuParam.setSkuName(request.skuName().trim());
        skuParam.setSpecText(request.specText().trim());
        skuParam.setBasePrice(request.basePrice());
        skuParam.setStockQty(request.stockQty());
        skuParam.setSafetyStock(request.safetyStock());
        skuParam.setOperatorId(operatorId);
        supplierProductMapper.insertSku(skuParam);

        return supplierProductMapper.selectSupplierProductBySkuId(supplierId, skuParam.getId());
    }

    @Transactional
    @Override
    public SupplierProductRow updateProduct(
            String username, Long skuId, SaveSupplierProductRequest request) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        Long operatorId = context.getUserId();
        SupplierOwnedProductRow ownedProduct =
                supplierProductMapper.selectSupplierOwnedProduct(supplierId, skuId);
        if (ownedProduct == null || ownedProduct.getSpuId() == null) {
            throw new BusinessException(404, "supplier product not found");
        }

        ProductSpuEntity spuParam = new ProductSpuEntity();
        spuParam.setSupplierId(supplierId);
        spuParam.setOperatorId(operatorId);
        spuParam.setSpuId(ownedProduct.getSpuId());
        spuParam.setBrandId(request.brandId());
        spuParam.setCategoryId(request.categoryId());
        spuParam.setSpuName(request.spuName().trim());
        spuParam.setMainImageId(request.mainImageId());
        spuParam.setImageIds(request.imageIds() != null ? toJsonArray(request.imageIds()) : null);
        spuParam.setKeywords(normalizeText(request.keywords()));
        spuParam.setDetailContent(normalizeText(request.detailContent()));
        spuParam.setDescription(normalizeText(request.description()));
        supplierProductMapper.updateSpu(spuParam);

        ProductSkuEntity skuParam = new ProductSkuEntity();
        skuParam.setSkuId(skuId);
        skuParam.setSkuName(request.skuName().trim());
        skuParam.setSpecText(request.specText().trim());
        skuParam.setBasePrice(request.basePrice());
        skuParam.setStockQty(request.stockQty());
        skuParam.setSafetyStock(request.safetyStock());
        skuParam.setOperatorId(operatorId);
        supplierProductMapper.updateSku(skuParam);

        return supplierProductMapper.selectSupplierProductBySkuId(supplierId, skuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String username, Long skuId) {
        SupplierContextRow context = requireSupplierContext(username);
        Long supplierId = context.getSupplierId();
        SupplierOwnedProductRow ownedProduct =
                supplierProductMapper.selectSupplierOwnedProduct(supplierId, skuId);
        if (ownedProduct == null || ownedProduct.getSpuId() == null) {
            throw new BusinessException(404, "supplier product not found");
        }
        supplierProductMapper.deleteSku(skuId);
        supplierProductMapper.deleteSpu(ownedProduct.getSpuId());
    }

    private SupplierContextRow requireSupplierContext(String username) {
        SupplierContextRow context =
                supplierProductMapper.selectSupplierContextByUsername(username);
        if (context == null || context.getSupplierId() == null) {
            throw new BusinessException(403, "supplier account is unavailable");
        }
        return context;
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String toJsonArray(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return "["
                + ids.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("")
                + "]";
    }

    private String buildCode(String prefix) {
        int sequence = CODE_SEQUENCE.updateAndGet(current -> (current + 1) % 1000);
        return prefix
                + LocalDateTime.now().format(CODE_FORMATTER)
                + String.format("%03d", sequence);
    }
}
