package com.logossystemsit.logiceducore.shared.errors;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        ErrorCode code,
        String message,
        String path
) {

    public static ErrorResponse of(
            int status,
            String error,
            ErrorCode code,
            String message,
            String path
    ) {
        return new ErrorResponse(
                Instant.now(),
                status,
                error,
                code,
                message,
                path
        );
    }
}

