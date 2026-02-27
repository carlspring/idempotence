package org.carlspring.testing.idempotence.config;

import org.springframework.stereotype.Service;

/**
 * Maven-specific implementation of {@link AbstractIdempotenceProperties} that uses
 * {@code target/test-resources} as the default base directory.
 *
 * @author carlspring
 */
@Service
public class MavenIdempotenceProperties
        extends AbstractIdempotenceProperties
{

    private String basedir = System.getProperty("org.carlspring.testing.idempotence.basedir") != null ?
                             System.getProperty("org.carlspring.testing.idempotence.basedir") :
                             "target/test-resources";

    /**
     * Creates a new instance of {@link MavenIdempotenceProperties}.
     */
    public MavenIdempotenceProperties()
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
