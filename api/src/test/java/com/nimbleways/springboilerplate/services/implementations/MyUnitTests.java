package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.interfaces.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void handleNormalProduct_Available() {
        Product product = new Product();
        product.setAvailable(1);
        product.setLeadTime(5);

        productService.handleNormalProduct(product);

        verify(productRepository).save(product);
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void handleNormalProduct_NotAvailable() {
        Product product = new Product();
        product.setAvailable(0);
        product.setLeadTime(5);

        productService.handleNormalProduct(product);

        verify(notificationService).sendDelayNotification(product.getLeadTime(), null);
        verify(productRepository, never()).save(product);
    }

    @Test
    void handleSeasonalProduct_AvailableDuringSeason() {
        Product product = new Product();
        product.setAvailable(1);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(1));

        productService.handleSeasonalProduct(product);

        verify(productRepository).save(product);
        verify(notificationService, never()).sendOutOfStockNotification(anyString());
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void handleSeasonalProduct_NotAvailableDuringSeason() {
        Product product = new Product();
        product.setAvailable(0);
        product.setLeadTime(3);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(1));

        productService.handleSeasonalProduct(product);

        verify(notificationService).sendOutOfStockNotification(product.getName());
    }

    @Test
    void handleSeasonalProduct_BeforeSeasonStart() {
        Product product = new Product();
        product.setAvailable(1);
        product.setLeadTime(3);
        product.setSeasonStartDate(LocalDate.now().plusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(3));

        productService.handleSeasonalProduct(product);

        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void handleSeasonalProduct_AfterSeasonEnd() {
        Product product = new Product();
        product.setAvailable(0);
        product.setLeadTime(3);
        product.setSeasonStartDate(LocalDate.now().minusDays(3));
        product.setSeasonEndDate(LocalDate.now().plusDays(1));

        productService.handleSeasonalProduct(product);

        verify(notificationService).sendOutOfStockNotification(product.getName());
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void handleSeasonalProduct_AvailableBeforeSeasonEnd() {
        Product product = new Product();
        product.setAvailable(1);
        product.setLeadTime(1);
        product.setSeasonStartDate(LocalDate.now().minusDays(3));
        product.setSeasonEndDate(LocalDate.now().plusDays(3));

        productService.handleSeasonalProduct(product);

        verify(productRepository).save(product);
    }

    @Test
    void handleExpiredProduct_AvailableNotExpired() {
        Product product = new Product();
        product.setAvailable(1);
        product.setExpiryDate(LocalDate.now().plusDays(1));

        productService.handleExpiredProduct(product);

        verify(productRepository).save(product);
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
        verify(notificationService, never()).sendExpirationNotification(anyString(), any());
    }

    @Test
    void handleExpiredProduct_AvailableExpired() {
        Product product = new Product();
        product.setAvailable(1);
        product.setExpiryDate(LocalDate.now().minusDays(1));

        productService.handleExpiredProduct(product);

        verify(productRepository).save(product);
        verify(notificationService).sendExpirationNotification(product.getName(), product.getExpiryDate());
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void handleExpiredProduct_NotAvailable() {
        Product product = new Product();
        product.setAvailable(0);
        product.setLeadTime(1);
        product.setExpiryDate(LocalDate.now().plusDays(1));

        productService.handleExpiredProduct(product);

        verify(notificationService).sendDelayNotification(product.getLeadTime(), product.getName());
    }

    @Test
    void handleFlashSaleProduct_WithinFlashSalePeriod() {
        Product product = new Product();
        product.setAvailable(1);
        product.setFlashSaleStartDate(LocalDate.now().minusDays(1));
        product.setFlashSaleEndDate(LocalDate.now().plusDays(1));

        productService.handleFlashSaleProduct(product);

        verify(productRepository).save(product);
        verify(notificationService, never()).sendOutOfStockNotification(anyString());
    }

    @Test
    void handleFlashSaleProduct_AfterFlashSaleEnd() {
        Product product = new Product();
        product.setAvailable(1);
        product.setFlashSaleStartDate(LocalDate.now().minusDays(3));
        product.setFlashSaleEndDate(LocalDate.now().minusDays(1));

        productService.handleFlashSaleProduct(product);

        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
    }

    @Test
    void handleFlashSaleProduct_NotAvailable() {
        Product product = new Product();
        product.setAvailable(0);
        product.setFlashSaleStartDate(LocalDate.now().minusDays(1));
        product.setFlashSaleEndDate(LocalDate.now().plusDays(1));

        productService.handleFlashSaleProduct(product);

        verify(notificationService).sendOutOfStockNotification(product.getName());
    }
}