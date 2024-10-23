package org.carlspring.testing.idempotence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author carlspring
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WithDbSchema
{

    /**
     * The name of the schema to use.
     *
     * @return The name of the schema to use.
     */
    String value();

}
