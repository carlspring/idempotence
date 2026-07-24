package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;
import org.carlspring.testing.idempotence.config.S3fsNioIdempotenceProperties;
import org.carlspring.testing.idempotence.extension.S3TestResourceExtension;
import org.carlspring.testing.idempotence.util.S3FileSystemUtils;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * Functional test verifying that {@link S3TestResourceExtension} correctly copies
 * resources declared via {@link TestResources} into a MinIO bucket and that those
 * objects can subsequently be found via the NIO2 {@link FileSystem} API.
 *
 * <p>A MinIO instance is started automatically via Testcontainers before the tests run.
 * The test is skipped when Docker is not available.</p>
 *
 * @author carlspring
 */
@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(S3TestResourceExtension.class)
class S3BasicFunctionalityMinIOTest
{

    static final String BUCKET = "idempotence-test-resources";

    @Container
    static final MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2025-09-07T16-13-09Z");

    @BeforeAll
    static void setUp()
            throws Exception
    {
        URI s3Endpoint = URI.create(minio.getS3URL());
        String host = s3Endpoint.getHost();
        int port = s3Endpoint.getPort();

        // Configure s3fs-nio system properties for MinIO
        System.setProperty("s3fs.access.key", minio.getUserName());
        System.setProperty("s3fs.secret.key", minio.getPassword());
        System.setProperty("s3fs.region", "us-east-1");
        System.setProperty("s3fs.path.style.access", "true");
        System.setProperty("s3fs.protocol", "http");

        // Update the idempotence basedir to point at the MinIO instance
        String basedir = "s3://" + host + ":" + port + "/" + BUCKET;
        S3fsNioIdempotenceProperties props = new S3fsNioIdempotenceProperties();
        props.setBasedir(basedir);
        IdempotencePropertiesService.getInstance().setIdempotenceProperties(props);

        // Create the test bucket via the AWS SDK
        try (S3Client s3Client = S3Client.builder()
                                         .endpointOverride(URI.create(minio.getS3URL()))
                                         .credentialsProvider(StaticCredentialsProvider.create(
                                                 AwsBasicCredentials.create(minio.getUserName(),
                                                                            minio.getPassword())))
                                         .region(Region.US_EAST_1)
                                         .serviceConfiguration(S3Configuration.builder()
                                                                               .pathStyleAccessEnabled(true)
                                                                               .build())
                                         .httpClient(ApacheHttpClient.builder().build())
                                         .build())
        {
            s3Client.createBucket(b -> b.bucket(BUCKET));
        }
    }

    @AfterAll
    static void tearDown()
    {
        // Close the S3 filesystem to release resources
        String basedir = IdempotencePropertiesService.getInstance()
                                                     .getIdempotenceProperties()
                                                     .getBasedir();
        try
        {
            FileSystem s3fs = S3FileSystemUtils.getOrCreateS3FileSystem(URI.create(basedir));
            s3fs.close();
        }
        catch (Exception ignored)
        {
        }

        // Restore the default idempotence properties
        IdempotencePropertiesService.getInstance().setIdempotenceProperties(new S3fsNioIdempotenceProperties());

        // Remove s3fs system properties set for MinIO
        System.clearProperty("s3fs.access.key");
        System.clearProperty("s3fs.secret.key");
        System.clearProperty("s3fs.region");
        System.clearProperty("s3fs.path.style.access");
        System.clearProperty("s3fs.protocol");
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
        Path s3File = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityMinIOTest-testSingleFile/foo.txt");

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
        Path s3File = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityMinIOTest-testWithPatterns/nested/dir/foo.txt");

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

        Path s3File1 = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityMinIOTest-testMultipleWithPatterns/foo.txt");
        Assertions.assertTrue(Files.exists(s3File1), "Test resource file should exist in S3!");

        Path s3File2 = s3fs.getPath(baseUri.getPath() + "/S3BasicFunctionalityMinIOTest-testMultipleWithPatterns/nested/dir/foo.txt");
        Assertions.assertTrue(Files.exists(s3File2), "Test resource file should exist in S3!");
    }

}
