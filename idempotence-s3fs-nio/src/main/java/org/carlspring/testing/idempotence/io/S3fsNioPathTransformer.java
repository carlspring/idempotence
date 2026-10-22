package org.carlspring.testing.idempotence.io;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * S3-specific implementation of {@link PathTransformer}.
 * <p>
 * Relativizes classpath resource paths so that only the meaningful portion
 * (relative to the classpath root) is used when constructing destination paths in S3.
 * Handles both Maven ({@code target/test-classes}) and Gradle
 * ({@code build/resources/test}, {@code build/classes/java/test}) build output directories.
 * </p>
 *
 * @author carlspring
 */
public class S3fsNioPathTransformer
        implements PathTransformer
{

    private static final String MAVEN_TEST_CLASSES = "target/test-classes";

    private static final String GRADLE_RESOURCES_TEST = "build/resources/test";

    private static final String GRADLE_CLASSES_TEST = "build/classes/java/test";


    /**
     * Creates a new instance of {@link S3fsNioPathTransformer}.
     */
    public S3fsNioPathTransformer()
    {
    }

    /**
     * At present, this is a no-op method.
     *
     * @param basePath     the base path (unused)
     * @param path         the resource path to transform
     * @return the unchanged resource path
     */
    @Override
    public Path transform(Path basePath, Path path)
    {
        return path;
    }

    /**
     * Relativizes the path based on known Maven and Gradle build output directory patterns.
     *
     * @param path the path to relativize
     * @return the relativized path, with any known build output directory prefix removed
     */
    @Override
    public Path relativize(Path path)
    {
        String pathString = path.toString();

        if (pathString.contains(MAVEN_TEST_CLASSES))
        {
            return Paths.get(pathString.substring(pathString.indexOf(MAVEN_TEST_CLASSES) +
                                                  MAVEN_TEST_CLASSES.length() + 1));
        }
        else if (pathString.contains(GRADLE_RESOURCES_TEST))
        {
            return Paths.get(pathString.substring(pathString.indexOf(GRADLE_RESOURCES_TEST) +
                                                  GRADLE_RESOURCES_TEST.length() + 1));
        }
        else if (pathString.contains(GRADLE_CLASSES_TEST))
        {
            return Paths.get(pathString.substring(pathString.indexOf(GRADLE_CLASSES_TEST) +
                                                  GRADLE_CLASSES_TEST.length() + 1));
        }
        else
        {
            return path;
        }
    }

}
