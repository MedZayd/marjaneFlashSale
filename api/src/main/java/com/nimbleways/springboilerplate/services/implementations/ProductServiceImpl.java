package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import java.util.Set;

import com.nimbleways.springboilerplate.services.interfaces.NotificationService;
import com.nimbleways.springboilerplate.services.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void handleNormalProduct(Product product) {
        log.info("Handle normal product: {}", product);
        if (product.getAvailable() > 0) {
            log.info("Product is available, decreasing availability.");
            product.decreaseAvailability();
            productRepository.save(product);
        } else {
            log.info("Product no longer available. Notifying lead time.");
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }

    @Override
    public void handleSeasonalProduct(Product product) {
        log.info("Handle seasonal product: {}", product);
        LocalDate now = LocalDate.now();
        if (now.isAfter(product.getSeasonStartDate())
                && now.isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0) {
            log.info("Product is available, decreasing availability.");
            product.decreaseAvailability();
            productRepository.save(product);
        } else if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            log.info("Product will be available way after the season has ended. Notifying out of stock.");
            notificationService.sendOutOfStockNotification(product.getName());
        } else if (product.getSeasonStartDate().isAfter(now)) {
            log.info("Product season has not yet started. Notifying out of stock.");
            productRepository.save(product);
            notificationService.sendOutOfStockNotification(product.getName());
        } else {
            log.info("Product will be available before the season ends. Notifying lead time.");
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }

    @Override
    public void handleExpiredProduct(Product product) {
        log.info("Handle expired product: {}", product);
        LocalDate now = LocalDate.now();
        if (product.getAvailable() > 0) {
            log.info("Product is available");
            if (product.getExpiryDate().isAfter(LocalDate.now())) {
                log.info("Product is not yet expired, decreasing availability.");
                product.decreaseAvailability();
            } else {
                log.info("Product is expired, setting availability to 0 and notifying expiration.");
                product.setAvailable(0);
                notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            }
            productRepository.save(product);
        } else {
            log.info("Product no longer available, notifying lead time.");
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }

    @Override
    public void handleFlashSaleProduct(Product product) {
        log.info("Handle flash sale product: {}", product);
        LocalDate now = LocalDate.now();
        if (now.isAfter(product.getFlashSaleStartDate())
                && now.isBefore(product.getFlashSaleEndDate())
                && product.getAvailable() > 0) {
            log.info("We are currently in the flash sale period and the product is still available, decreasing availability.");
            product.decreaseAvailability();
            productRepository.save(product);
        } if (now.isAfter(product.getFlashSaleEndDate()) && product.getAvailable() > 0) {
            log.info("Even though the product is available, the flash sale has ended.");
            product.setAvailable(0);
            productRepository.save(product);
            notificationService.sendOutOfStockNotification(product.getName());
        }
        else {
            log.info("The product is no longer available, notifying out of stock.");
            notificationService.sendOutOfStockNotification(product.getName());
        }
    }
}