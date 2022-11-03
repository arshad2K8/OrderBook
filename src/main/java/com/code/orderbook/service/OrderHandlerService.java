package com.code.orderbook.service;

import com.code.orderbook.domain.Order;

import java.util.List;

public interface OrderHandlerService {
    OrderBook addOrder(String symbol, Order order);
    OrderBook removeOrder(String symbol, long orderId );
    OrderBook modifyOrder(String symbol, long orderId, long newSize);
    double getPriceForSideAndLevel(String symbol, char side, int level);
    long getTotalSizeForLevel(String symbol, char side, int level);
    List<Order> getAllOrdersForSide(String symbol, char side);
}
