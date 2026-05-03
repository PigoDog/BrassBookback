package com.brassbook.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {}
