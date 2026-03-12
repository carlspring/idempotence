package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.extension.TestResourceExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test verifying that the hyphen separator produces paths of the form
 * {@code build/test-resources/ClassName-methodName/...} when the system property
 * {@code org.carlspring.testing.idempotence.target.separator} is set to {@code -}.
 *
 * @author carlspring
 */
@ExtendWith(TestResourceExtension.class)
class GradleHyphenSeparatorTest
{

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile()
    {
        File testResource = new File("build/test-resources/GradleHyphenSeparatorTest-testSingleFile/foo.txt");

        assertTrue(testResource.exists(), "Test resource file '" + testResource.getAbsolutePath() + "' should exist!");
    }

    @Test
    @TestResources(@TestResource(source = "classpath*:/**/foo.txt"))
    void testWithPatterns()
    {
        File testResource = new File("build/test-resources/GradleHyphenSeparatorTest-testWithPatterns/nested/dir/foo.txt");

        assertTrue(testResource.exists(), "Test resource file '" + testResource.getAbsolutePath() + "' should exist!");
    }

    @Test
    @TestResources({ @TestResource(source = "classpath:/foo.txt"),
                     @TestResource(source = "classpath*:/**/foo.txt")} )
    void testMultipleWithPatterns()
    {
        File testResource1 = new File("build/test-resources/GradleHyphenSeparatorTest-testMultipleWithPatterns/foo.txt");

        assertTrue(testResource1.exists(), "Test resource file '" + testResource1.getAbsolutePath() + "' should exist!");

        File testResource2 = new File("build/test-resources/GradleHyphenSeparatorTest-testMultipleWithPatterns/nested/dir/foo.txt");

        assertTrue(testResource2.exists(), "Test resource file '" + testResource2.getAbsolutePath() + "' should exist!");
    }

}
