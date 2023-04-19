package com.harness.harness;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.harness.cf.client.api.BaseConfig;
import io.harness.cf.client.api.CaffeineCache;
import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.api.FeatureFlagInitializeException;
import io.harness.cf.client.dto.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Client for Harness Feature Flag
 *
 * @author dtamboli
 */
@Component
public class HarnessClient {

    /**
     * Harness API SDK Key
     */
    @Value("${harness.sdkKey}")
    String apiKey;

    /**
     * CF Client
     */
    CfClient cfClient;

    private static final Logger logger = LoggerFactory.getLogger(HarnessClient.class);

    /**
     *
     */
    private Properties properties;

    public HarnessClient() throws Exception {
        initializeProperties();
    }

    /**
     * Initializes Properties locator to get feature flag value in case of Harness not available
     *
     * @throws IOException
     */
    private void initializeProperties() throws IOException {
        properties = new Properties();
        File file = ResourceUtils.getFile("classpath:featureflags.properties");
        InputStream stream = new FileInputStream(file);
        properties.load(stream);
    }

    /**
     * Initializes Harness CF Client (haarness SDK)
     *
     * @throws FeatureFlagInitializeException
     * @throws InterruptedException
     */
    private void initializeClient() throws FeatureFlagInitializeException, InterruptedException {
        // Create Options
        BaseConfig options = BaseConfig.builder()
                .pollIntervalInSeconds(60)
                .streamEnabled(true)
                .analyticsEnabled(true)
                .cache(new CaffeineCache(100))
                .build();

        // Create the client
        //CfClient cfClient = new CfClient(new HarnessConnector(apiKey, connectorConfig), options);
        cfClient = new CfClient(apiKey, options);
        cfClient.waitForInitialization();
    }

    /**
     * Get Feature Flag value from Harness Client
     * In case of error / circuit break - Get it from Properties file / default value
     *
     * @param featureFlagName
     * @param defaultValue
     * @return
     */
    @HystrixCommand(fallbackMethod = "fallback_featureFlag", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public boolean getFeatureFlagValue(String featureFlagName, boolean defaultValue) {
        try {
            logger.info("Trying to get the value for flag: {}", featureFlagName);
            if(StringUtils.isEmpty(apiKey)) {
                logger.debug("Harness API Key is not defined, so looking property files");
                return fallback_featureFlag(featureFlagName, defaultValue);
            }

            if (cfClient == null) {
                initializeClient();
            }
            final Target target = Target.builder()
                    .identifier("javasdk")
                    .name("JavaSDK")
                    .attribute("location", "emea")
                    .build();

            Boolean flagValue = cfClient.boolVariation(featureFlagName, target, defaultValue);
            return flagValue;
        } catch (FeatureFlagInitializeException | InterruptedException e) {
            logger.error("Error occurred while initializing Harness SDK, Error: {}", e.getMessage());
        }
        logger.info("Returning Default Feature flag: {}", featureFlagName );
        return defaultValue;
    }


    /**
     * In case of Fallback (Harness not responding), Read from Properties file
     *
     * @param featureFlagName
     * @param defaultValue
     * @return
     */
    private boolean fallback_featureFlag(String featureFlagName, boolean defaultValue) {
        logger.info("Fallback method for Harness Feature flag: {}", featureFlagName );
        if(properties != null && properties.getProperty(featureFlagName) != null) {
            boolean propertyValue = Boolean.valueOf(properties.getProperty(featureFlagName));
            logger.info("Property Value for Harness Feature flag: {}, value: {}", featureFlagName, propertyValue );
            return propertyValue;
        }
        return defaultValue;
    }


}
