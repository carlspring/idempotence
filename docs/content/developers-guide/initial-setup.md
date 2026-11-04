# Initial Setup

The set up is pretty straightforward. You just need to add the appropriate dependency for your respective build tool
to your project, as illustrated below:

=== "Gradle (Groovy DSL)"
    ```groovy
    testImplementation "org.carlspring.testing.idempotence:idempotence-gradle:{{ version }}"
    ```
=== "Gradle (Kotlin DSL)"
    ```kotlin
    testImplementation("org.carlspring.testing.idempotence:idempotence-gradle:{{ version }}")
    ```
=== "Maven"
    ```xml
    <dependency>
        <groupId>org.carlspring.testing.idempotence</groupId>
        <artifactId>idempotence-maven</artifactId>
        <version>{{ version }}</version>
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


## Using Snapshots

Snapshot builds are published to the [Maven Central Portal snapshot repository](https://central.sonatype.com/repository/maven-snapshots/org/carlspring/testing/idempotence/)
on every commit to `master`. Pull requests opened from branches of this repository also publish snapshots, versioned
as `<version>-PR-<pr-number>-SNAPSHOT` (for example, `1.0.0-PR-123-SNAPSHOT`).

You can find the latest snapshot version in the
[`maven-metadata.xml`](https://central.sonatype.com/repository/maven-snapshots/org/carlspring/testing/idempotence/idempotence-core/maven-metadata.xml)
of the respective artifact.

Snapshots are not available from Maven Central itself, so you will need to add the snapshot repository to your build:

=== "Gradle (Groovy DSL)"
    ```groovy
    repositories {
        mavenCentral()
        maven {
            url = "https://central.sonatype.com/repository/maven-snapshots/"
            mavenContent {
                snapshotsOnly()
            }
        }
    }
    ```
=== "Gradle (Kotlin DSL)"
    ```kotlin
    repositories {
        mavenCentral()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
    ```
=== "Maven"
    ```xml
    <repositories>
        <repository>
            <id>central-portal-snapshots</id>
            <name>Maven Central Portal Snapshots</name>
            <url>https://central.sonatype.com/repository/maven-snapshots/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    ```

Then use the snapshot version in the dependency declaration shown above.

!!! warning

    Snapshots are intended for testing unreleased changes only. They may change at any time and are automatically
    removed from the snapshot repository after 90 days, so please don't depend on them in your long-lived builds.
