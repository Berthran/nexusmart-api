package com.nexusmart.api.dto;


import com.nexusmart.api.entity.OrderStatus;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private List<OrderItemResponseDTO> items;

}
