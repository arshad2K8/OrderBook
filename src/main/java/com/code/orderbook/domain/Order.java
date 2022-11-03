package com.code.orderbook.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private long orderId;
    private double price;
    private char side;  // B "Bid " or O "Offer"
    private long size;
}
