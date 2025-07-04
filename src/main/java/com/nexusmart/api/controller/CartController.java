package com.nexusmart.api.controller;

import com.nexusmart.api.dto.AddItemToCartRequestDTO;
import com.nexusmart.api.dto.CartItemResponseDTO;
import com.nexusmart.api.dto.CartResponseDTO;
import com.nexusmart.api.dto.UpdateItemQuantityRequestDTO;
import com.nexusmart.api.entity.Cart;
import com.nexusmart.api.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        Cart updatedCart = cartService.addItemToCart(authentication.getName(), requestDTO.getProductId(), requestDTO.getQuantity());
        return ResponseEntity.ok(mapCartToResponseDTO(updatedCart));
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCartForUser(Authentication authentication) {
        Cart cart = cartService.getCartForUser(authentication.getName());
        return ResponseEntity.ok(mapCartToResponseDTO(cart));
    }

    @DeleteMapping("/items/{itemId:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromCart(@PathVariable Long itemId, Authentication authentication) {
        cartService.removeItemFromCart(authentication.getName(), itemId);
    }

    @PutMapping("/items/{itemId:[\\d]+}")
    public ResponseEntity<CartResponseDTO> updateItemQuantity(@PathVariable Long itemId, @Valid @RequestBody UpdateItemQuantityRequestDTO requestDTO, Authentication authentication) {
        Cart cart = cartService.updateItemQuantity(authentication.getName(), itemId, requestDTO.getQuantity());

        return ResponseEntity.ok(mapCartToResponseDTO(cart));
    }


    private CartResponseDTO mapCartToResponseDTO(Cart cart) {
        CartResponseDTO responseDTO = new CartResponseDTO();
        responseDTO.setId(cart.getId());
        responseDTO.setUserId(cart.getUser().getId());

        List<CartItemResponseDTO> itemDTOS = cart.getCartItems().stream().map(item -> {
            CartItemResponseDTO itemDTO = new CartItemResponseDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getProduct().getPrice());
            return itemDTO;
        }).collect(Collectors.toList());

        responseDTO.setItems(itemDTOS);
        return responseDTO;
    }
}
