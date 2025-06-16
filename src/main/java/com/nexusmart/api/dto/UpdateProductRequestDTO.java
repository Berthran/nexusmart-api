package com.nexusmart.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductRequestDTO {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Product price is required")
    @Positive(message = "Price must be positive.")
    private BigDecimal price;

    private String description;

    private String imageUrl;

    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private int stockQuantity;

    private String vendorName;

    private String category;
}
