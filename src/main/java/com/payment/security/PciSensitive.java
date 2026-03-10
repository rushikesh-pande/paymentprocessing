package com.payment.security;

import java.lang.annotation.*;

/**
 * Marks a method as PCI-sensitive — triggers PCI DSS audit logging
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PciSensitive {
    String value() default "PAYMENT_OPERATION";
}
