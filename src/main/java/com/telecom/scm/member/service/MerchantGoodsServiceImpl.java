package com.telecom.scm.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AuthorizationStatusEnum;
import com.telecom.scm.common.enums.CooperationStatusEnum;
import com.telecom.scm.common.enums.SaleStatusEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.member.convert.MerchantConvert;
import com.telecom.scm.member.dto.request.SaveMerchantGoodsRequest;
import com.telecom.scm.member.dto.request.SaveMerchantProductRequest;
import com.telecom.scm.member.dto.response.MerchantGoodsOptionsResponse;
import com.telecom.scm.member.entity.MerchantGoodsEntity;
import com.telecom.scm.member.entity.ProductSkuEntity;
import com.telecom.scm.member.entity.ProductSpuEntity;
import com.telecom.scm.member.mapper.MerchantGoodsMapper;
import com.telecom.scm.member.mapper.row.MerchantContextRow;
import com.telecom.scm.member.mapper.row.MerchantGoodOwnershipRow;
import com.telecom.scm.member.mapper.row.MerchantGoodsRow;

@Service
public class MerchantGoodsServiceImpl implements MerchantGoodsService {

    private final MerchantGoodsMapper merchantGoodsMapper;

    public MerchantGoodsServiceImpl(MerchantGoodsMapper merchantGoodsMapper) {
        this.merchantGoodsMapper = merchantGoodsMapper;
    }

    @Override
    public PageResult<MerchantGoodsRow> goods(
            String username, boolean includeCostFields, int page, int pageSize) {
        MerchantContextRow context = requireMerchantContext(username);
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        Long merchantId = context.getMerchantId();
        long total = merchantGoodsMapper.countMerchantGoodsRows(merchantId);
        List<MerchantGoodsRow> rows =
                merchantGoodsMapper.selectMerchantGoodsRows(merchantId, limit, offset);
        if (!includeCostFields) {
            rows.forEach(row -> row.setCostPrice(null));
        }
        return PageResult.of(rows, total, page, limit);
    }

