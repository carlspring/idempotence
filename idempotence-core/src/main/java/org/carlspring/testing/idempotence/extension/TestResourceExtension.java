package org.carlspring.testing.idempotence.extension;

import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;
import org.carlspring.testing.idempotence.io.ResourceCopier;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestResourceExtension
        extends Object
        implements BeforeEachCallback, BeforeAllCallback
{

    private ResourceCopier resourceCopier = new ResourceCopier(IdempotencePropertiesService.getInstance()
                                                                                           .getIdempotenceProperties()
                                                                                           .getBasedir());


    public TestResourceExtension()
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
     * This method will wipe out any old resources in the test method directory ensuring a clean isolated new
     * environment for the current execution.
     */
    private void cleanUp(ExtensionContext context)
            throws IOException
    {
        String testResourceDir = getTestResourceDirectory(context);
        Path testResourcesDirAsPath = Paths.get(IdempotencePropertiesService.getInstance()
                                                                            .getIdempotenceProperties()
                                                                            .getBasedir(),
                                                testResourceDir);

        if (!Files.exists(testResourcesDirAsPath) || !Files.isDirectory(testResourcesDirAsPath))
        {
            return;
        }

        Files.walkFileTree(testResourcesDirAsPath, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs)
                    throws IOException
            {
                System.out.println("Removing '" + file + "'...");

                Files.deleteIfExists(file);

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir,
                                                      IOException exc)
                    throws IOException
            {
                if (!dir.equals(testResourcesDirAsPath))
                {
                    System.out.println("Removing '" + dir + "'...");
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
            String testResourceDir = getTestResourceDirectory(context);
            resourceCopier.copyResources(testResources.value(), testResourceDir);
        }
    }

    private String getTestResourceDirectory(ExtensionContext context)
    {
        String className = context.getRequiredTestClass().getSimpleName();
        Optional<Method> testMethod = context.getTestMethod();

        String path = testMethod.map(method -> String.format("%s-%s",
                                                                     className,
                                                                     method.getName()))
                                .orElse(className);

        return path;
    }

}
