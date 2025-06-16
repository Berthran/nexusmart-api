package com.nexusmart.api.controller;

import com.nexusmart.api.dto.CreateProductRequestDTO;
import com.nexusmart.api.dto.ProductResponseDTO;
import com.nexusmart.api.entity.Product;
import com.nexusmart.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Using just @PostMapping maps to the base URL: /api/products
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductRequestDTO requestDTO) {
        Product createdProduct = productService.createProduct(requestDTO);

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(createdProduct.getId());
        responseDTO.setName(createdProduct.getName());
        responseDTO.setDescription(createdProduct.getDescription());
        responseDTO.setPrice(createdProduct.getPrice());
        responseDTO.setImageUrl(createdProduct.getImageUrl());
        responseDTO.setVendorName(createdProduct.getVendorName());
        responseDTO.setStockQuantity(createdProduct.getStockQuantity());
        responseDTO.setCategory(createdProduct.getCategory());
        responseDTO.setCreatedAt(createdProduct.getCreatedAt());
        responseDTO.setUpdatedAt(createdProduct.getUpdatedAt());

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

}
