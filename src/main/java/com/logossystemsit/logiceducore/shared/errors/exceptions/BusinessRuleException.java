package com.logossystemsit.logiceducore.shared.errors.exceptions;

import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

public class BusinessRuleException extends RuntimeException {

    private final ErrorCode code;

    public BusinessRuleException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
