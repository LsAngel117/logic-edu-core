package com.logossystemsit.logiceducore.shared.errors.exceptions;

import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode code;

    public ResourceNotFoundException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
