package org.carlspring.testing.idempotence.config;

import org.springframework.stereotype.Service;

/**
 * S3-specific implementation of {@link AbstractIdempotenceProperties} that uses
 * an S3 URI as the base directory for test resources.
 * <p>
 * The default base directory is {@code s3:///idempotence-test-resources}, which points
 * to a bucket named {@code idempotence-test-resources} on the default AWS S3 endpoint.
 * Override the {@code org.carlspring.testing.idempotence.basedir} system property to
 * specify a different S3 URI (e.g. {@code s3://localhost:9090/my-bucket/prefix} for
 * a local S3-compatible server such as MinIO).
 * </p>
 *
 * @author carlspring
 */
@Service
public class S3fsNioIdempotenceProperties
        extends AbstractIdempotenceProperties
{

    private String basedir = System.getProperty("org.carlspring.testing.idempotence.basedir") != null ?
                             System.getProperty("org.carlspring.testing.idempotence.basedir") :
                             "s3:///idempotence-test-resources";

    /**
     * Creates a new instance of {@link S3fsNioIdempotenceProperties}.
     */
    public S3fsNioIdempotenceProperties()
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
