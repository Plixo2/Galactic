package de.plixo.atic.exceptions.reasons;

import lombok.Getter;

public final class TokenFailure extends Failure {
    @Getter
    private final String message;
    @Getter
    private final TokenFailType failType;

    public TokenFailure(String message, TokenFailType failType) {
        this.message = message;
        this.failType = failType;
    }

    public enum TokenFailType {
        CAPTURE_FAILURE,
        TOP_TOKEN,
        UNEXPECTED
    }
}
