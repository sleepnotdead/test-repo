package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.demo.entity.Product;


public interface ProductRepository extends CrudRepository<Product, Long> {
}
