package org.carlspring.testing.idempotence.extension;

import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;
import org.carlspring.testing.idempotence.io.S3ResourceCopier;
import org.carlspring.testing.idempotence.util.S3FileSystemUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Jupiter extension that copies test resources to an S3 bucket before each test method
 * is executed. It also cleans up the S3 "directory" before copying to ensure a clean,
 * isolated environment.
 * <p>
 * Uses <a href="https://github.com/carlspring/s3fs-nio">s3fs-nio</a> to interact with S3 via
 * the Java NIO2 API.
 * </p>
 * <p>
 * Configure the target S3 location by setting the
 * {@code org.carlspring.testing.idempotence.s3fs.basedir} system property to an S3 URI
 * (e.g. {@code s3:///my-bucket/test-resources} or
 * {@code s3://localhost:9090/my-bucket/test-resources} for a local MinIO instance).
 * </p>
 *
 * @author carlspring
 */
public class S3TestResourceExtension
        implements BeforeEachCallback, BeforeAllCallback
{

    private static final Logger logger = LoggerFactory.getLogger(S3TestResourceExtension.class);


    /**
     * Creates a new instance of {@link S3TestResourceExtension}.
     */
    public S3TestResourceExtension()
    {
    }

    @Override
    public void beforeAll(ExtensionContext context)
            throws Exception
    {
    }

    @Override
    public void beforeEach(ExtensionContext context)
            throws Exception
    {
        cleanUp(context);
        copyResources(context.getRequiredTestMethod().getAnnotation(TestResources.class), context);
    }

    /**
     * Removes any existing objects in the S3 "directory" for the current test method,
     * ensuring a clean isolated environment for the current execution.
     */
    private void cleanUp(ExtensionContext context)
            throws IOException
    {
        String basedir = IdempotencePropertiesService.getInstance()
                                                     .getIdempotenceProperties()
                                                     .getBasedir();
        String testResourceDir = getTestResourceDirectory(context);

        URI baseUri = URI.create(basedir);
        FileSystem s3FileSystem = S3FileSystemUtils.getOrCreateS3FileSystem(baseUri);

        Path s3TestDir = s3FileSystem.getPath(baseUri.getPath() + "/" + testResourceDir);

        if (!Files.exists(s3TestDir))
        {
            return;
        }

        Files.walkFileTree(s3TestDir, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs)
                    throws IOException
            {
                logger.debug("Removing '{}'...", file);

                Files.deleteIfExists(file);

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir,
                                                      IOException exc)
                    throws IOException
            {
                if (!dir.equals(s3TestDir))
                {
                    logger.debug("Removing '{}'...", dir);

                    Files.deleteIfExists(dir);
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void copyResources(TestResources testResources,
                               ExtensionContext context)
            throws IOException
    {
        if (testResources != null)
        {
            String basedir = IdempotencePropertiesService.getInstance()
                                                         .getIdempotenceProperties()
                                                         .getBasedir();
            String testResourceDir = getTestResourceDirectory(context);
            new S3ResourceCopier(basedir).copyResources(testResources.value(), testResourceDir);
        }
    }

    private String getTestResourceDirectory(ExtensionContext context)
    {
        String className = context.getRequiredTestClass().getSimpleName();
        Optional<Method> testMethod = context.getTestMethod();

        return testMethod.map(method -> String.format("%s-%s", className, method.getName()))
                         .orElse(className);
    }

}
