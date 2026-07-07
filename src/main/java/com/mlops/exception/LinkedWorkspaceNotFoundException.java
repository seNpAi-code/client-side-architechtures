package com.mlops.exception;

/**
 * Exception thrown when a model is created referencing a workspaceId that does not exist.
 */
public class LinkedWorkspaceNotFoundException extends RuntimeException {
    public LinkedWorkspaceNotFoundException(String message) {
        super(message);
    }
}
