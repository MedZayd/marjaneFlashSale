package com.nimbleways.springboilerplate.dto.records;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbleways.springboilerplate.enums.ServerResponseStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServerResponse (
        ServerResponseStatus status,
        String message,
        ServerResponseError error
) {
    public ServerResponse(String message) {
        this(ServerResponseStatus.SUCCESS, message, null);
    }

    public ServerResponse(ServerResponseStatus status, String message) {
        this(status, message, null);
    }

    public ServerResponse(ServerResponseError error) {
        this(ServerResponseStatus.ERROR, null, error);
    }
}
