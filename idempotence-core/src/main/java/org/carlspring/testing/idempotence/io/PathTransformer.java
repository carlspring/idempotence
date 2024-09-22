package org.carlspring.testing.idempotence.io;

import java.nio.file.Path;

/**
 * @author carlspring
 */
public interface PathTransformer
{


    Path transform(Path basePath, Path resourcePath);

    /**
     * This method will attempt to relativize the path based on known patterns.
     *
     * @param path
     * @return
     */
    Path relativize(Path path);

}
