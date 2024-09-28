package org.carlspring.testing.idempotence.io;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.config.PathTransformerService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ResourceCopier
{

    private static Logger logger = LoggerFactory.getLogger(ResourceCopier.class);

    private PathTransformer pathTransformer = PathTransformerService.getInstance().getPathTransformer();

    private final String resourceBasePath;


    public ResourceCopier(String resourceBasePath)
    {
        this.resourceBasePath = resourceBasePath;
    }

    public void copyResources(TestResource[] testResources, String testResourceDir)
            throws IOException, URISyntaxException
    {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        File destinationDir = new File(resourceBasePath, testResourceDir);
        if (!destinationDir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            destinationDir.mkdirs();
        }

        for (TestResource testResource : testResources)
        {
            logger.debug("testResource: " + testResource.source());

            String sourcePath = testResource.source();
            boolean flatten = testResource.flatten();

            Resource[] resources = resolver.getResources(sourcePath);

            for (Resource resource : resources)
            {
                try
                {
                    File file = resource.getFile();

                    String targetDir = resourceBasePath + "/" + testResourceDir;
                    String relativePath = null;
                    if (resource instanceof ClassPathResource)
                    {
                        ClassPathResource classPathResource = (ClassPathResource) resource;
                        relativePath = pathTransformer.relativize(classPathResource.getFile().toPath()).toString();
                    }
                    else
                    {
                        FileSystemResource fileSystemResource = (FileSystemResource) resource;
                        relativePath = pathTransformer.relativize(fileSystemResource.getFile().toPath()).toString();
                    }

                    @SuppressWarnings("DataFlowIssue")
                    File destFile = new File(destinationDir, relativePath);

                    if (logger.isDebugEnabled())
                    {
                        // This is really not the right way to be handling this, but gets you the required info
                        // with less of an effort than having to dig through the log file and it's easier to read.
                        System.out.println("relativePath: " + relativePath);
                        System.out.println("targetDir:    " + targetDir);
                        System.out.println("destFile:     " + destFile);
                    }

                    if (!destFile.getParentFile().exists())
                    {
                        logger.debug("Creating parent directories for {}...", destFile.getParentFile());

                        //noinspection ResultOfMethodCallIgnored
                        destFile.getParentFile().mkdirs();
                    }

                    logger.debug("Copying {} to {}...", file.getPath(), destFile.getPath());

                    Files.copy(resource.getInputStream(),
                               destFile.toPath(),
                               StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