    @Override
    public MerchantGoodsOptionsResponse options() {
        return MerchantConvert.INSTANCE.toMerchantGoodsOptionsResponse(
                merchantGoodsMapper.selectBrandOptions(),
                merchantGoodsMapper.selectCategoryOptions());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MerchantGoodsRow createProduct(String username, SaveMerchantProductRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        Long merchantId = context.getMerchantId();
        Long operatorId = context.getUserId();

        // 1. create spu
        ProductSpuEntity spuParam = new ProductSpuEntity();
        spuParam.setSupplierId(merchantId);
        spuParam.setSpuCode("SPU" + System.currentTimeMillis());
        spuParam.setSpuName(request.spuName());
        spuParam.setBrandId(request.brandId());
        spuParam.setCategoryId(request.categoryId());
        spuParam.setDescription(request.description());
        spuParam.setKeywords(request.keywords());
        spuParam.setDetailContent(request.detailContent());
        spuParam.setMainImageId(request.mainImageId());
        spuParam.setOperatorId(operatorId);
        merchantGoodsMapper.insertDirectSpu(spuParam);
        Long spuId = spuParam.getId();

        // 2. create sku
        ProductSkuEntity skuParam = new ProductSkuEntity();
        skuParam.setSpuId(spuId);
        skuParam.setSkuCode("SKU" + System.currentTimeMillis());
        skuParam.setSkuName(request.skuName());
        skuParam.setSpecText(request.specText());
        skuParam.setStockQty(request.stockQty());
        skuParam.setOperatorId(operatorId);
        merchantGoodsMapper.insertDirectSku(skuParam);
        Long skuId = skuParam.getId();

        // 3. create merchant_goods
        MerchantGoodsEntity goodsParam = new MerchantGoodsEntity();
        goodsParam.setMerchantId(merchantId);
        goodsParam.setSupplierId(null);
        goodsParam.setSkuId(skuId);
        goodsParam.setSpuId(spuId);
        goodsParam.setSalePrice(request.salePrice());
        goodsParam.setFreightAmount(request.freightAmount());
        goodsParam.setSaleStatus(
                request.saleStatus() != null ? request.saleStatus() : SaleStatusEnum.ON);
        goodsParam.setDeliveryMode(normalizeDeliveryMode(request.deliveryMode()));
        goodsParam.setStockQty(request.stockQty());
        goodsParam.setSafetyStock(request.safetyStock());
        goodsParam.setMainImageId(request.mainImageId());
        goodsParam.setImageIds(request.imageIds() != null ? request.imageIds().toString() : null);
        goodsParam.setKeywords(request.keywords());
        goodsParam.setDetailContent(request.detailContent());
        goodsParam.setOperatorId(operatorId);
        merchantGoodsMapper.insertMerchantDirectGoods(goodsParam);
        Long merchantGoodsId = goodsParam.getId();

        return merchantGoodsMapper.selectMerchantGoodById(merchantId, merchantGoodsId);
    }

    @Override
    public MerchantGoodsRow getGood(String username, Long merchantGoodsId) {
        MerchantContextRow context = requireMerchantContext(username);
        Long merchantId = context.getMerchantId();
        MerchantGoodsRow result =
                merchantGoodsMapper.selectMerchantGoodById(merchantId, merchantGoodsId);
        if (result == null || result.getMerchantGoodsId() == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return result;
    }

    @Transactional
    @Override
    public MerchantGoodsRow updateGood(
            String username, Long merchantGoodsId, SaveMerchantGoodsRequest request) {
        MerchantContextRow context = requireMerchantContext(username);
        Long merchantId = context.getMerchantId();
        Long operatorId = context.getUserId();
        MerchantGoodOwnershipRow ownership =
                merchantGoodsMapper.selectMerchantGoodOwnership(merchantId, merchantGoodsId);
        if (ownership == null || ownership.getMerchantGoodsId() == null) {
            throw new BusinessException(404, "商品不存在");
        }

        SaleStatusEnum saleStatus =
                request.saleStatus() != null ? request.saleStatus() : SaleStatusEnum.ON;
        Long supplierId = ownership.getSupplierId();
        if (SaleStatusEnum.ON.equals(saleStatus) && supplierId != null) {
            String cooperationStatus = ownership.getCooperationStatus();
            String authorizationStatus = ownership.getAuthorizationStatus();
            if (!CooperationStatusEnum.ACTIVE.getCode().equals(cooperationStatus)
                    || !AuthorizationStatusEnum.ACTIVE.getCode().equals(authorizationStatus)) {
                throw new BusinessException(400, "供应商合作或授权未激活");
            }
        }

        MerchantGoodsRow currentGood =
                merchantGoodsMapper.selectMerchantGoodById(merchantId, merchantGoodsId);
        String sourceType = ownership.getSourceType();
        boolean isDirect = "DIRECT".equals(sourceType) || supplierId == null;

        if (isDirect) {
            MerchantGoodsEntity param = new MerchantGoodsEntity();
            param.setMerchantId(merchantId);
            param.setMerchantGoodsId(merchantGoodsId);
            param.setSalePrice(request.salePrice());
            param.setRebateRate(
                    request.rebateRate() != null
                            ? request.rebateRate()
                            : currentGood.getRebateRate());
            param.setSaleStatus(saleStatus);
            param.setDeliveryMode(
                    normalizeDeliveryMode(
                            request.deliveryMode() != null
                                    ? request.deliveryMode()
                                    : currentGood.getDeliveryMode()));
            // 供应商商品库存由供应商管理，商家不能修改
            param.setStockQty(
                    request.stockQty() != null ? request.stockQty() : currentGood.getStockQty());
            param.setSafetyStock(
                    request.safetyStock() != null
                            ? request.safetyStock()
                            : currentGood.getSafetyStock());
            param.setMainImageId(
                    request.mainImageId() != null
                            ? request.mainImageId()
                            : currentGood.getMainImageId());
            param.setKeywords(
                    request.keywords() != null ? request.keywords() : currentGood.getKeywords());
            param.setDetailContent(
                    request.detailContent() != null
                            ? request.detailContent()
                            : currentGood.getDetailContent());
            param.setDescription(
                    request.description() != null
                            ? request.description()
                            : currentGood.getDescription());
            param.setOperatorId(operatorId);
            merchantGoodsMapper.updateMerchantDirectGoodFull(param);
        } else {
            MerchantGoodsEntity param = new MerchantGoodsEntity();
            param.setMerchantId(merchantId);
            param.setMerchantGoodsId(merchantGoodsId);
            param.setSalePrice(request.salePrice());
            param.setRebateRate(
                    request.rebateRate() != null
                            ? request.rebateRate()
                            : currentGood.getRebateRate());
            param.setSaleStatus(saleStatus);
            param.setDeliveryMode(
                    normalizeDeliveryMode(
                            request.deliveryMode() != null
                                    ? request.deliveryMode()
                                    : currentGood.getDeliveryMode()));
            param.setStockQty(currentGood.getStockQty());
            param.setSafetyStock(currentGood.getSafetyStock());
            param.setOperatorId(operatorId);
            merchantGoodsMapper.updateMerchantGood(param);
        }
        return merchantGoodsMapper.selectMerchantGoodById(merchantId, merchantGoodsId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGood(String username, Long merchantGoodsId) {
        MerchantContextRow context = requireMerchantContext(username);
        Long merchantId = context.getMerchantId();
        MerchantGoodOwnershipRow ownership =
                merchantGoodsMapper.selectMerchantGoodOwnership(merchantId, merchantGoodsId);
        if (ownership == null || ownership.getMerchantGoodsId() == null) {
            throw new BusinessException(404, "商品不存在");
        }
        merchantGoodsMapper.deleteMerchantGood(merchantGoodsId);
    }

    private MerchantContextRow requireMerchantContext(String username) {
        MerchantContextRow context = merchantGoodsMapper.selectMerchantContextByUsername(username);
        if (context == null || context.getMerchantId() == null) {
            throw new BusinessException(403, "商家账号不可用");
        }
        return context;
    }

    private String normalizeDeliveryMode(String deliveryMode) {
        if (deliveryMode == null || deliveryMode.isBlank()) {
            return "SPOT";
        }
        String normalized = deliveryMode.trim().toUpperCase();
        if (!"SPOT".equals(normalized) && !"PROJECT".equals(normalized)) {
            throw new BusinessException(400, "交付方式必须为 SPOT 或 PROJECT");
        }
        return normalized;
    }
}
