package com.porpoise.common.key;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All methods within a class (and superclass) which should be considered for object equality should bear this annotation.
 * 
 * An annotated class can then make use of {@link BusinessKeys}, either by delegating its equals, hashCode and toString methods or by other classes making use of it
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BusinessKey {
    boolean required() default true;
}
