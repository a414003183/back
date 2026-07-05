package com.telecom.scm.mall.convert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.telecom.scm.mall.dto.response.MallCartItemResponse;
import com.telecom.scm.mall.dto.response.ProductDetailResponse;
import com.telecom.scm.mall.dto.response.ProductSummaryResponse;
import com.telecom.scm.mall.mapper.CartItemRow;
import com.telecom.scm.mall.mapper.ProductRow;
import com.telecom.scm.order.mapper.OrderCreateGoodsRow;

/**
 * Mall 模块领域对象转换器。
 *
 * <p>使用 MapStruct 替换手写构造器，复杂计算字段通过 Context 对象传入。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MallConvert {

    MallConvert INSTANCE = Mappers.getMapper(MallConvert.class);

    /** 商品价格计算上下文。 */
    record ProductPriceContext(
            double currentPrice,
            String currentLevel,
            String currentLevelName,
            Double currentDiscountValue,
            List<Map<String, Object>> levelPrices) {}

    /** 商品行 -> 商品列表响应。 */
    @Mapping(target = "id", source = "row.id")
    @Mapping(target = "merchantId", source = "row.merchantId")
    @Mapping(target = "merchantGoodsId", source = "row.merchantGoodsId")
    @Mapping(target = "name", source = "row.name")
    @Mapping(target = "brand", source = "row.brand")
    @Mapping(target = "category", source = "row.category")
    @Mapping(target = "parentCategory", source = "row.parentCategory")
    @Mapping(target = "rootCategory", source = "row.rootCategory")
    @Mapping(target = "summary", source = "row.summary")
    @Mapping(target = "price", source = "row.price")
    @Mapping(target = "memberPrice", source = "ctx.currentPrice")
    @Mapping(target = "stock", source = "row.stock")
    @Mapping(target = "specs", source = "row.specs")
    @Mapping(target = "leadTime", source = "row.leadTime")
    @Mapping(target = "badge", source = "row.badge")
    @Mapping(target = "mainImageId", source = "row.mainImageId")
    @Mapping(target = "shopName", source = "row.shopName")
    @Mapping(target = "saleCount", source = "row.saleCount")
    @Mapping(target = "currentLevel", source = "ctx.currentLevel")
    @Mapping(target = "currentLevelName", source = "ctx.currentLevelName")
    @Mapping(target = "currentDiscountValue", source = "ctx.currentDiscountValue")
    @Mapping(target = "levelPrices", source = "ctx.levelPrices")
    ProductSummaryResponse toProductSummaryResponse(ProductRow row, ProductPriceContext ctx);

    /** 商品行 -> 商品详情响应。 */
    @Mapping(target = "id", source = "row.id")
    @Mapping(target = "merchantId", source = "row.merchantId")
    @Mapping(target = "merchantGoodsId", source = "row.merchantGoodsId")
    @Mapping(target = "skuId", source = "row.skuId")
    @Mapping(target = "name", source = "row.name")
    @Mapping(target = "brand", source = "row.brand")
    @Mapping(target = "category", source = "row.category")
    @Mapping(target = "parentCategory", source = "row.parentCategory")
    @Mapping(target = "rootCategory", source = "row.rootCategory")
    @Mapping(target = "summary", source = "row.summary")
    @Mapping(target = "price", source = "row.price")
    @Mapping(target = "memberPrice", source = "ctx.currentPrice")
    @Mapping(target = "freightAmount", source = "row.freightAmount")
    @Mapping(target = "stock", source = "row.stock")
    @Mapping(target = "specs", source = "row.specs")
    @Mapping(target = "leadTime", source = "row.leadTime")
    @Mapping(target = "badge", source = "row.badge")
    @Mapping(target = "unit", expression = "java(\"piece\")")
    @Mapping(
            target = "deliveryNote",
            expression = "java(\"Support standard and project delivery\")")
    @Mapping(
            target = "pricingRule",
            expression = "java(\"customer special price > member discount > base sale price\")")
    @Mapping(target = "mainImageId", source = "row.mainImageId")
    @Mapping(target = "imageIds", source = "row.imageIds")
    @Mapping(target = "keywords", source = "row.keywords")
    @Mapping(target = "detailContent", source = "row.detailContent")
    @Mapping(target = "description", source = "row.description")
    @Mapping(target = "shopName", source = "row.shopName")
    @Mapping(target = "saleCount", source = "row.saleCount")
    @Mapping(target = "currentLevel", source = "ctx.currentLevel")
    @Mapping(target = "currentLevelName", source = "ctx.currentLevelName")
    @Mapping(target = "currentDiscountValue", source = "ctx.currentDiscountValue")
    @Mapping(target = "levelPrices", source = "ctx.levelPrices")
    ProductDetailResponse toProductDetailResponse(ProductRow row, ProductPriceContext ctx);

    /** 购物车行 + 商品信息 -> 购物车项响应。 */
    @Mapping(target = "id", expression = "java(String.valueOf(cartRow.getMerchantGoodsId()))")
    @Mapping(target = "merchantGoodsId", source = "cartRow.merchantGoodsId")
    @Mapping(target = "skuId", source = "goods.skuId")
    @Mapping(target = "productName", source = "goods.spuName")
    @Mapping(target = "skuName", source = "goods.skuName")
    @Mapping(target = "specText", source = "goods.specText")
    @Mapping(
            target = "unitPrice",
            source = "goods.salePrice",
            qualifiedByName = "bigDecimalToDouble")
    @Mapping(
            target = "memberPrice",
            source = "goods.memberPrice",
            qualifiedByName = "bigDecimalToDouble")
    @Mapping(
            target = "finalUnitPrice",
            expression = "java(resolveFinalUnitPrice(goods).doubleValue())")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "stockQty", source = "goods.stockQty", qualifiedByName = "integerToInt")
    @Mapping(
            target = "lineAmount",
            expression =
                    "java(resolveFinalUnitPrice(goods).multiply(java.math.BigDecimal.valueOf(quantity)).doubleValue())")
    @Mapping(
            target = "freightAmount",
            source = "goods.freightAmount",
            qualifiedByName = "bigDecimalToDouble")
    @Mapping(target = "badge", expression = "java((String) null)")
    @Mapping(target = "mainImageId", source = "goods.mainImageId")
    MallCartItemResponse toMallCartItemResponse(
            CartItemRow cartRow, OrderCreateGoodsRow goods, int quantity);

    @Named("bigDecimalToDouble")
    default double bigDecimalToDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    @Named("integerToInt")
    default int integerToInt(Integer value) {
        return value == null ? 0 : value;
    }

    default BigDecimal resolveFinalUnitPrice(OrderCreateGoodsRow goods) {
        if (goods == null) {
            return BigDecimal.ZERO;
        }
        if (goods.getCustomerPrice() != null) {
            return goods.getCustomerPrice();
        }
        if (goods.getMemberPrice() != null) {
            return goods.getMemberPrice();
        }
        return goods.getSalePrice() == null ? BigDecimal.ZERO : goods.getSalePrice();
    }
}
