package org.carlspring.testing.idempotence.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Gradle-specific implementation of {@link PathTransformer}.
 *
 * @author carlspring
 */
public class GradlePathTransformer
        implements PathTransformer
{

    private static Logger logger = LoggerFactory.getLogger(GradlePathTransformer.class);


    /**
     * Transforms the resource path for the Gradle build output directory structure.
     *
     * @param basePath     the base path used as the reference for transformation
     * @param resourcePath the resource path to transform
     * @return the transformed path adjusted for the Gradle {@code build/test-resources} directory
     */
    @Override
    public Path transform(Path basePath, Path resourcePath)
    {
        /*
            ├─ foo.txt
            └─ nested
               └─ dir
                  └─ foo.txt
        */

        String basePathString = basePath.toString();
        String resourcePathString = resourcePath.toString();
        String relativePathString = basePath.relativize(resourcePath).normalize().toString();

        String transformedPathString = resourcePathString;

        logger.debug("basePathString:        {}", basePathString);
        logger.debug("resourcePathString:    {}", resourcePathString);
        logger.debug("relativePathString:    {}", relativePathString);


        if (basePathString.contains("build/resources/test"))
        {
            transformedPathString = basePathString.replaceAll("build/resources/test", "build/test-resources") + File.separatorChar + relativePathString;
        }
        else if (basePathString.contains("build/classes/java/test"))
        {
            transformedPathString = basePathString.replaceAll("build/classes/java/test", "build/test-resources") + File.separatorChar + relativePathString;
        }
//        if (resourcePathString.contains("target/test-classes"))
//        {
//            // This should never really happen outside the scope of the tests which run in Maven
//            transformedPathString = basePathString.replaceAll("target/test-classes", "build/test-resources") + File.separatorChar + relativePathString;;
//        }

        Path path = Paths.get(transformedPathString);

        logger.debug("transformedPathString: {}", transformedPathString);

        return path;
    }

    /**
     * Relativizes the path based on known Gradle build output directory patterns.
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
            return Paths.get(pathString.substring(pathString.indexOf("build/resources/test") +
                                                  "build/resources/test".length() + 1));
        }
        // Just a stupid workaround for the tests which run in Maven
        else if (pathString.contains("target/test-classes"))
        {
            return Paths.get(pathString.substring(pathString.indexOf("target/test-classes") +
                                                  "target/test-classes".length() + 1));
        }
        else if (path.startsWith("build/classes/java/test"))
        {
            return Paths.get(pathString.substring(pathString.indexOf("build/classes/java/test") +
                                                  "build/classes/java/test".length() + 1));
        }
        else
        {
            return path;
        }
    }

}
