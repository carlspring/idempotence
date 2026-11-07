package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.extension.TestResourceExtension;
import org.carlspring.testing.idempotence.util.TestInvocationDetails;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import static org.carlspring.testing.idempotence.util.TestMethodService.getTestInvocationDetails;

/**
 * @author carlspring
 */
@ExtendWith(TestResourceExtension.class)
class MavenBasicFunctionalityTest
{

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile()
    {
        TestInvocationDetails details = getTestInvocationDetails();

        File testResource = new File(details.getPathToMethodTestResources(), "foo.txt");

        Assertions.assertTrue(testResource.exists(), "Test resource file should exist!");
    }

    @Test
    @TestResources(@TestResource(source = "classpath:/**/foo.txt"))
    void testWithPatterns()
    {
        TestInvocationDetails details = getTestInvocationDetails();

        File testResource = new File(details.getPathToMethodTestResources(), "nested/dir/foo.txt");

        Assertions.assertTrue(testResource.exists(), "Test resource file should exist!");
    }

    @Test
    @TestResources({ @TestResource(source = "classpath:/foo.txt"),
                     @TestResource(source = "classpath:/**/foo.txt")} )
    void testMultipleWithPatterns()
    {
        TestInvocationDetails details = getTestInvocationDetails();

        File testResource1 = new File(details.getPathToMethodTestResources(), "foo.txt");

        Assertions.assertTrue(testResource1.exists(), "Test resource file should exist!");

        File testResource2 = new File(details.getPathToMethodTestResources(), "nested/dir/foo.txt");

        Assertions.assertTrue(testResource2.exists(), "Test resource file should exist!");
    }

}
