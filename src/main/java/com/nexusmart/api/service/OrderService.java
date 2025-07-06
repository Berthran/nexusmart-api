package com.nexusmart.api.service;

import com.nexusmart.api.entity.*;
import com.nexusmart.api.exception.ResourceNotFoundException;
import com.nexusmart.api.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final UserService userService;



    public OrderService(CartService cartService, UserService userService, OrderRepository orderRepository, ProductRepository productRepository, CartRepository cartRepository) {
        this.cartService = cartService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;

    }

    @Transactional
    public Order createOrder(String userEmail) {
        // 1. Find the User
        User user = userService.findUserByEmail(userEmail);

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

    @Transactional(readOnly = true)
    public Page<Order> getOrderForUser(String userEmail, Pageable pageable) {
        User user = userService.findUserByEmail(userEmail);

        return orderRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(String userEmail, Long orderId) {
        User user = userService.findUserByEmail(userEmail);

        // 2. Find the order by its ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // 3. THE CRITICAL OWNERSHIP CHECK
        // We check if the ID of the user who owns the order is the same as the ID of the user making the request.
        if (!order.getUser().getId().equals(user.getId())) {
            // If they don't match, the user is forbidden from seeing this resource.
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        return order;
    }
}
