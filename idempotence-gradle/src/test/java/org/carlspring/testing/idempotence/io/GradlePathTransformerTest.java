package org.carlspring.testing.idempotence.io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author carlspring
 */
class GradlePathTransformerTest
{

    @Test
    void testPathTransformationFromGradlePath1()
    {
        Path basePath = Paths.get("build/classes/java/test");
        Path resourcePath = Paths.get("build/classes/java/test/foo.txt");

        GradlePathTransformer transformer = new GradlePathTransformer();
        Path transformedPath = transformer.transform(basePath, resourcePath);

        assertThat(transformedPath.toString()).endsWith("build/test-resources/foo.txt");
    }

    @Test
    void testPathTransformationFromGradlePath2()
    {
        Path basePath = Paths.get("build/resources/test");
        Path resourcePath = Paths.get("build/resources/test/foo.txt");

        GradlePathTransformer transformer = new GradlePathTransformer();
        Path transformedPath = transformer.transform(basePath, resourcePath);

        assertThat(transformedPath.toString()).endsWith("build/test-resources/foo.txt");
    }

//    @Test
//    void testPathTransformationFromMavenPath()
//    {
//        Path basePath = Paths.get("idempotence/idempotence-gradle/target/test-classes/nested/dir");
//        Path resourcePath = Paths.get("idempotence/idempotence-gradle/target/test-classes/nested/dir/foo.txt");
//
//        GradlePathTransformer transformer = new GradlePathTransformer();
//        Path transformedPath = transformer.transform(basePath, resourcePath);
//
//        assertThat(transformedPath.toString()).endsWith("build/test-resources/foo.txt");
//    }

    @Test
    void testRelativization()
            throws IOException
    {
        // Load the resource
        Resource resource = new ClassPathResource("/nested/dir/foo.txt");

        // Convert the resource to a Path
        Path resourcePath = resource.getFile().toPath();

        // Calculate the relative path within the classpath
        String classpathRoot = new ClassPathResource("/").getFile().getAbsolutePath();
        Path basePath = Paths.get(classpathRoot).toAbsolutePath();
        Path relativePath = basePath.relativize(resourcePath);

        // Print the results
        System.out.println("Base Path: " + basePath);
        System.out.println("Relative Path: " + relativePath);

        assertThat(relativePath.toString()).isEqualTo("nested/dir/foo.txt");
    }

}
