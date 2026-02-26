package org.carlspring.testing.idempotence.config;

import org.carlspring.testing.idempotence.io.PathTransformer;

import java.util.Arrays;
import java.util.ServiceLoader;

/**
 * Singleton service that loads and provides the {@link PathTransformer} implementation
 * via the Java {@link java.util.ServiceLoader} mechanism.
 *
 * @author carlspring
 */
public class PathTransformerService
{

    private static PathTransformer pathTransformer;

    private static PathTransformerService instance;


    private PathTransformerService()
    {
    }

    /**
     * Returns the singleton instance of {@link PathTransformerService}.
     *
     * @return the singleton instance
     */
    public static PathTransformerService getInstance()
    {
        if (instance == null)
        {
            instance = new PathTransformerService();
            instance.initialize();
        }

        return instance;
    }

    private void initialize()
    {
        ServiceLoader<PathTransformer> serviceLoader = ServiceLoader.load(PathTransformer.class);

        long count = serviceLoader.stream().count();
        if (count == 0)
        {
            throw new IllegalStateException("No PathTransformer implementation found! Please, add the respective dependency.");
        }
        else if (count > 1)
        {
            throw new IllegalStateException("Only one PathTransformer implementation can be used at a time. " +
                                            "Found " + count + "! " + Arrays.toString(serviceLoader.stream().toArray()));
        }
        else if (count == 1)
        {
            pathTransformer = serviceLoader.iterator().next();
        }
    }

    /**
     * Returns the loaded {@link PathTransformer} instance.
     *
     * @return the {@link PathTransformer} instance
     */
    public PathTransformer getPathTransformer()
    {
        return pathTransformer;
    }

    /**
     * Sets the {@link PathTransformer} instance to use.
     *
     * @param pathTransformer the {@link PathTransformer} instance to set
     */
    public void setPathTransformer(PathTransformer pathTransformer)
    {
        this.pathTransformer = pathTransformer;
    }

}
