package com.code.orderbook.controller;

import com.code.orderbook.domain.Order;
import com.code.orderbook.domain.OrderOperationResult;
import com.code.orderbook.service.OrderBook;
import com.code.orderbook.service.OrderHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/orderbook")
@Validated
@Slf4j
public class OrderBookController {

    // Normally Order Handler Service should Instantiate OrderBook's for each ticker
    // for now I am assuming there is one OrderBook


    private OrderHandlerService orderHandlerService;

    public OrderBookController(OrderHandlerService orderHandlerService) {
        this.orderHandlerService = orderHandlerService;
    }

    @PostMapping("/placeOrder/{ticker}")
    void placeOrder(@PathVariable String ticker, @RequestBody Order order) {
        this.orderHandlerService.addOrder(ticker, order);
    }

    @PostMapping("/removeOrder/{ticker}/{orderId}")
    void removeOrder(@PathVariable String ticker, @PathVariable long orderId) {
        this.orderHandlerService.removeOrder(ticker, orderId);
    }

    @GetMapping("/getAllOrdersForSide/{ticker}/{side}")
    List<Order> getAllOrdersForSide(@PathVariable String ticker, @PathVariable char side) {
        return this.orderHandlerService.getAllOrdersForSide(ticker, side);
    }

    @GetMapping("/getPriceForSideAndLevel/{ticker}/{side}/{level}")
    double getPriceForSideAndLevel(@PathVariable String ticker, @PathVariable char side, @PathVariable int level) {
        return this.orderHandlerService.getPriceForSideAndLevel(ticker, side, level);
    }

    @GetMapping("/getTotalSizeForLevel/{ticker}/{side}/{level}")
    long getTotalSizeForLevel(@PathVariable String ticker, @PathVariable char side, @PathVariable int level) {
        return this.orderHandlerService.getTotalSizeForLevel(ticker, side, level);
    }

}
