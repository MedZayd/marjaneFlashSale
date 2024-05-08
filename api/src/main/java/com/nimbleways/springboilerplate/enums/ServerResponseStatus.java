package com.nimbleways.springboilerplate.enums;

import lombok.Getter;

@Getter
public enum ServerResponseStatus {
    SUCCESS("success"),
    ERROR("error");

    public final String value;

    ServerResponseStatus(String label) {
        this.value = label;
    }

    public static ServerResponseStatus valueOfLabel(String label) {
        for (ServerResponseStatus status : values()) {
            if (status.value.equals(label)) {
                return status;
            }
        }
        return null;
    }
}
