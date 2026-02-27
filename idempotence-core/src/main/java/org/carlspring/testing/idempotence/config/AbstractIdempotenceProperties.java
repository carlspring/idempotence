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

    private String separator = System.getProperty("org.carlspring.testing.idempotence.target.separator", "-");


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

    @Override
    public String getSeparator()
    {
        return separator;
    }

    @Override
    public void setSeparator(String separator)
    {
        if (separator == null || separator.isEmpty())
        {
            throw new IllegalArgumentException("Separator must not be null or empty.");
        }
        this.separator = separator;
    }

}
