package com.nimbleways.springboilerplate.exceptions;

public class ProductTypeNotSupportedException extends Exception {
    public ProductTypeNotSupportedException(String message) {
        super(message);
    }
}
