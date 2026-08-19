package org.carlspring.testing.idempotence.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author carlspring
 */
class S3fsNioPathTransformerTest
{

    @Test
    void testTransformIsNoOp()
    {
        Path basePath = Paths.get("target/test-classes");
        Path resourcePath = Paths.get("foo.txt");

        S3fsNioPathTransformer transformer = new S3fsNioPathTransformer();
        Path transformedPath = transformer.transform(basePath, resourcePath);

        assertEquals("foo.txt", transformedPath.toString());
    }

    @Test
    void testRelativizeMavenPath()
    {
        Path path = Paths.get("/home/runner/work/project/target/test-classes/foo.txt");

        S3fsNioPathTransformer transformer = new S3fsNioPathTransformer();
        Path relativized = transformer.relativize(path);

        assertEquals("foo.txt", relativized.toString());
    }

    @Test
    void testRelativizeMavenNestedPath()
    {
        Path path = Paths.get("/home/runner/work/project/target/test-classes/nested/dir/foo.txt");

        S3fsNioPathTransformer transformer = new S3fsNioPathTransformer();
        Path relativized = transformer.relativize(path);

        assertEquals("nested/dir/foo.txt", relativized.toString());
    }

    @Test
    void testRelativizeGradleResourcesPath()
    {
        Path path = Paths.get("/home/runner/work/project/build/resources/test/foo.txt");

        S3fsNioPathTransformer transformer = new S3fsNioPathTransformer();
        Path relativized = transformer.relativize(path);

        assertEquals("foo.txt", relativized.toString());
    }

    @Test
    void testRelativizeGradleClassesPath()
    {
        Path path = Paths.get("/home/runner/work/project/build/classes/java/test/nested/dir/foo.txt");

        S3fsNioPathTransformer transformer = new S3fsNioPathTransformer();
        Path relativized = transformer.relativize(path);

        assertEquals("nested/dir/foo.txt", relativized.toString());
    }

    @Test
    void testRelativizeUnknownPath()
    {
        Path path = Paths.get("foo.txt");

        S3fsNioPathTransformer transformer = new S3fsNioPathTransformer();
        Path relativized = transformer.relativize(path);

        assertEquals("foo.txt", relativized.toString());
    }

}
