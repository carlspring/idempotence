package org.carlspring.testing.idempotence.extension;

import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.config.IdempotencePropertiesService;
import org.carlspring.testing.idempotence.config.PathTransformerService;
import org.carlspring.testing.idempotence.io.PathTransformer;
import org.carlspring.testing.idempotence.io.ResourceCopier;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestResourceExtension
        implements BeforeEachCallback, BeforeAllCallback
{

    private ResourceCopier resourceCopier = new ResourceCopier(IdempotencePropertiesService.getInstance()
                                                                                           .getIdempotenceProperties()
                                                                                           .getBasedir());


    public TestResourceExtension()
    {
    }

    @Override
    public void beforeAll(ExtensionContext context)
            throws Exception
    {
        handleResources(context.getRequiredTestClass().getAnnotation(TestResources.class), context);
    }

    @Override
    public void beforeEach(ExtensionContext context)
            throws Exception
    {
        handleResources(context.getRequiredTestMethod().getAnnotation(TestResources.class), context);
    }

    private void handleResources(TestResources testResources, ExtensionContext context)
            throws IOException, URISyntaxException
    {
        if (testResources != null)
        {
            String testResourceDir = getTestResourceDirectory(context);
            resourceCopier.copyResources(testResources.value(), testResourceDir);
        }
    }

    private String getTestResourceDirectory(ExtensionContext context)
    {
        String className = context.getRequiredTestClass().getSimpleName();
        Optional<Method> testMethod = context.getTestMethod();

        return testMethod.map(method -> String.format("%s-%s", className, method.getName())).orElse(className);
    }

}
