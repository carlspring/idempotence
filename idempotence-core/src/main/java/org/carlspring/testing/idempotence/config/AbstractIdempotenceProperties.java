package org.carlspring.testing.idempotence.config;

/**
 * Abstract base implementation of {@link IdempotenceProperties} that reads
 * the {@code useFullyQualifiedClassNamePrefixes} setting from a system property.
 *
 * @author carlspring
 */
public abstract class AbstractIdempotenceProperties
    implements IdempotenceProperties
{

    private boolean useFullyQualifiedClassNamePrefixes = Boolean.parseBoolean(System.getProperty(
            "org.carlspring.testing.idempotence.useFullyQualifiedClassNamePrefixes"));


    /**
     * Creates a new instance of {@link AbstractIdempotenceProperties}.
     */
    public AbstractIdempotenceProperties()
    {
    }

    @Override
    public boolean useFullyQualifiedNamePrefixes()
    {
        return useFullyQualifiedClassNamePrefixes;
    }

    @Override
    public void setUseFullyQualifiedClassNamePrefixes(boolean useFullyQualifiedClassNamePrefixes)
    {
        this.useFullyQualifiedClassNamePrefixes = useFullyQualifiedClassNamePrefixes;
    }

}
