package com.nexusmart.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private List<CartItemResponseDTO> items;
    // We could add fields like 'totalPrice' here later
}
