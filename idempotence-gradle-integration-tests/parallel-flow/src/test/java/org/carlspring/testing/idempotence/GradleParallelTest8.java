package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.extension.TestResourceExtension;
import org.carlspring.testing.idempotence.util.TestInvocationDetails;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.carlspring.testing.idempotence.util.TestMethodService.getTestInvocationDetails;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A concurrently-initialised test classes that verify {@link TestResourceExtension}
 * (and, transitively, {@code IdempotencePropertiesService#getInstance()}) behaves correctly when
 * multiple test classes are loaded and run in parallel Gradle test workers.
 *
 * @author carlspring
 */
@ExtendWith(TestResourceExtension.class)
class GradleParallelTest8
{

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile()
    {
        TestInvocationDetails details = getTestInvocationDetails();

        File testResource = new File(details.getPathToMethodTestResources(), "foo.txt");

        assertTrue(testResource.exists(), "Test resource file '" + testResource.getAbsolutePath() + "' should exist!");
    }

    @Test
    @TestResources(@TestResource(source = "classpath*:/**/foo.txt"))
    void testWithPatterns()
    {
        TestInvocationDetails details = getTestInvocationDetails();

        File testResource = new File(details.getPathToMethodTestResources(), "nested/dir/foo.txt");

        assertTrue(testResource.exists(), "Test resource file '" + testResource.getAbsolutePath() + "' should exist!");
    }

}
