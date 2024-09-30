# Initial Setup

The set up is pretty straightforward. You just need to add the appropriate dependency for your respective build tool
to your project, as illustrated below:

=== "Gradle (Groovy DSL)"
    ```groovy
    testImplementation "org.carlspring.testing.idempotence:idempotence-gradle:1.0.0-SNAPSHOT"
    ```
=== "Gradle (Kotlin DSL)"
    ```kotlin
    testImplementation("org.carlspring.testing.idempotence:idempotence-gradle:1.0.0-SNAPSHOT")
    ```
=== "Maven"
    ```xml
    <dependency>
        <groupId>org.carlspring.testing.idempotence</groupId>
        <artifactId>idempotence-maven</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
    ```

!!! note

    You must only use one of the above dependencies.

At this point you can prepare your test resources in the `src/test/resources` directory and annotate your test methods
as required. The `idempotence` framework will take care of the rest.

!!! note

    It is important that these resources must not be altered by the tests in any way; they should instead only serve as
    "read-only" resources that can be copied by the tests to their own isolated directories.

