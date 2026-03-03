package org.carlspring.testing.idempotence.io;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.config.PathTransformerService;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.carlspring.testing.idempotence.util.S3FileSystemUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Copies test resources from the classpath to an S3 bucket using the
 * <a href="https://github.com/carlspring/s3fs-nio">s3fs-nio</a> library.
 * <p>
 * This copier resolves the S3 {@link FileSystem} from the configured base URI and
 * writes each resource using the NIO2 {@link Files#copy(java.io.InputStream, Path, java.nio.file.CopyOption...)} API.
 * </p>
 *
 * @author carlspring
 */
public class S3ResourceCopier
{

    private static final Logger logger = LoggerFactory.getLogger(S3ResourceCopier.class);

    private final PathTransformer pathTransformer = PathTransformerService.getInstance().getPathTransformer();

    private final String resourceBaseUri;


    /**
     * Creates a new instance of {@link S3ResourceCopier}.
     *
     * @param resourceBaseUri the S3 base URI to which test resources will be copied
     *                        (e.g. {@code s3:///my-bucket/test-resources})
     */
    public S3ResourceCopier(String resourceBaseUri)
    {
        this.resourceBaseUri = resourceBaseUri;
    }

    /**
     * Copies the given test resources to the specified directory within the S3 base URI.
     *
     * @param testResources   the array of {@link TestResource} annotations describing the resources to copy
     * @param testResourceDir the relative directory within the base URI where resources should be placed
     * @throws IOException if an I/O error occurs during copying
     */
    public void copyResources(TestResource[] testResources,
                              String testResourceDir)
            throws IOException
    {
        URI baseUri = URI.create(resourceBaseUri);
        FileSystem s3FileSystem = S3FileSystemUtils.getOrCreateS3FileSystem(baseUri);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (TestResource testResource : testResources)
        {
            logger.debug("testResource: {}", testResource.source());

            String sourcePath = testResource.source();

            Resource[] resources = resolver.getResources(sourcePath);

            for (Resource resource : resources)
            {
                try
                {
                    String relativePath = resolveRelativePath(testResource, resource);

                    Path s3DestDir = buildS3DestinationDir(s3FileSystem, baseUri, testResourceDir, testResource);
                    Path s3DestFile = s3DestDir.resolve(relativePath);

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("relativePath: {}", relativePath);
                        logger.debug("s3DestDir:    {}", s3DestDir);
                        logger.debug("s3DestFile:   {}", s3DestFile);
                    }

                    createParentDirectories(s3DestFile);

                    logger.debug("Copying {} to {}...", resource.getDescription(), s3DestFile);

                    Files.copy(resource.getInputStream(),
                               s3DestFile,
                               StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private String resolveRelativePath(TestResource testResource,
                                       Resource resource)
            throws IOException
    {
        if (resource instanceof ClassPathResource classPathResource)
        {
            if (testResource.destinationDir().isEmpty())
            {
                return pathTransformer.relativize(classPathResource.getFile().toPath()).toString();
            }
            else
            {
                return pathTransformer.relativize(
                        classPathResource.getFile().toPath().getFileName()).toString();
            }
        }
        else
        {
            if (testResource.destinationDir().isEmpty())
            {
                return pathTransformer.relativize(resource.getFile().toPath()).toString();
            }
            else
            {
                return pathTransformer.relativize(
                        resource.getFile().toPath().getFileName()).toString();
            }
        }
    }

    private Path buildS3DestinationDir(FileSystem s3FileSystem,
                                       URI baseUri,
                                       String testResourceDir,
                                       TestResource testResource)
    {
        String basePath = baseUri.getPath();

        String destDirPath = !testResource.destinationDir().isEmpty() ?
                             basePath + "/" + testResourceDir + "/" + testResource.destinationDir() :
                             basePath + "/" + testResourceDir;

        return s3FileSystem.getPath(destDirPath);
    }

    private void createParentDirectories(Path path)
    {
        Path parent = path.getParent();
        if (parent != null)
        {
            try
            {
                Files.createDirectories(parent);
            }
            catch (IOException e)
            {
                logger.debug("Could not create parent directories for {}: {}", path, e.getMessage());
            }
        }
    }

}
