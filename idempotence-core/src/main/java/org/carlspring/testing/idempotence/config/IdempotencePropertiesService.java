package org.carlspring.testing.idempotence.config;

import java.util.Arrays;
import java.util.ServiceLoader;

/**
 * Singleton service that loads and provides the {@link IdempotenceProperties} implementation
 * via the Java {@link java.util.ServiceLoader} mechanism.
 *
 * @author carlspring
 */
public class IdempotencePropertiesService
{

    private static IdempotenceProperties idempotenceProperties;

    private static volatile IdempotencePropertiesService instance;


    private IdempotencePropertiesService()
    {
    }

    /**
     * Returns the singleton instance of {@link IdempotencePropertiesService}.
     *
     * @return the singleton instance
     */
    public static IdempotencePropertiesService getInstance()
    {
        IdempotencePropertiesService localInstance = instance;
        if (localInstance == null)
        {
            synchronized (IdempotencePropertiesService.class)
            {
                localInstance = instance;
                if (localInstance == null)
                {
                    localInstance = new IdempotencePropertiesService();
                    localInstance.initialize();
                    instance = localInstance;
                }
            }
        }

        return localInstance;
    }

    private void initialize()
    {
        ServiceLoader<IdempotenceProperties> serviceLoader = ServiceLoader.load(IdempotenceProperties.class);

        long count = serviceLoader.stream().count();
        if (count == 0)
        {
            throw new IllegalStateException("No IdempotenceProperties implementation found! Please, add the respective dependency.");
        }
        else if (count > 1)
        {
            throw new IllegalStateException("Only one IdempotenceProperties implementation can be used at a time. " +
                                            "Found " + count + "! " + Arrays.toString(serviceLoader.stream().toArray()));
        }
        else if (count == 1)
        {
            idempotenceProperties = serviceLoader.iterator().next();
        }
    }

    /**
     * Returns the loaded {@link IdempotenceProperties} instance.
     *
     * @return the {@link IdempotenceProperties} instance
     */
    public IdempotenceProperties getIdempotenceProperties()
    {
        return idempotenceProperties;
    }

    /**
     * Sets the {@link IdempotenceProperties} instance to use.
     *
     * @param idempotenceProperties the {@link IdempotenceProperties} instance to set
     */
    public void setIdempotenceProperties(IdempotenceProperties idempotenceProperties)
    {
        this.idempotenceProperties = idempotenceProperties;
    }

}
