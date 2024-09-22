package org.carlspring.testing.idempotence.annotation;

/**
 * @author carlspring
 */
public @interface TestResource
{

    String source();

    String dest() default "";

    boolean flatten() default false;

}
