package com.nimbleways.springboilerplate.contollers;


import com.nimbleways.springboilerplate.dto.records.ServerResponse;
import com.nimbleways.springboilerplate.dto.records.ServerResponseError;
import com.nimbleways.springboilerplate.enums.ServerResponseStatus;
import com.nimbleways.springboilerplate.exceptions.NotFoundException;
import com.nimbleways.springboilerplate.exceptions.ProductTypeNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ControllerAdvice
public class SpringGuardian {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ZZZZ");

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerResponse> handleException(Exception e) {
        log.error("An unexpected error occurred: {} {}", e.getClass().getName(), e.getMessage());
        ServerResponseError error =
                new ServerResponseError(generateUniqueCode(), e.getMessage(), formatter.format(OffsetDateTime.now()));
        ServerResponse serverResponse = new ServerResponse(error);
        return new ResponseEntity<>(serverResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ServerResponse> handleNotFoundException(NotFoundException e) {
        ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, e.getMessage());
        return new ResponseEntity<>(serverResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductTypeNotSupportedException.class)
    public ResponseEntity<ServerResponse> handleProductTypeNotSupportedException(ProductTypeNotSupportedException e) {
        ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, e.getMessage());
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    private String generateUniqueCode() {
        return "err_".concat(RandomStringUtils.randomAlphanumeric(10));
    }
}
