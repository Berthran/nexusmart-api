package com.nexusmart.api.controller;

import com.nexusmart.api.dto.OrderItemResponseDTO;
import com.nexusmart.api.dto.OrderResponseDTO;
import com.nexusmart.api.entity.Order;
import com.nexusmart.api.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(Authentication authentication) {
        Order newOrder = orderService.createOrder(authentication.getName());

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(newOrder.getId());
        responseDTO.setUserId(newOrder.getUser().getId());
        responseDTO.setOrderDate(newOrder.getOrderDate());
        responseDTO.setStatus(newOrder.getStatus());
        responseDTO.setTotalPrice(newOrder.getTotalPrice());

        List<OrderItemResponseDTO> itemDTOs = newOrder.getItems().stream().map(item -> {
            OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtPurchase(item.getPriceAtPurchase());
            return itemDTO;
        }).collect(Collectors.toList());

        responseDTO.setItems(itemDTOs);

        return ResponseEntity.ok(responseDTO);
    }

}
