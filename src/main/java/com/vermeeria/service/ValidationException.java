package com.vermeeria.service;

/**
 * Indicates invalid user input or invalid business state.
 *
 * @author Jette
 */
public class ValidationException extends RuntimeException {

    /**
     * Creates a validation exception with a user-facing message.
     *
     * @param message the validation message
     */
    public ValidationException(final String message) {
        super(message);
    }
}
