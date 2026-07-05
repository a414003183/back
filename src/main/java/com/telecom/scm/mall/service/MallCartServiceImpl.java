package com.telecom.scm.mall.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.OrderSourceEnum;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.mall.convert.MallConvert;
import com.telecom.scm.mall.dto.request.AddCartItemRequest;
import com.telecom.scm.mall.dto.request.DirectBuyRequest;
import com.telecom.scm.mall.dto.request.MallCheckoutRequest;
import com.telecom.scm.mall.dto.request.UpdateCartItemRequest;
import com.telecom.scm.mall.dto.response.MallCartItemResponse;
import com.telecom.scm.mall.mapper.CartItemRow;
import com.telecom.scm.mall.mapper.MallCartMapper;
import com.telecom.scm.order.dto.request.CreateOrderItemRequest;
import com.telecom.scm.order.dto.request.CreateOrderRequest;
import com.telecom.scm.order.dto.response.OrderCreateResponse;
import com.telecom.scm.order.mapper.OrderCreateContextRow;
import com.telecom.scm.order.mapper.OrderCreateGoodsRow;
import com.telecom.scm.order.mapper.OrderWriteMapper;
import com.telecom.scm.order.service.OrderCommandService;

@Service
public class MallCartServiceImpl implements MallCartService {

    private static final Logger log = LoggerFactory.getLogger(MallCartServiceImpl.class);

    private final MallCartMapper mallCartMapper;
    private final OrderWriteMapper orderWriteMapper;
    private final OrderCommandService orderCommandService;

    public MallCartServiceImpl(
            MallCartMapper mallCartMapper,
            OrderWriteMapper orderWriteMapper,
            OrderCommandService orderCommandService) {
        this.mallCartMapper = mallCartMapper;
        this.orderWriteMapper = orderWriteMapper;
        this.orderCommandService = orderCommandService;
    }

    @Override
    public PageResult<MallCartItemResponse> currentCart(String username, int page, int pageSize) {
        pageSize = Math.min(pageSize, 200);
        int offset = (page - 1) * pageSize;
        OrderCreateContextRow context = requireContext(username);
        long total = mallCartMapper.countCartItems(context.getCustomerId());
        if (total == 0) {
            return PageResult.empty(page, pageSize);
        }
        List<CartItemRow> cartRows =
                mallCartMapper.selectCartItems(context.getCustomerId(), offset, pageSize);
        List<MallCartItemResponse> list = hydrateCartRows(context, cartRows, true);
        return PageResult.of(list, total, page, pageSize);
    }

