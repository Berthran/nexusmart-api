package com.nexusmart.api.service;

import com.nexusmart.api.entity.*;
import com.nexusmart.api.exception.ResourceNotFoundException;
import com.nexusmart.api.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;



    public OrderService(CartService cartService, UserRepository userRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, CartRepository cartRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;

    }

    @Transactional
    public Order createOrder(String userEmail) {
        // 1. Find the User
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        // 2. Find the user's Cart
        Cart cart = cartService.getCartForUser(userEmail);

        // 3. Check if the cart is empty
        if (cart.getCartItems().isEmpty())
            throw new IllegalStateException("Cannot create order from an empty cart.");

        // 4. Pre-check all items for stock
        for (CartItem item : cart.getCartItems()) {
            if (item.getQuantity() > item.getProduct().getStockQuantity())
                throw new IllegalStateException("Not enough stock for product: " + item.getProduct().getName());
        }

        // 5. Create new Order
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setStatus(OrderStatus.PENDING);

        // 6. Loop through CartItem and create new OrderItems// 6. Loop through CartItems, create new OrderItems, and calculate total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice()); // Snapshot the price

            newOrder.getItems().add(orderItem);

            // Correctly calculate total price: price * quantity
            BigDecimal itemTotal = orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        // 7. Set the total price of the order
        newOrder.setTotalPrice(totalPrice);

        // 8. Save the Order (and its OrderItems due to cascade)
        Order savedOrder = orderRepository.save(newOrder);

        // 9. Loop through OrderItems and decrease stock of each product
        for (OrderItem orderItem : savedOrder.getItems()) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());
            productRepository.save(product);
        }

        // 10. Clear the Cart correctly
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // 11. Return the saved order
        return savedOrder;
    }
}
