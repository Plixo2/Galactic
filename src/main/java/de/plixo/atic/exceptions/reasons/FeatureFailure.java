package de.plixo.atic.exceptions.reasons;

public final class FeatureFailure extends Failure {
    public FeatureFailure(String feature) {
        setMessage(feature + " is missing or not implemented");

    }
}
