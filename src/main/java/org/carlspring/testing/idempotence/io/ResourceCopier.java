package org.carlspring.testing.idempotence.io;

import org.carlspring.testing.idempotence.annotation.TestResource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ResourceCopier
{

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
            destinationDir.mkdirs();
        }

        for (TestResource testResource : testResources)
        {
            String sourcePath = testResource.source();
            boolean flatten = testResource.flatten();

            Resource[] resources = resolver.getResources(sourcePath);
            for (Resource resource : resources)
            {
                String relativePath = flatten ? resource.getFilename() : getRelativePath(resource, sourcePath);
                File destFile = new File(destinationDir, relativePath);
                destFile.getParentFile().mkdirs();
                Files.copy(resource.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private String getRelativePath(Resource resource, String sourcePath)
            throws IOException, URISyntaxException
    {
        Path resourcePath = Paths.get(resource.getURI());
        Path basePath = Paths.get(this.getClass().getResource("/").toURI());

        return basePath.relativize(resourcePath).toString();
    }
}
