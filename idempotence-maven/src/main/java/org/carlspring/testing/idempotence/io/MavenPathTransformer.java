package org.carlspring.testing.idempotence.io;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Maven-specific implementation of {@link PathTransformer}.
 *
 * @author carlspring
 */
public class MavenPathTransformer
        implements PathTransformer
{

    /**
     * Creates a new instance of {@link MavenPathTransformer}.
     */
    public MavenPathTransformer()
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
     * Relativizes the path based on known Maven build output directory patterns.
     *
     * @param path the path to relativize
     * @return the relativized path
     */
    @Override
    public Path relativize(Path path)
    {
        String pathString = path.toString();
        if (pathString.contains("build/resources/test"))
        {
            return Paths.get(pathString.substring(pathString.indexOf("target/classses") +
                                                  "target/classses".length() + 1));
        }
        // Just a stupid workaround for the tests which run in Maven
        else if (pathString.contains("target/test-classes"))
        {
            return Paths.get(pathString.substring(pathString.indexOf("target/test-classes") +
                                                  "target/test-classes".length() + 1));
        }
        else
        {
            return path;
        }
    }

}
