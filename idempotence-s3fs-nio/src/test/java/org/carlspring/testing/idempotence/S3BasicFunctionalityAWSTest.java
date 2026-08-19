package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;
import org.carlspring.testing.idempotence.config.S3fsNioIdempotenceProperties;
import org.carlspring.testing.idempotence.extension.S3TestResourceExtension;
import org.carlspring.testing.idempotence.util.S3FileSystemUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Functional test verifying that {@link S3TestResourceExtension} correctly copies
 * resources declared via {@link TestResources} into a real AWS S3 bucket and that those
 * objects can subsequently be found via the NIO2 {@link FileSystem} API.
 *
 * <p>This test is only enabled when the {@code AWS_ACCESS_KEY_ID} environment variable
 * is set, indicating that explicit AWS credentials are available. The target S3 location
 * is configured via the {@code org.carlspring.testing.idempotence.s3fs.basedir} system
 * property (defaults to {@code s3:///idempotence-test-resources}).</p>
 *
 * <p>Prerequisites:</p>
 * <ul>
 *   <li>AWS credentials must be available (e.g. via {@code AWS_ACCESS_KEY_ID} /
 *       {@code AWS_SECRET_ACCESS_KEY} environment variables or {@code ~/.aws/credentials}).</li>
 *   <li>The target S3 bucket must already exist and the credentials must have
 *       read/write/delete permissions on it.</li>
 *   <li>The AWS region can be configured via the {@code s3fs.region} system property or
 *       the {@code AWS_DEFAULT_REGION} environment variable.</li>
 * </ul>
 *
 * @author carlspring
 */
@EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches = "\\S+")
@ExtendWith(S3TestResourceExtension.class)
class S3BasicFunctionalityAWSTest
{

    private static final Logger logger = LoggerFactory.getLogger(S3BasicFunctionalityAWSTest.class);

    @BeforeAll
    static void setUp()
    {
        // Use the default S3fsNioIdempotenceProperties which reads the basedir from the
        // org.carlspring.testing.idempotence.s3fs.basedir system property, or defaults to
        // s3:///idempotence-test-resources. AWS credentials and region are resolved via the
        // SDK's default provider chain (env vars, ~/.aws/credentials, EC2 instance profile).
        S3fsNioIdempotenceProperties props = new S3fsNioIdempotenceProperties();
        IdempotencePropertiesService.getInstance().setIdempotenceProperties(props);

        logger.info("Running AWS S3 functional tests against: {}", props.getBasedir());
    }

    @AfterAll
    static void tearDown()
    {
        String basedir = IdempotencePropertiesService.getInstance()
                                                     .getIdempotenceProperties()
                                                     .getBasedir();
        URI baseUri = URI.create(basedir);

        // Clean up all test resource directories created by this test class
        try
        {
            FileSystem s3fs = S3FileSystemUtils.getOrCreateS3FileSystem(baseUri);
            cleanDirectory(s3fs, baseUri, "S3BasicFunctionalityAWSTest-testSingleFile");
            cleanDirectory(s3fs, baseUri, "S3BasicFunctionalityAWSTest-testWithPatterns");
            cleanDirectory(s3fs, baseUri, "S3BasicFunctionalityAWSTest-testMultipleWithPatterns");
            s3fs.close();
        }
        catch (Exception e)
        {
            logger.warn("Failed to clean up S3 test resources: {}", e.getMessage());
        }

        // Restore the default idempotence properties
        IdempotencePropertiesService.getInstance().setIdempotenceProperties(new S3fsNioIdempotenceProperties());
    }

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile()
            throws Exception
    {
        String basedir = IdempotencePropertiesService.getInstance()
                                                     .getIdempotenceProperties()
                                                     .getBasedir();
        URI baseUri = URI.create(basedir);
        FileSystem s3fs = S3FileSystemUtils.getOrCreateS3FileSystem(baseUri);
        Path s3File = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityAWSTest-testSingleFile/foo.txt");

        Assertions.assertTrue(Files.exists(s3File), "Test resource file should exist in S3!");
    }

    @Test
    @TestResources(@TestResource(source = "classpath:/**/foo.txt"))
    void testWithPatterns()
            throws Exception
    {
        String basedir = IdempotencePropertiesService.getInstance()
                                                     .getIdempotenceProperties()
                                                     .getBasedir();
        URI baseUri = URI.create(basedir);
        FileSystem s3fs = S3FileSystemUtils.getOrCreateS3FileSystem(baseUri);
        Path s3File = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityAWSTest-testWithPatterns/nested/dir/foo.txt");

        Assertions.assertTrue(Files.exists(s3File), "Test resource file should exist in S3!");
    }

    @Test
    @TestResources({ @TestResource(source = "classpath:/foo.txt"),
                     @TestResource(source = "classpath:/**/foo.txt") })
    void testMultipleWithPatterns()
            throws Exception
    {
        String basedir = IdempotencePropertiesService.getInstance()
                                                     .getIdempotenceProperties()
                                                     .getBasedir();
        URI baseUri = URI.create(basedir);
        FileSystem s3fs = S3FileSystemUtils.getOrCreateS3FileSystem(baseUri);

        Path s3File1 = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityAWSTest-testMultipleWithPatterns/foo.txt");
        Assertions.assertTrue(Files.exists(s3File1), "Test resource file should exist in S3!");

        Path s3File2 = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityAWSTest-testMultipleWithPatterns/nested/dir/foo.txt");
        Assertions.assertTrue(Files.exists(s3File2), "Test resource file should exist in S3!");
    }

    private static void cleanDirectory(FileSystem s3fs,
                                       URI baseUri,
                                       String dirName)
    {
        Path s3Dir = s3fs.getPath(baseUri.getPath() + "/" + dirName);

        if (!Files.exists(s3Dir))
        {
            return;
        }

        try
        {
            Files.walkFileTree(s3Dir, new SimpleFileVisitor<>()
            {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs)
                        throws IOException
                {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                                                          IOException exc)
                        throws IOException
                {
                    if (exc != null)
                    {
                        logger.warn("Error visiting directory {}: {}", dir, exc.getMessage());
                        return FileVisitResult.CONTINUE;
                    }

                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e)
        {
            logger.warn("Failed to clean S3 directory {}: {}", dirName, e.getMessage());
        }
    }

}
