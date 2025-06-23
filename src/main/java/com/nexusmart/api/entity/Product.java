package com.nexusmart.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity // <-- 1. Tells JPA this is a database entity
@Table(name = "products") // <--. Links to the "products" table
public class Product {

    @Id // <--3. marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- 4. Lets the database handle ID generation
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")  // Using columnDefinition is an alternative way to specify longer text types
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)  // <-- 5. Precision for money
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0; // It's good practice to initialize default values here

    @Column(name = "vendor_name", length = 150)
    private String vendorName;

    @Column(length = 100)
    private String category;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp // <-- 6. Automates creation timestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp // <-- 7. Automates update timestamp
    private LocalDateTime updatedAt;


}
