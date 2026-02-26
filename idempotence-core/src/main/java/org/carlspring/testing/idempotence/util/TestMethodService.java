package org.carlspring.testing.idempotence.util;

import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;

/**
 * Utility service for retrieving details about the currently invoked test method.
 *
 * @author carlspring
 */
public class TestMethodService
{

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TestMethodService()
    {
    }

    /**
     * Returns {@link TestInvocationDetails} for the test method that is currently being invoked,
     * based on the provided stack trace.
     *
     * @param invokingThreadStackTrace the stack trace of the invoking thread
     * @return the {@link TestInvocationDetails} for the current test method
     */
    public static TestInvocationDetails getTestInvocationDetails(StackTraceElement[] invokingThreadStackTrace)
    {
        // Get the current class and method name
        StackTraceElement currentMethod = invokingThreadStackTrace[1];

        String className = IdempotencePropertiesService.getInstance()
                                                       .getIdempotenceProperties()
                                                       .useFullyQualifiedNamePrefixes() ?
                           currentMethod.getClassName().replaceAll("\\.", "/") :
                           currentMethod.getClassName().substring(currentMethod.getClassName().lastIndexOf(".") + 1);

        String methodName = currentMethod.getMethodName();

        TestInvocationDetails details = new TestInvocationDetails();
        details.setClassName(className);
        details.setMethodName(methodName);

        return details;
    }

}
