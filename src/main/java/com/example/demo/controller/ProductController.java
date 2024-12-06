package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Product> getAll() {

        return (List<Product>) repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product request) {
        Product created = repository.save(
                Product.builder()
                        .description(request.getDescription())
                        .price(request.getPrice() != null ? request.getPrice() : BigDecimal.valueOf(0))
                        .build()
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product request) {
        return repository.findById(id)
                .map(product -> {
                    if(request.getName() != null) product.setName(request.getName());
                    if(request.getDescription() != null) product.setDescription(request.getDescription());
                    if(request.getPrice() != null) product.setPrice(request.getPrice());
                    repository.save(product);
                    return ResponseEntity.ok(product);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
