package org.carlspring.testing.idempotence.io;

import org.carlspring.testing.idempotence.annotation.TestResource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author carlspring
 */
@Disabled
class ResourceCopierGradleTest
{


    @Test
    void testResourceCopier()
            throws IOException, URISyntaxException
    {
        ResourceCopier copier = new ResourceCopier("build/resources/test/foo.txt");
        copier.copyResources(new TestResource[] { getTestResource("classpath:/**/foo.txt")}, null);
    }

    public TestResource getTestResource(String source)
    {
        return new TestResource() {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return null;
            }

            @Override
            public String source()
            {
                return source;
            }

            @Override
            public String destinationDir()
            {
                return "";
            }

            @Override
            public boolean flatten()
            {
                return false;
            }

        };
    }

}
