package org.carlspring.testing.idempotence.config;

import org.springframework.stereotype.Service;

/**
 * Gradle-specific implementation of {@link AbstractIdempotenceProperties} that uses
 * {@code build/test-resources} as the default base directory.
 *
 * @author carlspring
 */
@Service
public class GradleIdempotenceProperties
        extends AbstractIdempotenceProperties
{

    /**
     * The base directory where test resources will be copied to. Defaults to {@code build/test-resources}
     * unless overridden by the {@code org.carlspring.testing.idempotence.basedir} system property.
     */
    public String basedir = System.getProperty("org.carlspring.testing.idempotence.basedir") != null ?
                            System.getProperty("org.carlspring.testing.idempotence.basedir") :
                            "build/test-resources";

    /**
     * Creates a new instance of {@link GradleIdempotenceProperties}.
     */
    public GradleIdempotenceProperties()
    {
    }

    @Override
    public String getBasedir()
    {
        return basedir;
    }

    @Override
    public void setBasedir(String basedir)
    {
        this.basedir = basedir;
    }

}
