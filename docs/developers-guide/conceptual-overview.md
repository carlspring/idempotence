# Conceptual Overview

<p align="center">
  <img src="../assets/images/idempotence-4-1.webp" />
</p>

## Problem Statement

You need a simple framework that guarantees that your test resources are independent for each test case,
so that you can run your tests in parallel without any side effects and get consistent results.

Enter, `idempotence`.

## Concept

Common test resources are stored under the `src/test/resources` directory.

!!! note

    It is important that these resources must not be altered by the tests in any way; they should instead only serve as
    "read-only" resources that can be copied by the tests to their own isolated directories.

### Extensions

As different build tools have their own directory structures, the `idempotence` framework provides extensions for:

* [Maven](./modules/idempotence-maven.md)
* [Gradle](./modules/idempotence-gradle.md)

These extensions handle the copying of test resources to the isolated directories of the tests.

### Directory Structure

Each test case has its own test resources directory in the format `${testName}/${testMethod}` under the
`target/test-resources` directory like this:
```
target/test-resources/BasicFunctionalityTest/testMultipleWithPatterns
target/test-resources/BasicFunctionalityTest/testSingleFile
```

Test methods are annotated with the `@TestResources` annotation to specify the resources that they need. For example:
```java
    @Test
    @TestResources({ @TestResource(source = "classpath:/foo.txt"),
                     @TestResource(source = "classpath*:/**/foo.txt")} )
    void testMultipleWithPatterns()
    {
        ...
    }
```

And provided that the following resources exist:
```
src/test/resources/foo.txt
src/test/resources/nested/dir/foo.txt
```

* For a Gradle project, with a test called `GradleBasicFunctionalityTest` with a method `testMultipleWithPatterns`,
  they will be placed under:
```
build/test-resources/GradleBasicFunctionalityTest-testMultipleWithPatterns/nested/dir/foo.txt
build/test-resources/GradleBasicFunctionalityTest-testMultipleWithPatterns/testMultipleWithPatterns/foo.txt
```

* For a Maven project, with a test called `MavenBasicFunctionalityTest` with a method `testMultipleWithPatterns`,
  they will be placed under:
```
target/test-resources/MavenBasicFunctionalityTest-testMultipleWithPatterns/nested/dir/foo.txt
target/test-resources/MavenBasicFunctionalityTest-testMultipleWithPatterns/foo.txt
```

At this point you need to make sure your tests look for the test resources in their isolated directories.
