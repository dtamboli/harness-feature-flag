package com.harness.harness;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration to enable hysterix and AOP
 *
 * @author dtamboli
 */
@Configuration
@EnableCircuitBreaker
@EnableAspectJAutoProxy
@ComponentScan("com.harness")
public class HarnessConfig {
}
