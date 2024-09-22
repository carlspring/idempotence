package org.carlspring.testing.idempotence.config;

import org.carlspring.testing.idempotence.io.PathTransformer;

import java.util.Arrays;
import java.util.ServiceLoader;

/**
 * @author carlspring
 */
public class PathTransformerService
{

    private static PathTransformer pathTransformer;

    private static PathTransformerService instance;


    private PathTransformerService()
    {
    }

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

    public PathTransformer getPathTransformer()
    {
        return pathTransformer;
    }

    public void setIdempotenceProperties(PathTransformer pathTransformer)
    {
        this.pathTransformer = pathTransformer;
    }

}
