package com.mlops.exception;

/**
 * Exception thrown when attempting to delete a workspace that still has models assigned to it.
 */
public class WorkspaceNotEmptyException extends RuntimeException {
    public WorkspaceNotEmptyException(String message) {
        super(message);
    }
}
