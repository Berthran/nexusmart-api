package com.nexusmart.api.service;

import com.nexusmart.api.entity.Cart;
import com.nexusmart.api.entity.CartItem;
import com.nexusmart.api.entity.Product;
import com.nexusmart.api.entity.User;
import com.nexusmart.api.exception.ResourceNotFoundException;
import com.nexusmart.api.repository.CartItemRepository;
import com.nexusmart.api.repository.CartRepository;
import com.nexusmart.api.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    public CartService(ProductRepository productRepository,
                       CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       UserService userService) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }


    @Transactional(readOnly = true)
    public Cart getCartForUser(String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        return this.findOrCreateCartByUser(user);
    }

    @Transactional
    public Cart addItemToCart(String userEmail, Long productId, int quantity) {
        // Step 1. Find the User by their email
        User user = userService.findUserByEmail(userEmail);

        // Step 2. Find the Product by its ID.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Step 3. Find the user's Cart. If they don't have one, create it.
        Cart cart = this.findOrCreateCartByUser(user);

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

    @Transactional
    public void removeItemFromCart(String userEmail, Long cartItemId) {
        // 1. Find the user and their cart
        User user = userService.findUserByEmail(userEmail);

        // 2. Find the cart. If it doesn't exist, they have no items to remove.
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user "));

        // 3. Find the specific cart item *within the user's cart*
        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // 3. Remove the item from the list.
        // Because of 'orphanRemoval = true' on our @OneToMany mapping,
        // Hibernate will automatically delete the CartItem from the database.
        cart.getCartItems().remove(itemToRemove);

        // 4. Save the changes
        //  the explicit .save() call is often not needed for update or delete operations within a transaction.
        cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuantity(String userEmail, Long cartItemId, int newQuantity) {
        // 1. Find the user and their cart
        User user = userService.findUserByEmail(userEmail);

        // 2. Find the cart. If it doesn't exist, they have no items to update.
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user "));

        // 3. Find the specific cart item *within the user's cart*
        CartItem itemToUpdate = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // 4. Get the associated product to check its stock
        Product product = itemToUpdate.getProduct();

        if (product.getStockQuantity() < newQuantity) {
            // We throw a clear exception if there isn't enough stock.
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName() +
                    ". Requested: " + newQuantity + ", Available: " + product.getStockQuantity());
        }

        // 5. If stock is sufficient, update the quantity
        itemToUpdate.setQuantity(newQuantity);

        // 6. Return the cart. No explicit save is needed due to @Transactional.
        return cart;
    }


    private Cart findOrCreateCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
}
