package com.nexusmart.api.controller;

import com.nexusmart.api.dto.CreateProductRequestDTO;
import com.nexusmart.api.dto.PagedResponseDTO;
import com.nexusmart.api.dto.ProductResponseDTO;
import com.nexusmart.api.dto.UpdateProductRequestDTO;
import com.nexusmart.api.entity.Product;
import com.nexusmart.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDTO<ProductResponseDTO>> getProducts(Pageable pageable) {
        Page<Product> productPage = productService.getProducts(pageable);

        List<ProductResponseDTO> productDTOs = productPage.stream()
                .map(this::mapProductToResponseDTO)
                .toList();
        PagedResponseDTO<ProductResponseDTO> response = new PagedResponseDTO<>(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id:[\\d]+}")
    public ResponseEntity<ProductResponseDTO> viewProduct(@PathVariable Long id) {
        Product existingProduct = productService.getProductById(id);
        // Use the .ok() shortcut for a 200 OK response
        // return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        return ResponseEntity.ok(mapProductToResponseDTO(existingProduct));
    }

    // Using just @PostMapping maps to the base URL: /api/products
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductRequestDTO requestDTO) {
        Product createdProduct = productService.createProduct(requestDTO);

        ProductResponseDTO responseDTO = mapProductToResponseDTO(createdProduct);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id:[\\d]+}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequestDTO requestDTO) {
        Product updatedProduct = productService.updateProduct(id, requestDTO);

        ProductResponseDTO responseDTO = mapProductToResponseDTO(updatedProduct);

        // Use the .ok() shortcut for a 200 OK response
        // return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    private ProductResponseDTO mapProductToResponseDTO(Product product) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(product.getId());
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setImageUrl(product.getImageUrl());
        responseDTO.setVendorName(product.getVendorName());
        responseDTO.setStockQuantity(product.getStockQuantity());
        responseDTO.setCategory(product.getCategory());
        responseDTO.setCreatedAt(product.getCreatedAt());
        responseDTO.setUpdatedAt(product.getUpdatedAt());
        return  responseDTO;
    }

}
