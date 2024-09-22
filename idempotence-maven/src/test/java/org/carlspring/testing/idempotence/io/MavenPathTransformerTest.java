package org.carlspring.testing.idempotence.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author carlspring
 */
class MavenPathTransformerTest
{

    @Test
    void testPathTransformation()
    {
        Path basePath = Paths.get("target/test-classes");
        Path resourcePath = Paths.get("foo.txt");

        MavenPathTransformer transformer = new MavenPathTransformer();
        Path transformedPath = transformer.transform(basePath, resourcePath);

        assertEquals("foo.txt", transformedPath.toString());
    }

}
