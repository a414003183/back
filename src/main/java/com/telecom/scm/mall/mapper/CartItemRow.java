package com.telecom.scm.mall.mapper;

public class CartItemRow {

    private Long merchantGoodsId;
    private Long skuId;
    private Integer quantity;

    public Long getMerchantGoodsId() {
        return merchantGoodsId;
    }

    public void setMerchantGoodsId(Long merchantGoodsId) {
        this.merchantGoodsId = merchantGoodsId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
