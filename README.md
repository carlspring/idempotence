
<p align="center">
  <img src="docs/resources/images/idempotence-4-1.webp" />
</p>

# Problem Statement

You need a simple framework that guarantees that your test resources are independent for each test case,
so that you can run your tests in parallel without any side effects and get consistent results.

Enter, `idempotence`.

# Concept

Common test resources are stored under the `src/test/resources` directory.

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
                     @TestResource(source = "classpath:/**/foo.txt")} )
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

These test resources will be placed under:
```
target/test-resources/BasicFunctionalityTest/testMultipleWithPatterns/nested/dir/foo.txt
target/test-resources/BasicFunctionalityTest/testMultipleWithPatterns/foo.txt
```

At this point you need to make sure your tests look for the test resources in their isolated directories.
