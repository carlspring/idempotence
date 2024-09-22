package org.carlspring.testing.idempotence.config;

import org.springframework.stereotype.Service;

/**
 * @author carlspring
 */
@Service
public class GradleIdempotenceProperties
        extends AbstractIdempotenceProperties
{

    public String basedir = System.getProperty("org.carlspring.testing.idempotence.basedir") != null ?
                            System.getProperty("org.carlspring.testing.idempotence.basedir") :
                            "build/test-resources";

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
