package org.carlspring.testing.idempotence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for {@link TestResource} that allows multiple test resources to be declared
 * on a single test method or class.
 *
 * @author carlspring
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,
          ElementType.TYPE })
public @interface TestResources
{

    /**
     * The array of {@link TestResource} annotations.
     *
     * @return the array of test resources
     */
    TestResource[] value();

}
