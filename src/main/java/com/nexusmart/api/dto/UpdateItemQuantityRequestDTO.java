package com.nexusmart.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateItemQuantityRequestDTO {
    @NotNull(message = "Quantity is required.")
    @Positive(message = "Provide a quantity greater than zero.")
    int quantity;
}
