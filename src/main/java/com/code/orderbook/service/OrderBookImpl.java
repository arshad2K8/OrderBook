package com.code.orderbook.service;

import com.code.orderbook.domain.Order;
import com.code.orderbook.domain.OrderOperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Normally Order Handler Service should Instantiate OrderBook's for each ticker
// for now I am assuming there is one OrderBook
@Slf4j
//@Service
public class OrderBookImpl implements OrderBook {

    private String symbol;
    // Maps use Synchronized List as value to hold multiple Orders with same price
    private Map<Long, Double> orderIdToPriceMap;

    private Map<Double, List<Order>> bids;
    private Map<Double, List<Order>> offers;
    private TreeSet<Double> bidsPriceQueue;
    private TreeSet<Double> offersPriceQueue;

    public OrderBookImpl(String symbol) {
        this.symbol = symbol;
        this.orderIdToPriceMap = new ConcurrentHashMap<>();
        this.bids = new ConcurrentHashMap<>();
        this.offers = new ConcurrentHashMap<>();
        bidsPriceQueue = new TreeSet<>(Collections.reverseOrder());
        offersPriceQueue = new TreeSet<>();
    }


    @Override
    public OrderOperationResult addOrder(Order order) {
        log.info("Adding new Order {}", order);
        this.orderIdToPriceMap.put(order.getOrderId(), order.getPrice());

        if (order.getSide() == 'B') {
            addBidOrder(order);
        } else if (order.getSide() == 'O') {
            addOfferOrder(order);
        }
        return null;
    }

    private boolean addBidOrder(Order newBidOrder) {
        log.info("Adding BidOrder {}", newBidOrder);
        addOrderToCorrespondingMaps(newBidOrder, this.bids, this.bidsPriceQueue);
        // match orders and Book
        return true;
    }

    private boolean addOfferOrder(Order newOfferOrder) {
        addOrderToCorrespondingMaps(newOfferOrder, this.offers, this.offersPriceQueue);
        // match orders and Book
        return true;
    }

    private boolean addOrderToCorrespondingMaps(Order newOrder, Map<Double, List<Order>> bidsOrOffersMap, TreeSet<Double> bidsOrOffersPriceQueue) {
        log.info("Adding Order to Corresponding Data Structures {}", newOrder);
        List<Order> listOfOrdersForAPrice = bidsOrOffersMap.computeIfAbsent(newOrder.getPrice(), k -> Collections.synchronizedList(new LinkedList<>()));
        listOfOrdersForAPrice.add(newOrder);
        bidsOrOffersPriceQueue.add(newOrder.getPrice());
        return true;
    }

    @Override
    public OrderOperationResult removeOrder(long orderId) {
        double toBeRemovedOrderPrice = this.orderIdToPriceMap.remove(orderId);

        // update bids/offers maps
        boolean s1 = this.bids.get(toBeRemovedOrderPrice) != null && this.bids.get(toBeRemovedOrderPrice).removeIf(order -> order.getOrderId() == orderId);
        boolean s2 = this.offers.get(toBeRemovedOrderPrice) != null && this.offers.get(toBeRemovedOrderPrice).removeIf(order -> order.getOrderId() == orderId);

        // update priority Queues too
        if (this.bids.get(toBeRemovedOrderPrice) != null && this.bids.get(toBeRemovedOrderPrice).size() == 0) {
            this.bidsPriceQueue.remove(toBeRemovedOrderPrice);
        }
        if (this.offers.get(toBeRemovedOrderPrice) != null && this.offers.get(toBeRemovedOrderPrice).size() == 0) {
            this.offersPriceQueue.remove(toBeRemovedOrderPrice);
        }

        return OrderOperationResult.builder().orderId(orderId).success(s1 || s2).build();
    }

    @Override
    public OrderOperationResult modifyOrder(long orderId, long newSize) {
        double orderPriceForOrderId = this.orderIdToPriceMap.get(orderId);
        //
        if (this.bids.get(orderPriceForOrderId) != null) {
            this.bids.get(orderPriceForOrderId).forEach(e -> {
                if (e.getOrderId() == orderId) {
                    e.setSize(newSize);
                }
            });
        }

        if (this.offers.get(orderPriceForOrderId) != null) {
            this.offers.get(orderPriceForOrderId).forEach(e -> {
                if (e.getOrderId() == orderId) {
                    e.setSize(newSize);
                }
            });
        }

        return OrderOperationResult.builder().orderId(orderId).success(true).build();
    }

    @Override
    public double getPriceForSideAndLevel(char side, int level) {
        if (side == 'B') {
            return getKthElemFromTreeSet(this.bidsPriceQueue, level);
        } else if (side == 'O') {
            return getKthElemFromTreeSet(this.offersPriceQueue, level);
        }
        return -1;
    }

    private double getKthElemFromTreeSet(TreeSet<Double> pricesSet, int k) {
        Iterator<Double> it = pricesSet.iterator();
        int i = 0;
        Double current = null;
        while(it.hasNext() && i < k) {
            current = it.next();
            i++;
        }
        return current;
    }

    @Override
    public long getTotalSizeForLevel(char side, int level) {
        double bestPriceByLevel;
        if (side == 'B') {
            bestPriceByLevel = getKthElemFromTreeSet(this.bidsPriceQueue, level);
            return this.bids.get(bestPriceByLevel).stream().map(Order::getSize).reduce(Long::sum).orElse(0L);
        } else if (side == 'O') {
            bestPriceByLevel = getKthElemFromTreeSet(this.offersPriceQueue, level);
            return this.offers.get(bestPriceByLevel).stream().map(Order::getSize).reduce(Long::sum).orElse(0L);
        }

        return 0L;
    }

    @Override
    public List<Order> getAllOrdersForSide(char side) {
        List<Order> allOrdersForSide = new LinkedList<>();
        if (side == 'B') {
            this.bidsPriceQueue.forEach(p -> allOrdersForSide.addAll(this.bids.get(p)));
            return allOrdersForSide;
        } else if (side == 'O') {
            this.offersPriceQueue.forEach(p -> allOrdersForSide.addAll(this.offers.get(p)));
            return allOrdersForSide;
        }
        return Collections.emptyList();
    }
}
