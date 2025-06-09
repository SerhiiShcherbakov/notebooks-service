package com.serhiishcherbakov.notebooks.exception;

import lombok.Getter;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class AppException extends ResponseStatusException {
    private final Error error;

    public AppException(Error error) {
        super(error.getStatus(), error.getMessage());
        this.error = error;
    }
}
