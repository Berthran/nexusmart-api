package com.nexusmart.api.controller;

import com.nexusmart.api.dto.AddItemToCartRequestDTO;
import com.nexusmart.api.dto.CartItemResponseDTO;
import com.nexusmart.api.dto.CartResponseDTO;
import com.nexusmart.api.entity.Cart;
import com.nexusmart.api.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItemToCartRequest(@Valid @RequestBody AddItemToCartRequestDTO requestDTO, Authentication authentication) {
        // 1. Get the current user's email from the Authentication principal
        String userEmail = authentication.getName();

        // 2. Call the service which now returns the updated cart
        Cart updatedCart = cartService.addItemToCart(userEmail, requestDTO.getProductId(), requestDTO.getQuantity());

        // 3. Map the Cart Entity to a CartResponseDTO
        CartResponseDTO responseDTO = new CartResponseDTO();
        responseDTO.setId(updatedCart.getId());
        responseDTO.setUserId(updatedCart.getUser().getId());

        List<CartItemResponseDTO> itemDTOs = updatedCart.getCartItems().stream().map(item -> {
            CartItemResponseDTO itemDTO = new CartItemResponseDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getProduct().getPrice());
            return itemDTO;
        }).collect(Collectors.toList());

        responseDTO.setItems(itemDTOs);

        return ResponseEntity.ok(responseDTO);
    }
}