    private List<MallCartItemResponse> fetchCartItems(String username) {
        OrderCreateContextRow context = requireContext(username);
        List<CartItemRow> cartRows =
                mallCartMapper.selectCartItems(context.getCustomerId(), 0, Integer.MAX_VALUE);
        return hydrateCartRows(context, cartRows, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MallCartItemResponse> addCartItem(String username, AddCartItemRequest request) {
        OrderCreateContextRow context = requireContext(username);
        OrderCreateGoodsRow goods = requireGoods(context, request.merchantGoodsId());
        if (goods.getStockQty() == null || goods.getStockQty() < request.quantity()) {
            throw new BusinessException(400, "insufficient stock for selected goods");
        }
        mallCartMapper.upsertCartItem(
                context.getCustomerId(),
                goods.getMerchantGoodsId(),
                goods.getSkuId(),
                request.quantity());
        return fetchCartItems(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MallCartItemResponse> updateCartItem(
            String username, Long merchantGoodsId, UpdateCartItemRequest request) {
        OrderCreateContextRow context = requireContext(username);
        OrderCreateGoodsRow goods = requireGoods(context, merchantGoodsId);
        if (goods.getStockQty() == null || goods.getStockQty() < request.quantity()) {
            throw new BusinessException(400, "insufficient stock for selected goods");
        }
        int affected =
                mallCartMapper.updateCartQuantity(
                        context.getCustomerId(), merchantGoodsId, request.quantity());
        if (affected == 0) {
            throw new BusinessException(404, "cart item not found");
        }
        return fetchCartItems(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCartItem(String username, Long merchantGoodsId) {
        OrderCreateContextRow context = requireContext(username);
        mallCartMapper.deleteCartItem(context.getCustomerId(), merchantGoodsId);
    }

    @Override
    public OrderCreateResponse checkout(String username, MallCheckoutRequest request) {
        OrderCreateContextRow context = requireContext(username);
        List<MallCartItemResponse> cartItems = fetchCartItems(username);
        if (cartItems.isEmpty()) {
            throw new BusinessException(400, "cart is empty");
        }
        List<MallCartItemResponse> selectedItems = cartItems;
        List<Long> selectedIds = request.selectedMerchantGoodsIds();
        if (selectedIds != null && !selectedIds.isEmpty()) {
            selectedItems =
                    cartItems.stream()
                            .filter(item -> selectedIds.contains(item.merchantGoodsId()))
                            .toList();
        }
        if (selectedItems.isEmpty()) {
            throw new BusinessException(400, "no selected items to checkout");
        }
        CreateOrderRequest createOrderRequest =
                new CreateOrderRequest(
                        request.receiverName(),
                        request.receiverPhone(),
                        request.receiverProvince(),
                        request.receiverCity(),
                        request.receiverDistrict(),
                        request.receiverAddress(),
                        request.payMethod(),
                        request.customerRemark(),
                        request.contractFileId(),
                        request.usePoints(),
                        selectedItems.stream()
                                .map(
                                        item ->
                                                new CreateOrderItemRequest(
                                                        item.merchantGoodsId(),
                                                        item.skuId(),
                                                        item.quantity()))
                                .toList());
        OrderCreateResponse response =
                orderCommandService.createCustomerOrder(
                        username, createOrderRequest, OrderSourceEnum.WEB_MALL.getCode());
        for (MallCartItemResponse item : selectedItems) {
            mallCartMapper.deleteCartItem(context.getCustomerId(), item.merchantGoodsId());
        }
        return response;
    }

    @Override
    public OrderCreateResponse directBuy(String username, DirectBuyRequest request) {
        OrderCreateContextRow context = requireContext(username);
        OrderCreateGoodsRow goods = requireGoods(context, request.merchantGoodsId());
        if (goods.getStockQty() == null || goods.getStockQty() < request.quantity()) {
            throw new BusinessException(400, "insufficient stock for selected goods");
        }
        CreateOrderRequest createOrderRequest =
                new CreateOrderRequest(
                        request.receiverName(),
                        request.receiverPhone(),
                        request.receiverProvince(),
                        request.receiverCity(),
                        request.receiverDistrict(),
                        request.receiverAddress(),
                        request.payMethod(),
                        request.customerRemark(),
                        request.contractFileId(),
                        request.usePoints(),
                        List.of(
                                new CreateOrderItemRequest(
                                        goods.getMerchantGoodsId(),
                                        goods.getSkuId(),
                                        request.quantity())));
        return orderCommandService.createCustomerOrder(
                username, createOrderRequest, OrderSourceEnum.WEB_MALL.getCode());
    }

    private OrderCreateContextRow requireContext(String username) {
        OrderCreateContextRow context = orderWriteMapper.selectCustomerContextByUsername(username);
        if (context == null) {
            throw new BusinessException(403, "customer account is unavailable");
        }
        return context;
    }

    private OrderCreateGoodsRow requireGoods(OrderCreateContextRow context, Long merchantGoodsId) {
        List<OrderCreateGoodsRow> goodsList =
                orderWriteMapper.selectGoodsForCreate(
                        List.of(merchantGoodsId),
                        context.getCustomerId(),
                        context.getMemberLevel());
        if (goodsList == null || goodsList.isEmpty()) {
            throw new BusinessException(404, "goods not found");
        }
        return goodsList.get(0);
    }

    private List<MallCartItemResponse> hydrateCartRows(
            OrderCreateContextRow context, List<CartItemRow> cartRows, boolean autoRepair) {
        if (cartRows == null || cartRows.isEmpty()) {
            return List.of();
        }
        List<Long> merchantGoodsIds =
                cartRows.stream().map(CartItemRow::getMerchantGoodsId).distinct().toList();
        List<OrderCreateGoodsRow> goodsList =
                orderWriteMapper.selectGoodsForCreate(
                        merchantGoodsIds, context.getCustomerId(), context.getMemberLevel());
        Map<Long, OrderCreateGoodsRow> goodsMap = new LinkedHashMap<>();
        for (OrderCreateGoodsRow goods : goodsList) {
            goodsMap.put(goods.getMerchantGoodsId(), goods);
        }

        return cartRows.stream()
                .map(
                        cartRow -> {
                            OrderCreateGoodsRow goods = goodsMap.get(cartRow.getMerchantGoodsId());
                            if (goods == null) {
                                if (autoRepair) {
                                    mallCartMapper.deleteCartItem(
                                            context.getCustomerId(), cartRow.getMerchantGoodsId());
                                }
                                return (MallCartItemResponse) null;
                            }
                            int availableStock =
                                    goods.getStockQty() == null ? 0 : goods.getStockQty();
                            int quantity =
                                    cartRow.getQuantity() == null ? 0 : cartRow.getQuantity();
                            if (autoRepair && quantity > availableStock) {
                                if (availableStock <= 0) {
                                    mallCartMapper.deleteCartItem(
                                            context.getCustomerId(), cartRow.getMerchantGoodsId());
                                    return (MallCartItemResponse) null;
                                }
                                mallCartMapper.updateCartQuantity(
                                        context.getCustomerId(),
                                        cartRow.getMerchantGoodsId(),
                                        availableStock);
                                quantity = availableStock;
                            }
                            return MallConvert.INSTANCE.toMallCartItemResponse(
                                    cartRow, goods, quantity);
                        })
                .filter(item -> item != null)
                .toList();
    }

    private BigDecimal resolveFinalUnitPrice(OrderCreateGoodsRow goods) {
        if (goods.getCustomerPrice() != null) {
            return goods.getCustomerPrice();
        }
        if (goods.getMemberPrice() != null) {
            return goods.getMemberPrice();
        }
        return goods.getSalePrice() == null ? BigDecimal.ZERO : goods.getSalePrice();
    }
}
