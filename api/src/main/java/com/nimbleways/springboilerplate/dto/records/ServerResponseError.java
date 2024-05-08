package com.nimbleways.springboilerplate.dto.records;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServerResponseError (
        String code,
        String message,
        String timestamp
) {
}
