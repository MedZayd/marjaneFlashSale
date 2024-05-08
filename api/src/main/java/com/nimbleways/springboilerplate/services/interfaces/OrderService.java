package com.nimbleways.springboilerplate.services.interfaces;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.exceptions.NotFoundException;
import com.nimbleways.springboilerplate.exceptions.ProductTypeNotSupportedException;

public interface OrderService {
    ProcessOrderResponse processOrder(Long orderId) throws NotFoundException, ProductTypeNotSupportedException;
}
