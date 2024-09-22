package org.carlspring.testing.idempotence.config;

import java.util.Arrays;
import java.util.ServiceLoader;

/**
 * @author carlspring
 */
public class IdempotencePropertiesService
{

    private static IdempotenceProperties idempotenceProperties;

    private static IdempotencePropertiesService instance;


    private IdempotencePropertiesService()
    {
    }

    public static IdempotencePropertiesService getInstance()
    {
        if (instance == null)
        {
            instance = new IdempotencePropertiesService();
            instance.initialize();
        }

        return instance;
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

    public IdempotenceProperties getIdempotenceProperties()
    {
        return idempotenceProperties;
    }

    public void setIdempotenceProperties(IdempotenceProperties idempotenceProperties)
    {
        this.idempotenceProperties = idempotenceProperties;
    }

}
