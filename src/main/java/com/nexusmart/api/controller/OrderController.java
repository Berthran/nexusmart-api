package com.nexusmart.api.controller;

import com.nexusmart.api.dto.OrderItemResponseDTO;
import com.nexusmart.api.dto.OrderResponseDTO;
import com.nexusmart.api.entity.Order;
import com.nexusmart.api.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

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
        OrderResponseDTO responseDTO = mapOrderToResponseDTO(newOrder);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getOrderHistory(Authentication authentication, Pageable pageable) {
        // 1. Get the current user's email
        String userEmail = authentication.getName();
        // 2. Call the service to get a Page of Order entities
        Page<Order> orderPage = orderService.getOrderForUser(userEmail, pageable);
        // 3. Map the Page<Order> to a Page<OrderResponseDTO>
        // Page<OrderResponseDTO> responseDTOPage = orderPage.map(this::mapOrderToResponseDTO);
        Page<OrderResponseDTO> responseDTOPage = orderPage.map(order -> {
            return mapOrderToResponseDTO(order);
        });
        return ResponseEntity.ok(responseDTOPage);
    }

    @GetMapping("/{orderId:[\\d]+}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId, Authentication authentication) {
        String userEmail = authentication.getName();

        Order order = orderService.getOrderById(userEmail, orderId);

        return ResponseEntity.ok(mapOrderToResponseDTO(order));
    }
    
    private OrderResponseDTO mapOrderToResponseDTO(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setUserId(order.getUser().getId());
        responseDTO.setOrderDate(order.getOrderDate());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setTotalPrice(order.getTotalPrice());

        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream().map(item -> {
            OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtPurchase(item.getPriceAtPurchase());
            return itemDTO;
        }).collect(Collectors.toList());

        responseDTO.setItems(itemDTOs);
        
        return responseDTO;
    }

}
