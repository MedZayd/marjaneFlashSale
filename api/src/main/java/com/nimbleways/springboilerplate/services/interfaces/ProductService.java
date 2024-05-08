package com.nimbleways.springboilerplate.services.interfaces;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductService {
    void handleNormalProduct(Product product);

    void handleSeasonalProduct(Product product);

    void handleExpiredProduct(Product product);

    void handleFlashSaleProduct(Product p);
}
