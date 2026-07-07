package com.mlops.exception;

/**
 * Exception thrown when attempting to add metrics to a deprecated model.
 */
public class ModelDeprecatedException extends RuntimeException {
    public ModelDeprecatedException(String message) {
        super(message);
    }
}
