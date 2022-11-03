package com.code.orderbook.service;

import com.code.orderbook.domain.Order;
import com.code.orderbook.domain.OrderOperationResult;

import java.util.List;

public interface OrderBook {
    OrderOperationResult addOrder(Order order);
    OrderOperationResult removeOrder(long orderId );
    OrderOperationResult modifyOrder(long orderId, long newSize);
    double getPriceForSideAndLevel(char side, int level);
    long getTotalSizeForLevel(char side, int level);
    List<Order> getAllOrdersForSide(char side);
}
