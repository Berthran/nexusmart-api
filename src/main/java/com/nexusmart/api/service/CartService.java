package com.nexusmart.api.service;

import com.nexusmart.api.entity.Cart;
import com.nexusmart.api.entity.CartItem;
import com.nexusmart.api.entity.Product;
import com.nexusmart.api.entity.User;
import com.nexusmart.api.exception.ResourceNotFoundException;
import com.nexusmart.api.repository.CartItemRepository;
import com.nexusmart.api.repository.CartRepository;
import com.nexusmart.api.repository.ProductRepository;
import com.nexusmart.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    public CartService(UserRepository userRepository,
                       ProductRepository productRepository,
                       CartRepository cartRepository,
                       CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }


    @Transactional
    public Cart getCartForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        return findOrCreateCartByUser(user);
    }

    @Transactional
    public Cart addItemToCart(String userEmail, Long productId, int quantity) {
        // Step 1. Find the User by their email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // Step 2. Find the Product by its ID.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Step 3. Find the user's Cart. If they don't have one, create it.
        Cart cart = findOrCreateCartByUser(user);

        // Step 4. Check if the product is already in the cart.
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getCartItems().add(newItem);
            // We don't need to save the newItem directly because of CascadeType.ALL on the Cart's cartItems list.
        }

        // 4. Save the cart. Cascade will handle the items.
        return cartRepository.save(cart);
    }

    public Cart findOrCreateCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
}
