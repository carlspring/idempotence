# Configuration

The Idempotence framework exposes a set of system properties that can be used to tune its behaviour without modifying
test code. All properties share the common prefix `org.carlspring.testing.idempotence`.

## System Properties Reference

### `org.carlspring.testing.idempotence.target.separator`

Controls the character (or string) used to join the test class name and the test method name when constructing
the isolated test-resource directory path.

| Attribute    | Value                                                                |
|:-------------|:---------------------------------------------------------------------|
| Type         | `String`                                                             |
| Default      | `/`                                                                  |
| Applies to   | Maven and Gradle                                                     |

**Example – default (`/`):**

=== "Maven"
    ```
    target/test-resources/MavenBasicFunctionalityTest/testMultipleWithPatterns/foo.txt
    ```
=== "Gradle"
    ```
    build/test-resources/GradleBasicFunctionalityTest/testMultipleWithPatterns/foo.txt
    ```

**Example – with `-Dorg.carlspring.testing.idempotence.target.separator=-`:**

=== "Maven"
    ```
    target/test-resources/MavenBasicFunctionalityTest-testMultipleWithPatterns/foo.txt
    ```
=== "Gradle"
    ```
    build/test-resources/GradleBasicFunctionalityTest-testMultipleWithPatterns/foo.txt
    ```

The separator can also be changed programmatically at runtime:

```java
IdempotencePropertiesService.getInstance()
                            .getIdempotenceProperties()
                            .setSeparator("-");
```

---

### `org.carlspring.testing.idempotence.basedir`

Overrides the base directory into which isolated test-resource directories are created. When this property is not
set the build-tool-specific default is used.

| Attribute    | Value                                          |
|:-------------|:-----------------------------------------------|
| Type         | `String`                                       |
| Default      | `target/test-resources` (Maven), `build/test-resources` (Gradle) |
| Applies to   | Maven and Gradle                               |

**Example:**

=== "Maven"
    ```bash
    -Dorg.carlspring.testing.idempotence.basedir=/tmp/test-resources
    ```
=== "Gradle"
    ```bash
    -Dorg.carlspring.testing.idempotence.basedir=/tmp/test-resources
    ```

---

### `org.carlspring.testing.idempotence.useFullyQualifiedClassNamePrefixes`

When set to `true`, the test class name part of the path is expanded to the fully-qualified class name
(using `/` as the package separator), giving every package segment its own directory level.

| Attribute    | Value                                                                |
|:-------------|:---------------------------------------------------------------------|
| Type         | `boolean`                                                            |
| Default      | `false`                                                              |
| Applies to   | Maven and Gradle                                                     |

**Example – default (`false`):**

=== "Maven"
    ```
    target/test-resources/MavenBasicFunctionalityTest/testMultipleWithPatterns/foo.txt
    ```
=== "Gradle"
    ```
    build/test-resources/GradleBasicFunctionalityTest/testMultipleWithPatterns/foo.txt
    ```

**Example – with `-Dorg.carlspring.testing.idempotence.useFullyQualifiedClassNamePrefixes=true`:**

=== "Maven"
    ```
    target/test-resources/org/carlspring/testing/idempotence/MavenBasicFunctionalityTest/testMultipleWithPatterns/foo.txt
    ```
=== "Gradle"
    ```
    build/test-resources/org/carlspring/testing/idempotence/GradleBasicFunctionalityTest/testMultipleWithPatterns/foo.txt
    ```
