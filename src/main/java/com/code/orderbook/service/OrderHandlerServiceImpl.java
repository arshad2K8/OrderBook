package com.code.orderbook.service;

import com.code.orderbook.domain.Order;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Normally Order Handler Service should Instantiate OrderBook's for each ticker
// for now I am assuming there is one OrderBook
@Service
public class OrderHandlerServiceImpl implements OrderHandlerService {

    Map<String, OrderBook> orderBookMap = new ConcurrentHashMap<>();

    @Override
    public OrderBook addOrder(String symbol, Order order) {
        OrderBook orderBook = orderBookMap.computeIfAbsent(symbol, OrderBookImpl::new);
        orderBook.addOrder(order);
        return orderBook;
    }

    @Override
    public OrderBook removeOrder(String symbol, long orderId) {
        OrderBook orderBook = orderBookMap.get(symbol);
        orderBook.removeOrder(orderId);
        return orderBook;
    }

    @Override
    public OrderBook modifyOrder(String symbol, long orderId, long newSize) {
        OrderBook orderBook = orderBookMap.get(symbol);
        orderBook.modifyOrder(orderId, newSize);
        return orderBook;
    }

    @Override
    public double getPriceForSideAndLevel(String symbol, char side, int level) {
        OrderBook orderBook = orderBookMap.get(symbol);
        return orderBook != null ? orderBook.getPriceForSideAndLevel(side, level) : 0;
    }

    @Override
    public long getTotalSizeForLevel(String symbol, char side, int level) {
        OrderBook orderBook = orderBookMap.get(symbol);
        return orderBook != null ? orderBook.getTotalSizeForLevel(side, level) : 0;
    }

    @Override
    public List<Order> getAllOrdersForSide(String symbol, char side) {
        OrderBook orderBook = orderBookMap.get(symbol);
        return orderBook != null ? orderBook.getAllOrdersForSide(side) : Collections.emptyList();
    }
}
