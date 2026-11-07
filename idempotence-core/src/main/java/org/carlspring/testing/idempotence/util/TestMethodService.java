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
     * Returns {@link TestInvocationDetails} for the method that called this one.
     *
     * @return the {@link TestInvocationDetails} for the current test method
     */
    public static TestInvocationDetails getTestInvocationDetails()
    {
        StackWalker.StackFrame caller = StackWalker.getInstance()
                                                   .walk(frames -> frames.skip(1).findFirst())
                                                   .orElseThrow();

        return getTestInvocationDetails(caller.getClassName(), caller.getMethodName());
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

        return getTestInvocationDetails(currentMethod.getClassName(), currentMethod.getMethodName());
    }

    private static TestInvocationDetails getTestInvocationDetails(String fullyQualifiedClassName,
                                                                  String methodName)
    {
        String className = IdempotencePropertiesService.getInstance()
                                                       .getIdempotenceProperties()
                                                       .useFullyQualifiedNamePrefixes() ?
                           fullyQualifiedClassName.replaceAll("\\.", "/") :
                           fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1);

        TestInvocationDetails details = new TestInvocationDetails();
        details.setClassName(className);
        details.setMethodName(methodName);

        return details;
    }

}
