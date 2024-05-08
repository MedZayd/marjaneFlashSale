package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.interfaces.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.SetupDatabase;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;

// import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@SetupDatabase
@SpringBootTest
@AutoConfigureMockMvc
public class OrderResourceIntegrationTests {
        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private NotificationService notificationService;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private ProductRepository productRepository;

        @Test
        public void processOrderShouldReturn() throws Exception {
                List<Product> allProducts = createProducts();
                Set<Product> orderItems = new HashSet<Product>(allProducts);
                Order order = createOrder(orderItems);
                productRepository.saveAll(allProducts);
                order = orderRepository.save(order);
                mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                                .contentType("application/json"))
                                .andExpect(status().isOk());
                Optional<Order> resultOrder = orderRepository.findById(order.getId());
                Assertions.assertTrue(resultOrder.isPresent());
                Assertions.assertEquals(resultOrder.get().getId(), order.getId());
        }

        private static Order createOrder(Set<Product> products) {
                Order order = new Order();
                order.setItems(products);
                return order;
        }

        private static List<Product> createProducts() {
                LocalDate now = LocalDate.now();
                List<Product> products = new ArrayList<>();
                products.add(Product.builder().leadTime(15).available(30).type(ProductType.NORMAL).name("USB Cable").build());
                products.add(Product.builder().leadTime(10).available(0).type(ProductType.NORMAL).name("USB Dongle").build());
                products.add(Product.builder().leadTime(15).available(30).type(ProductType.EXPIRABLE).name("Butter").expiryDate(now.plusDays(26)).build());
                products.add(Product.builder().leadTime(90).available(6).type(ProductType.EXPIRABLE).name("Milk").expiryDate(now.minusDays(2)).build());
                products.add(Product.builder().leadTime(15).available(30).type(ProductType.SEASONAL).name("Watermelon").seasonStartDate(now.minusDays(2)).seasonEndDate(now.plusDays(58)).build());
                products.add(Product.builder().leadTime(15).available(30).type(ProductType.SEASONAL).name("Grapes").seasonStartDate(now.plusDays(180)).seasonEndDate(now.plusDays(240)).build());
                products.add(Product.builder().available(30).type(ProductType.FLASHSALE).name("Bed cover sheets").flashSaleStartDate(now.minusDays(2)).flashSaleEndDate(now.plusDays(8)).build());
                products.add(Product.builder().available(30).type(ProductType.FLASHSALE).name("Lamps").flashSaleStartDate(now.plusDays(2)).flashSaleEndDate(now.plusDays(10)).build());
                products.add(Product.builder().available(0).type(ProductType.FLASHSALE).name("Lamps").flashSaleStartDate(now.plusDays(2)).flashSaleEndDate(now.plusDays(10)).build());
                return products;
        }
}
