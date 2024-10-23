package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.extension.TestResourceExtension;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author carlspring
 */
@ExtendWith(TestResourceExtension.class)
class GradleBasicFunctionalityTest
{

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile()
    {
        File testResource = new File("build/test-resources/GradleBasicFunctionalityTest-testSingleFile/foo.txt");

        Assertions.assertTrue(testResource.exists(), "Test resource file should exist!");
    }

    @Test
    @TestResources(@TestResource(source = "classpath*:/**/foo.txt"))
    void testWithPatterns()
    {
        File testResource = new File("build/test-resources/GradleBasicFunctionalityTest-testWithPatterns/nested/dir/foo.txt");

        Assertions.assertTrue(testResource.exists(), "Test resource file should exist!");
    }

    @Test
    @TestResources({ @TestResource(source = "classpath:/foo.txt"),
                     @TestResource(source = "classpath*:/**/foo.txt")} )
    void testMultipleWithPatterns()
    {
        File testResource1 = new File("build/test-resources/GradleBasicFunctionalityTest-testMultipleWithPatterns/foo.txt");

        Assertions.assertTrue(testResource1.exists(), "Test resource file should exist!");

        File testResource2 = new File("build/test-resources/GradleBasicFunctionalityTest-testMultipleWithPatterns/nested/dir/foo.txt");

        Assertions.assertTrue(testResource2.exists(), "Test resource file should exist!");
    }

}
