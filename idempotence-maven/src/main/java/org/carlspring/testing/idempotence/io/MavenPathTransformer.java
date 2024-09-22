package org.carlspring.testing.idempotence.io;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author carlspring
 */
public class MavenPathTransformer
        implements PathTransformer
{

    /**
     * At present, this is a no-op method.
     *
     * @param path
     * @return
     */
    @Override
    public Path transform(Path basePath, Path path)
    {
        return path;
    }

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
