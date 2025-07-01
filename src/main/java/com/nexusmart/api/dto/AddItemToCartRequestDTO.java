package com.nexusmart.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddItemToCartRequestDTO {
    @NotNull(message = "Product ID required")
    private Long productId;

    @NotNull(message = "A product quantity is required")
    @Positive(message = "Product quantity must be positive")
    private int quantity;
}
