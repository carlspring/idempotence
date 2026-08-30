package org.carlspring.testing.idempotence.util;

import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author carlspring
 */
// This is running in sequential mode because we're overriding the default behavior of the
// IdempotenceProperties for the purpose of testing.
@Execution(ExecutionMode.SAME_THREAD)
class S3fsNioTestMethodUtilsTest
{


    @Test
    void testMethodsDefault()
    {
        TestInvocationDetails details = TestMethodService.getTestInvocationDetails(Thread.currentThread().getStackTrace());

        assertEquals("S3fsNioTestMethodUtilsTest", details.getClassName());
        assertEquals("testMethodsDefault", details.getMethodName());
        assertEquals("s3:///idempotence-test-resources/S3fsNioTestMethodUtilsTest-testMethodsDefault",
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

            assertEquals("org/carlspring/testing/idempotence/util/S3fsNioTestMethodUtilsTest", details.getClassName());
            assertEquals("testMethodsWithFQDNPath", details.getMethodName());
            assertEquals("s3:///idempotence-test-resources/org/carlspring/testing/idempotence/util/S3fsNioTestMethodUtilsTest-testMethodsWithFQDNPath",
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
