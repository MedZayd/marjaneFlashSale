package com.nimbleways.springboilerplate.dto.records;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbleways.springboilerplate.enums.ServerResponseStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServerResponseData<T>(ServerResponseStatus status, T data) {
    public ServerResponseData(T data) {
        this(ServerResponseStatus.SUCCESS, data);
    }
}

