package org.carlspring.testing.idempotence.config;

/**
 * @author carlspring
 */
public abstract class AbstractIdempotenceProperties
    implements IdempotenceProperties
{

    private boolean useFullyQualifiedClassNamePrefixes = Boolean.parseBoolean(System.getProperty(
            "org.carlspring.testing.idempotence.useFullyQualifiedClassNamePrefixes"));


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
