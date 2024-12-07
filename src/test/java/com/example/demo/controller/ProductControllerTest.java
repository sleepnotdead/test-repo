package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ProductControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        repository.deleteAll(); // Clear the database before each test
    }

    @Test
    void testGetAllProducts() throws Exception {
        // Arrange: Add a product to the database
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("A product for testing");
        product.setPrice(BigDecimal.valueOf(19.99));
        repository.save(product);

        // Act and Assert: Perform GET request and validate response
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].description").value("A product for testing"))
                .andExpect(jsonPath("$[0].price").value(19.99));
    }

    @Test
    void testCreateProduct() throws Exception {
        // Arrange: Define product JSON
        String productJson = """
                {
                  "name": "Test Product",
                  "description": "A product for testing",
                  "price": 19.99
                }
                """;

        // Act: Perform POST request
        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("A product for testing"))
                .andExpect(jsonPath("$.price").value(19.99));

        // Assert: Verify the product is saved in the database
        Product savedProduct = ((List<Product>)repository.findAll()).get(0);
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getDescription()).isEqualTo("A product for testing");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(19.99));
    }

    @Test
    void testCreateProductWithoutName_ShouldFail() throws Exception {
        // Arrange: Define invalid product JSON
        String productJson = """
                {
                  "description": "No name here",
                  "price": 10.00
                }
                """;

        // Act and Assert: Perform POST request and expect 400 Bad Request
        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson))
                .andExpect(status().isBadRequest());
    }
}
