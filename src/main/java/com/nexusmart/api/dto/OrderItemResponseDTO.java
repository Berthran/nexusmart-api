package com.nexusmart.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private  Long productId;
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
}
