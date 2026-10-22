package org.carlspring.testing.idempotence.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;

/**
 * Utility class for working with S3 {@link FileSystem} instances via the s3fs-nio provider.
 *
 * @author carlspring
 */
public final class S3FileSystemUtils
{

    private S3FileSystemUtils()
    {
    }

    /**
     * Returns an existing S3 {@link FileSystem} for the root URI derived from the given base URI,
     * or creates and registers a new one if none exists yet.
     * <p>
     * The root URI is constructed from the scheme, optional host, and optional port of the provided
     * URI (e.g. {@code s3:///} or {@code s3://localhost:9090/}).
     * </p>
     *
     * @param baseUri the S3 base URI (e.g. {@code s3:///my-bucket/prefix})
     * @return the S3 {@link FileSystem}
     * @throws IOException if an I/O error occurs while creating a new {@link FileSystem}
     */
    public static FileSystem getOrCreateS3FileSystem(URI baseUri)
            throws IOException
    {
        URI s3RootUri = buildRootUri(baseUri);
        try
        {
            return FileSystems.getFileSystem(s3RootUri);
        }
        catch (java.nio.file.FileSystemNotFoundException e)
        {
            return FileSystems.newFileSystem(s3RootUri, Collections.emptyMap());
        }
    }

    private static URI buildRootUri(URI baseUri)
    {
        StringBuilder sb = new StringBuilder(baseUri.getScheme()).append("://");

        if (baseUri.getHost() != null)
        {
            sb.append(baseUri.getHost());
        }

        if (baseUri.getPort() != -1)
        {
            sb.append(":").append(baseUri.getPort());
        }

        sb.append("/");

        return URI.create(sb.toString());
    }

}
