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
class GradleTestMethodUtilsTest
{


    @Test
    void testMethodsDefault()
    {
        TestInvocationDetails details = TestMethodService.getTestInvocationDetails(Thread.currentThread().getStackTrace());

        assertEquals("GradleTestMethodUtilsTest", details.getClassName());
        assertEquals("testMethodsDefault", details.getMethodName());
        assertEquals("build/test-resources/GradleTestMethodUtilsTest-testMethodsDefault",
                     details.getPathToMethodTestResources());
    }

    @Test
    void testMethodsWithFQDNPath()
    {
        IdempotencePropertiesService.getInstance()
                                    .getIdempotenceProperties()
                                    .setUseFullyQualifiedClassNamePrefixes(true);

        try
        {
            TestInvocationDetails details = TestMethodService.getTestInvocationDetails(Thread.currentThread().getStackTrace());

            assertEquals("org/carlspring/testing/idempotence/util/GradleTestMethodUtilsTest", details.getClassName());
            assertEquals("testMethodsWithFQDNPath", details.getMethodName());
            assertEquals("build/test-resources/org/carlspring/testing/idempotence/util/GradleTestMethodUtilsTest-testMethodsWithFQDNPath",
                         details.getPathToMethodTestResources());
        }
        finally
        {
            IdempotencePropertiesService.getInstance()
                                        .getIdempotenceProperties()
                                        .setUseFullyQualifiedClassNamePrefixes(false);
        }
    }

    @Test
    void testMethodsWithSlashSeparator()
    {
        IdempotencePropertiesService.getInstance()
                                    .getIdempotenceProperties()
                                    .setSeparator("/");

        try
        {
            TestInvocationDetails details = TestMethodService.getTestInvocationDetails(Thread.currentThread().getStackTrace());

            assertEquals("GradleTestMethodUtilsTest", details.getClassName());
            assertEquals("testMethodsWithSlashSeparator", details.getMethodName());
            assertEquals("build/test-resources/GradleTestMethodUtilsTest/testMethodsWithSlashSeparator",
                         details.getPathToMethodTestResources());
        }
        finally
        {
            IdempotencePropertiesService.getInstance()
                                        .getIdempotenceProperties()
                                        .setSeparator("-");
        }
    }

}
