package com.nexusmart.api.service;

import com.nexusmart.api.dto.CreateProductRequestDTO;
import com.nexusmart.api.dto.UpdateProductRequestDTO;
import com.nexusmart.api.entity.Product;
import com.nexusmart.api.entity.User;
import com.nexusmart.api.exception.ResourceConflictException;
import com.nexusmart.api.exception.ResourceNotFoundException;
import com.nexusmart.api.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(CreateProductRequestDTO requestDTO){
        // 1. Check for uniqueness before doing anything else
        Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(requestDTO.getName());
        if (existingProduct.isPresent()) {
            throw new ResourceConflictException("A product with name '" + requestDTO.getName() + "' already exists.");
        }
        // 2. If it's unique, proceed with creating the new product
        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setImageUrl(requestDTO.getImageUrl());
        product.setVendorName(requestDTO.getVendorName());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setCategory(requestDTO.getCategory());

        return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    public Product updateProduct(Long productId, UpdateProductRequestDTO requestDTO) {
        Product existingProduct = this.getProductById(productId);

        // Update the product with the fields in the requestDTO
        existingProduct.setName(requestDTO.getName());
        existingProduct.setDescription(requestDTO.getDescription());
        existingProduct.setPrice(requestDTO.getPrice());
        existingProduct.setStockQuantity(requestDTO.getStockQuantity());
        existingProduct.setImageUrl(requestDTO.getImageUrl());
        existingProduct.setVendorName(requestDTO.getVendorName());
        existingProduct.setCategory(requestDTO.getCategory());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product existingProduct = this.getProductById(id);
        productRepository.delete(existingProduct);
    }

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

}