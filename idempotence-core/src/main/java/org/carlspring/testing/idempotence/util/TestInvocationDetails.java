package org.carlspring.testing.idempotence.util;

import org.carlspring.testing.idempotence.config.IdempotenceProperties;
import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;

/**
 * Holds details about the current test invocation, including the class name, method name,
 * and the path to the test resource directory.
 *
 * @author carlspring
 */
public class TestInvocationDetails
{

    private String className;

    private String methodName;

    private IdempotenceProperties properties = IdempotencePropertiesService.getInstance().getIdempotenceProperties();


    /**
     * Creates a new instance of {@link TestInvocationDetails} with no values set.
     */
    public TestInvocationDetails()
    {
    }

    /**
     * Creates a new instance of {@link TestInvocationDetails} with the given class and method names.
     *
     * @param className  the fully-qualified name of the test class
     * @param methodName the name of the test method
     */
    public TestInvocationDetails(String className,
                                 String methodName)
    {
        this.className = className;
        this.methodName = methodName;
    }

    /**
     * Returns the path to the test resource directory for the current test method.
     *
     * @return the path to the test resource directory
     */
    public String getPathToMethodTestResources()
    {
        return getBaseDir() + "/" + getClassNameString() + "-" + methodName;
    }

    private String getBaseDir()
    {
        return properties.getBasedir().endsWith("/") ?
               properties.getBasedir().substring(0, properties.getBasedir().length() - 1) :
               properties.getBasedir();
    }

    private String getClassNameString()
    {
        if (properties.useFullyQualifiedNamePrefixes())
        {
            return className.replace(".", "/");
        }
        else
        {
            return className.substring(className.lastIndexOf(".") + 1);
        }
    }

    /**
     * Returns the fully-qualified name of the test class.
     *
     * @return the class name
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Sets the fully-qualified name of the test class.
     *
     * @param className the class name to set
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * Returns the name of the test method.
     *
     * @return the method name
     */
    public String getMethodName()
    {
        return methodName;
    }

    /**
     * Sets the name of the test method.
     *
     * @param methodName the method name to set
     */
    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

}
