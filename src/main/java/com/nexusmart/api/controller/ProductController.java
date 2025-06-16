package com.nexusmart.api.controller;

import com.nexusmart.api.dto.CreateProductRequestDTO;
import com.nexusmart.api.dto.ProductResponseDTO;
import com.nexusmart.api.dto.UpdateProductRequestDTO;
import com.nexusmart.api.entity.Product;
import com.nexusmart.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id:[\\d]+}")
    public ResponseEntity<ProductResponseDTO> viewProduct(@PathVariable Long id) {
        Product existingProduct = productService.getProductById(id);

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(existingProduct.getId());
        responseDTO.setName(existingProduct.getName());
        responseDTO.setDescription(existingProduct.getDescription());
        responseDTO.setPrice(existingProduct.getPrice());
        responseDTO.setImageUrl(existingProduct.getImageUrl());
        responseDTO.setVendorName(existingProduct.getVendorName());
        responseDTO.setStockQuantity(existingProduct.getStockQuantity());
        responseDTO.setCategory(existingProduct.getCategory());
        responseDTO.setCreatedAt(existingProduct.getCreatedAt());
        responseDTO.setUpdatedAt(existingProduct.getUpdatedAt());

        // Use the .ok() shortcut for a 200 OK response
        // return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        return ResponseEntity.ok(responseDTO);
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

    @PutMapping("/{id:[\\d]+}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequestDTO requestDTO) {
        Product updatedProduct = productService.updateProduct(id, requestDTO);

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(updatedProduct.getId());
        responseDTO.setName(updatedProduct.getName());
        responseDTO.setDescription(updatedProduct.getDescription());
        responseDTO.setPrice(updatedProduct.getPrice());
        responseDTO.setImageUrl(updatedProduct.getImageUrl());
        responseDTO.setVendorName(updatedProduct.getVendorName());
        responseDTO.setStockQuantity(updatedProduct.getStockQuantity());
        responseDTO.setCategory(updatedProduct.getCategory());
        responseDTO.setCreatedAt(updatedProduct.getCreatedAt());
        responseDTO.setUpdatedAt(updatedProduct.getUpdatedAt());

        // Use the .ok() shortcut for a 200 OK response
        // return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
