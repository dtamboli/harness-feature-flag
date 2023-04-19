package com.harness.services;

import com.harness.annotations.FeatureFlag;
import com.harness.harness.HarnessClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Implementation to retrieve value of feature flag
 *
 * @author dtamboli
 */
@Service
public class FeatureFlagServiceImpl implements FeatureFlagService {

    @Autowired
    HarnessClient client;

    /**
     *
     * @inheritDoc
     */
    @Override
    public boolean isFeatureFlagSet(String featureFlagName, boolean defaultValue) {
        return client.getFeatureFlagValue(featureFlagName, defaultValue);
    }

}
