package org.carlspring.testing.idempotence.io;

import java.nio.file.Path;

/**
 * Defines the contract for transforming resource paths during the copy process.
 *
 * @author carlspring
 */
public interface PathTransformer
{


    /**
     * Transforms the given resource path relative to the base path.
     *
     * @param basePath     the base path used as the reference for transformation
     * @param resourcePath the resource path to transform
     * @return the transformed path
     */
    Path transform(Path basePath, Path resourcePath);

    /**
     * This method will attempt to relativize the path based on known patterns.
     *
     * @param path the path to relativize
     * @return the relativized path
     */
    Path relativize(Path path);

}
