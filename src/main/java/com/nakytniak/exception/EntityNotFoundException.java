package com.nakytniak.exception;

public class EntityNotFoundException extends RuntimeException {
    protected static final String NOT_FOUND_MESSAGE = "was not found";

    public EntityNotFoundException(final String message) {
        super(message);
    }

    public EntityNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(final Class<?> entityClass, final String id) {
        super(String.format("%s with id %s %s", entityClass.getSimpleName(), id, NOT_FOUND_MESSAGE));
    }
}