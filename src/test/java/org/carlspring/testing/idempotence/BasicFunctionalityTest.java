package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.extension.TestResourceExtension;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author carlspring
 */
@ExtendWith(TestResourceExtension.class)
class BasicFunctionalityTest
{

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile()
    {
        File testResource = new File("target/test-resources/BasicFunctionalityTest-testSingleFile/foo.txt");

        assertTrue(testResource.exists(), "Test resource file should exist!");
    }

    @Test
    @TestResources(@TestResource(source = "classpath:/**/foo.txt"))
    void testWithPatterns()
    {
        File testResource = new File("target/test-resources/BasicFunctionalityTest-testWithPatterns/nested/dir/foo.txt");

        assertTrue(testResource.exists(), "Test resource file should exist!");
    }

    @Test
    @TestResources({ @TestResource(source = "classpath:/foo.txt"),
                     @TestResource(source = "classpath:/**/foo.txt")} )
    void testMultipleWithPatterns()
    {
        File testResource1 = new File("target/test-resources/BasicFunctionalityTest-testMultipleWithPatterns/foo.txt");

        assertTrue(testResource1.exists(), "Test resource file should exist!");

        File testResource2 = new File("target/test-resources/BasicFunctionalityTest-testMultipleWithPatterns/nested/dir/foo.txt");

        assertTrue(testResource2.exists(), "Test resource file should exist!");
    }

}
