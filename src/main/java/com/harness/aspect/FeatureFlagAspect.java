package com.harness.aspect;

import com.harness.annotations.FeatureFlag;
import com.harness.exceptions.FeatureNotEnabledException;
import com.harness.harness.HarnessClient;
import com.harness.services.FeatureFlagService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Spring Aspect to validate feature flag value
 *
 * @author dtamboli
 */
@Aspect
@Component
public class FeatureFlagAspect {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagAspect.class);

    private FeatureFlagService featureFlagService;

    public FeatureFlagAspect(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    /**
     * This method validates if feature flag value aligns with expected value or not
     * By Default, Expected Value is true
     *
     * @param joinPoint
     * @param featureFlag
     */
    @Before("@annotation(featureFlag)")
    public void checkFeatureFlag(JoinPoint joinPoint, FeatureFlag featureFlag) {
        boolean harnessFeatureFlag = featureFlagService.isFeatureFlagSet(featureFlag.name(), featureFlag.defaultValue());
        logger.debug("Harness Feature Flag Name: {}, Value: {}, Expected: {}", featureFlag.name(), harnessFeatureFlag,
                featureFlag.expectedValue());
        if (harnessFeatureFlag != featureFlag.expectedValue()) {
            throw new FeatureNotEnabledException();
        }
    }


}
