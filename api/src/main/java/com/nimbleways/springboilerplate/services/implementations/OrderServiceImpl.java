package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.NotFoundException;
import com.nimbleways.springboilerplate.exceptions.ProductTypeNotSupportedException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.interfaces.OrderService;
import com.nimbleways.springboilerplate.services.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Override
    public ProcessOrderResponse processOrder(Long orderId) throws NotFoundException, ProductTypeNotSupportedException {
        log.info("Processing order: {}", orderId);
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() -> new NotFoundException(String.format("Order id '%s' not found.", orderId)));
        Set<Product> products = order.getItems();
        for (Product p : products) {
            switch (p.getType()) {
                case NORMAL -> productService.handleNormalProduct(p);
                case SEASONAL -> productService.handleSeasonalProduct(p);
                case EXPIRABLE -> productService.handleExpiredProduct(p);
                case FLASHSALE -> productService.handleFlashSaleProduct(p);
                default -> throw new ProductTypeNotSupportedException(String.format("Product ['%s'] type ['%s'] is not handled for the moment.", p.getId(), p.getType()));
            }
        }
        return new ProcessOrderResponse(order.getId());
    }
}
