# Conceptual Overview

<p align="center">
  <img src="../../assets/images/idempotence-4-1.webp" />
</p>

## Problem Statement

A common challenge when running tests in parallel is test interference, which often leads to inconsistent or unreliable
results. This problem arises primarily when tests share resources, such as files, databases, or other external services.
When multiple tests attempt to access or modify these shared resources simultaneously, they can conflict, causing tests
to fail randomly and in unexpected and hard to reproduce and debug ways. 

To mitigate this, it's essential to ensure that each test has exclusive access to the resources it requires.
One effective approach is to create isolated environments for each test case by copying the necessary shared resources
into separate directories or instances. By doing so, you ensure that each test can operate independently, free from the
influence of other tests running in parallel. This guarantees that any changes or manipulations performed by one test
do not affect the execution or results of another.

A crucial aspect of this process is being able to identify and explicitly define the resources that need isolation.
If a test can correctly list the resource files, services, or external dependencies it interacts with, you can ensure
those resources are appropriately isolated for that specific test case and thus achieve idempotence.
This allows tests to run concurrently without side effects.

This is where the concept of idempotence becomes key. By designing your tests and their associated resource management
with idempotence in mind, you ensure that no matter how many times a test is run or how many tests are executed
simultaneously, the outcome remains consistent. Each test will be self-contained, repeatable, and immune to external
interference—allowing you to harness the full power of parallel execution without compromising reliability.

The Idempotence framework is a Java testing library that provides the tools and utilities to help you achieve this.

## Concept

All common test resources are stored under the `src/test/resources` directory as usual.

Each test method defines the resources it needs by annotating it with the `@TestResources` annotation. The framework
copies these resources to an isolated directory for the test to use. This ensures that the test has exclusive access
to the resources it requires, preventing interference from other tests running in parallel.

!!! note

    It is important that these common test resources are not altered by the tests in any way.
    They should instead only serve as "read-only" resources that can be copied by the tests
    to their own isolated directories.

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
build/test-resources/GradleBasicFunctionalityTest-testMultipleWithPatterns/foo.txt
```

* For a Maven project, with a test called `MavenBasicFunctionalityTest` with a method `testMultipleWithPatterns`,
  they will be placed under:
```
target/test-resources/MavenBasicFunctionalityTest-testMultipleWithPatterns/nested/dir/foo.txt
target/test-resources/MavenBasicFunctionalityTest-testMultipleWithPatterns/foo.txt
```

At this point you need to make sure your tests look for the test resources in their isolated directories.
