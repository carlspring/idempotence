package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.extension.TestResourceExtension;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Third of three concurrently-initialised test classes that verify {@link TestResourceExtension}
 * (and, transitively, {@code IdempotencePropertiesService#getInstance()}) behaves correctly when
 * multiple test classes are loaded and run in parallel Gradle test workers.
 *
 * @author carlspring
 */
@ExtendWith(TestResourceExtension.class)
class GradleParallelTestC
{

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile()
    {
        File testResource = new File("build/test-resources/GradleParallelTestC/testSingleFile/foo.txt");

        assertTrue(testResource.exists(), "Test resource file '" + testResource.getAbsolutePath() + "' should exist!");
    }

    @Test
    @TestResources(@TestResource(source = "classpath*:/**/foo.txt"))
    void testWithPatterns()
    {
        File testResource = new File("build/test-resources/GradleParallelTestC/testWithPatterns/nested/dir/foo.txt");

        assertTrue(testResource.exists(), "Test resource file '" + testResource.getAbsolutePath() + "' should exist!");
    }

}
