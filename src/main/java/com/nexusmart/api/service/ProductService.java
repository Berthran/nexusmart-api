package com.nexusmart.api.service;

import com.nexusmart.api.dto.CreateProductRequestDTO;
import com.nexusmart.api.entity.Product;
import com.nexusmart.api.exception.ResourceConflictException;
import com.nexusmart.api.repository.ProductRepository;
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
        Optional<Product> existingProduct = productRepository.findByName(requestDTO.getName());
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
}
