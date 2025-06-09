package com.serhiishcherbakov.notebooks.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Error {
    UNAUTHORIZED_CLIENT(HttpStatus.UNAUTHORIZED, "Missing-Api-Key"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "User is not authorized"),
    NOTEBOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "Notebook not found"),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "Tag not found"),
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "Validation error"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;
}
