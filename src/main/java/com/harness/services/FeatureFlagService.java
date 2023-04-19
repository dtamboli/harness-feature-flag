package com.harness.services;

import com.harness.annotations.FeatureFlag;
import org.springframework.stereotype.Service;

/**
 * Interface to get value of feature flag
 *
 * @author dtamboli
 */
@Service
public interface FeatureFlagService {

    /**
     * Gets Value based on feature flag name
     *
     * @param featureFlagName
     * @param defaultValue
     * @return
     */
    boolean isFeatureFlagSet(String featureFlagName, boolean defaultValue);
}
