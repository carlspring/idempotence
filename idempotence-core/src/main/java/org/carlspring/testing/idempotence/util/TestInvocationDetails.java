package org.carlspring.testing.idempotence.util;

import org.carlspring.testing.idempotence.config.IdempotenceProperties;
import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;

/**
 * @author carlspring
 */
public class TestInvocationDetails
{

    private String className;

    private String methodName;

    private IdempotenceProperties properties = IdempotencePropertiesService.getInstance().getIdempotenceProperties();


    public TestInvocationDetails()
    {
    }

    public TestInvocationDetails(String className,
                                 String methodName)
    {
        this.className = className;
        this.methodName = methodName;
    }

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

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

}
