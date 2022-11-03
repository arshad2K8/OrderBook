package com.code.orderbook;

import com.code.orderbook.domain.Order;
import com.code.orderbook.service.OrderBook;
import com.code.orderbook.service.OrderBookImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderBookImplTest {

    @Test
    public void testAddBidOrdersAreSortedByTimePriority() {
        OrderBookImpl orderBook = new OrderBookImpl("IBM");
        initBuySideOrders(orderBook);

        List<Order> orders = orderBook.getAllOrdersForSide('B');

        Assertions.assertEquals(2, orders.get(0).getOrderId());
        Assertions.assertEquals(4, orders.get(1).getOrderId());
        Assertions.assertEquals(5, orders.get(2).getOrderId());
        Assertions.assertEquals(1, orders.get(3).getOrderId());
        Assertions.assertEquals(3, orders.get(4).getOrderId());

        // getPriceForSideAndLevel
        Assertions.assertEquals(30, orderBook.getPriceForSideAndLevel('B', 1));
        Assertions.assertEquals(25, orderBook.getPriceForSideAndLevel('B', 2));
        Assertions.assertEquals(20, orderBook.getPriceForSideAndLevel('B', 3));
        Assertions.assertEquals(10, orderBook.getPriceForSideAndLevel('B', 4));

        // getTotalSizeForLevel
        Assertions.assertEquals(200, orderBook.getTotalSizeForLevel('B', 1));
        Assertions.assertEquals(700, orderBook.getTotalSizeForLevel('B', 2));
        Assertions.assertEquals(100, orderBook.getTotalSizeForLevel('B', 3));
        Assertions.assertEquals(50, orderBook.getTotalSizeForLevel('B', 4));
    }

    @Test
    public void testRemoveOrderWorksFine() {
        OrderBookImpl orderBook = new OrderBookImpl("IBM");
        initBuySideOrders(orderBook);
        List<Order> orders = orderBook.getAllOrdersForSide('B');
        Assertions.assertEquals(5, orders.size());

        orderBook.removeOrder(1);
        Assertions.assertTrue(orderBook.getAllOrdersForSide('B').stream().noneMatch(x -> x.getOrderId() == 1));
    }

    @Test
    public void testModifyOrderWorksFine() {
        OrderBookImpl orderBook = new OrderBookImpl("IBM");
        initBuySideOrders(orderBook);
        List<Order> orders = orderBook.getAllOrdersForSide('B');
        Assertions.assertEquals(5, orders.size());
        Assertions.assertEquals(2, orders.get(0).getOrderId());
        Assertions.assertEquals(200, orders.get(0).getSize());

        orderBook.modifyOrder(2, 300);
        orders = orderBook.getAllOrdersForSide('B');
        Assertions.assertEquals(2, orders.get(0).getOrderId());
        Assertions.assertEquals(300, orders.get(0).getSize());

    }

    @Test
    public void testAddOfferOrdersAreSortedByTimePriority() {
        OrderBookImpl orderBook = new OrderBookImpl("IBM");

        Order order1 = Order.builder().orderId(1).side('O').size(100).price(20).build();
        Order order2 = Order.builder().orderId(2).side('O').size(200).price(30).build();
        Order order3 = Order.builder().orderId(3).side('O').size(50).price(10).build();
        Order order4 = Order.builder().orderId(4).side('O').size(300).price(25).build();
        Order order5 = Order.builder().orderId(5).side('O').size(400).price(25).build();
        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);
        orderBook.addOrder(order4);
        orderBook.addOrder(order5);
        List<Order> orders = orderBook.getAllOrdersForSide('O');

        Assertions.assertIterableEquals(Arrays.asList(3L, 1L, 4L, 5L, 2L),
                orders.stream().map(Order::getOrderId)
                        .collect(Collectors.toList()));
    }

    private void initBuySideOrders(OrderBook orderBook) {
        Order order1 = Order.builder().orderId(1).side('B').size(100).price(20).build();
        Order order2 = Order.builder().orderId(2).side('B').size(200).price(30).build();
        Order order3 = Order.builder().orderId(3).side('B').size(50).price(10).build();
        Order order4 = Order.builder().orderId(4).side('B').size(300).price(25).build();
        Order order5 = Order.builder().orderId(5).side('B').size(400).price(25).build();

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);
        orderBook.addOrder(order4);
        orderBook.addOrder(order5);
    }
}
