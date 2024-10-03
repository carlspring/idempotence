package org.carlspring.testing.idempotence.util;

import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author carlspring
 */
// This is running in sequential mode because we're overriding the default behavior of the TestMethodService class
// for the purpose of testing it.
@Execution(ExecutionMode.SAME_THREAD)
class MavenTestMethodUtilsTest
{


    @Test
    void testMethodsDefault()
    {
        TestInvocationDetails details = TestMethodService.getTestInvocationDetails(Thread.currentThread().getStackTrace());

        assertEquals("MavenTestMethodUtilsTest", details.getClassName());
        assertEquals("testMethodsDefault", details.getMethodName());
        assertEquals("target/test-resources/MavenTestMethodUtilsTest-testMethodsDefault",
                     details.getPathToMethodTestResources());
    }

    @Test
    void testMethodsWithFQDNPath()
    {
        IdempotencePropertiesService instance = IdempotencePropertiesService.getInstance();

        instance
                                    .getIdempotenceProperties()
                                    .setUseFullyQualifiedClassNamePrefixes(true);

        try
        {
            TestInvocationDetails details = TestMethodService.getTestInvocationDetails(Thread.currentThread().getStackTrace());

            assertEquals("org/carlspring/testing/idempotence/util/MavenTestMethodUtilsTest", details.getClassName());
            assertEquals("testMethodsWithFQDNPath", details.getMethodName());
            assertEquals("target/test-resources/org/carlspring/testing/idempotence/util/MavenTestMethodUtilsTest-testMethodsWithFQDNPath",
                         details.getPathToMethodTestResources());
        }
        finally
        {
            IdempotencePropertiesService.getInstance()
                                        .getIdempotenceProperties()
                                        .setUseFullyQualifiedClassNamePrefixes(false);
        }
    }

}
