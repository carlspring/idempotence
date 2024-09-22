package org.carlspring.testing.idempotence.util;

import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;

/**
 * @author carlspring
 */
public class TestMethodService
{


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
