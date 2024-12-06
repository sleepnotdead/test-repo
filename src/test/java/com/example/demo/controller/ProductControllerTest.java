package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository repository;

    @Test
    void testGetAllProducts() throws Exception {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testCreateProduct() throws Exception {
        // Mock repository: return the product as "saved"
        Mockito.when(repository.save(Mockito.any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(1L); // Simulate DB assigning an ID
            return saved;
        });

        String productJson = """
                {
                  "name": "Test Product",
                  "description": "A product for testing",
                  "price": 19.99
                }
                """;

        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("A product for testing"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    /**
     * This test simulates a scenario where a required field (name) is missing.
     * We expect the server to reject the request with a 400 status, assuming we have validation in place.
     * However, since the code doesn’t perform validation or return a 400, this test will fail subtly.
     */
    @Test
    void testCreateProductWithoutName_ShouldFail() throws Exception {
        String productJson = """
                {
                  "description": "No name here",
                  "price": 10.00
                }
                """;

        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson))
                // We expect validation to fail and return a 400 Bad Request
                .andExpect(status().isBadRequest());
    }
}
