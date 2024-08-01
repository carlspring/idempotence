package org.carlspring.testing.idempotence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author carlspring
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,
          ElementType.TYPE })
public @interface TestResources
{

    TestResource[] value();

}
