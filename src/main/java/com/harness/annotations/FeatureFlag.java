package com.harness.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation
 *
 * @author dtamboli
 */
@Target( {ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureFlag {

    /**
     * Flag Name
     * @return
     */
    String name();

    /**
     * Default Value
     *
     * @return
     */
    boolean defaultValue() default false;

    /**
     * Expected Value
     *
     * @return
     */
    boolean expectedValue() default true;
}
