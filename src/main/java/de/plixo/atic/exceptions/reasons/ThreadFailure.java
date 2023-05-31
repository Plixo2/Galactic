package de.plixo.atic.exceptions.reasons;

public final class ThreadFailure extends Failure {
    public ThreadFailure(Throwable throwable) {
        setInternalError(throwable);
    }
}
