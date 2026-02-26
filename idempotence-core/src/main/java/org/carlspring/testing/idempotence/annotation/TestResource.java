package org.carlspring.testing.idempotence.annotation;

/**
 * Annotation used to declare a test resource that should be copied to the test resource directory
 * before the test is executed.
 *
 * @author carlspring
 */
public @interface TestResource
{

    /**
     * The source path of the test resource (supports classpath patterns).
     *
     * @return the source path of the test resource
     */
    String source();

    /**
     * The destination directory within the test resource directory. If empty, the resource is placed
     * directly in the test resource directory.
     *
     * @return the destination directory, or an empty string to use the default location
     */
    String destinationDir() default "";

    /**
     * Whether to flatten the directory structure when copying resources.
     *
     * @return {@code true} if the directory structure should be flattened, {@code false} otherwise
     */
    boolean flatten() default false;

}
