package com.harness.exceptions;

/**
 * Feature not available Exception
 *
 * @author dtamboli
 */
public class FeatureNotEnabledException extends RuntimeException {
    private static final String MESSAGE = "Feature is not enabled.";

    public FeatureNotEnabledException() {
        super(MESSAGE);
    }
}
