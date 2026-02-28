# `idempotence-gradle-integration-tests`

This Maven module contains the integration tests for the Gradle extension.

As the overall build is implemented using Maven, the Gradle extension is tested by running a Gradle build from the
Maven build and validating the results.

## Directory Structure

```
idempotence-gradle-integration-tests/
├── GradleIntegrationTest.groovy          # Base Groovy helper class used by test scripts
├── common-flow/                          # The "common-flow" Gradle integration test project
│   ├── build.gradle.kts                  # Gradle build file for the test project
│   ├── settings.gradle                   # Gradle settings file for the test project
│   ├── test-gradle-common-flow.groovy    # Groovy script that drives the Maven→Gradle test execution
│   └── src/
│       └── test/
│           ├── java/
│           │   └── org/carlspring/testing/idempotence/
│           │       └── GradleBasicFunctionalityTest.java   # JUnit 5 test class
│           └── resources/
│               ├── foo.txt               # Top-level test resource
│               └── nested/dir/foo.txt    # Nested test resource
├── gradle/                               # Gradle wrapper files
├── gradlew                               # Gradle wrapper script (Unix)
├── gradlew.bat                           # Gradle wrapper script (Windows)
└── pom.xml                               # Maven module descriptor
```

Each integration test scenario lives in its own subdirectory (e.g. `common-flow/`) alongside a Groovy driver script
that Maven invokes at the `integration-test` phase via the `gmavenplus-plugin`.

## How to Add a New Gradle Integration Test

Follow the steps below to add a new Gradle integration test scenario.

### 1. Create a New Test Scenario Directory

Create a new subdirectory under `idempotence-gradle-integration-tests/` to hold the self-contained Gradle project
for your test scenario. Use a descriptive, kebab-case name that reflects what the scenario tests.

```
idempotence-gradle-integration-tests/
└── my-new-scenario/
```

### 2. Add the Gradle Wrapper

The test scenario must be a fully self-contained Gradle project. Copy the wrapper from an existing scenario or
generate it anew:

```bash
cp -r common-flow/gradle            my-new-scenario/gradle
cp    common-flow/gradlew           my-new-scenario/gradlew
cp    common-flow/gradlew.bat       my-new-scenario/gradlew.bat
chmod +x my-new-scenario/gradlew
```

### 3. Create `settings.gradle`

Create a `settings.gradle` file to define the root project name:

```groovy
rootProject.name = 'my-new-scenario'
```

### 4. Create `build.gradle.kts`

Create a `build.gradle.kts` file. You can base it on `common-flow/build.gradle.kts`. The essential parts are the
test dependencies on `idempotence-core` and `idempotence-gradle`:

```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.carlspring.testing.idempotence"
description = "My New Scenario Integration Test"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("org.carlspring.testing:idempotence-core:1.0.0-SNAPSHOT")
    testImplementation("org.carlspring.testing:idempotence-gradle:1.0.0-SNAPSHOT")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(enforcedPlatform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
        }
    }
}
```

### 5. Add Test Resources

Place any test resource files your test needs under:

```
my-new-scenario/src/test/resources/
```

For example:

```
my-new-scenario/src/test/resources/my-file.txt
my-new-scenario/src/test/resources/nested/dir/my-file.txt
```

These resources are **read-only** templates. The framework copies them into an isolated directory for each test
method at runtime.

### 6. Write the JUnit 5 Test Class

Create your test class under `my-new-scenario/src/test/java/`. Annotate the class with
`@ExtendWith(TestResourceExtension.class)` and each test method with `@TestResources` to declare the resources it
requires.

```java
package org.carlspring.testing.idempotence;

import org.carlspring.testing.idempotence.annotation.TestResource;
import org.carlspring.testing.idempotence.annotation.TestResources;
import org.carlspring.testing.idempotence.extension.TestResourceExtension;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestResourceExtension.class)
class MyNewScenarioTest
{

    @Test
    @TestResources(@TestResource(source = "classpath:/my-file.txt"))
    void testSingleFile()
    {
        File testResource = new File("build/test-resources/MyNewScenarioTest-testSingleFile/my-file.txt");

        Assertions.assertTrue(testResource.exists(), "Test resource file should exist!");
    }

}
```

!!! note

    The isolated test resource directory follows the pattern:
    `build/test-resources/${TestClassName}-${testMethodName}/`

### 7. Create the Groovy Driver Script

Create a Groovy script that Maven will invoke to run the Gradle build for your scenario.
Name it `test-gradle-my-new-scenario.groovy` and place it inside the scenario directory:

```groovy
def baseScript = new GroovyScriptEngine( "$project.basedir" ).with {
    loadScriptByName( 'GradleIntegrationTest.groovy' )
}
this.metaClass.mixin baseScript

println "Executing test-gradle-my-new-scenario.groovy..."

def targetPath = getTargetPath(project)
def gradlePath = getGradlePath(project)
def executionPath = gradlePath.resolve('my-new-scenario')

def gradlewName
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    gradlewName = "gradlew.bat"
} else {
    gradlewName = "gradlew"
}

def gradleExec = gradlePath.resolve(gradlewName).toString()

runCommand(executionPath, String.format("$gradleExec build"))
```

!!! note

    The `GradleIntegrationTest.groovy` base script (located at the module root) provides the `runCommand`,
    `getTargetPath`, and `getGradlePath` helper methods.

### 8. Register the Script in `pom.xml`

Open `idempotence-gradle-integration-tests/pom.xml` and add your new Groovy script to the `<scripts>` list of the
`gmavenplus-plugin` configuration inside the `run-gradle-it-tests` profile:

```xml
<configuration>
    <scripts>
        <script>file:///${basedir}/common-flow/test-gradle-common-flow.groovy</script>
        <script>file:///${basedir}/my-new-scenario/test-gradle-my-new-scenario.groovy</script>
    </scripts>
</configuration>
```

## Running the Integration Tests

The integration tests are bound to the Maven `integration-test` phase and are enabled by default (unless
`-DskipTests` is specified).

### Run all integration tests

```bash
# From the repository root:
./mvnw install -pl idempotence-gradle-integration-tests

# Alternatively, from inside the module:
cd idempotence-gradle-integration-tests
../mvnw integration-test
```

### Skip the Gradle integration tests

```bash
./mvnw install -DskipTests
# or, explicitly deactivate the profile:
./mvnw install -P'!run-gradle-it-tests'   # Linux/macOS
./mvnw install -P!run-gradle-it-tests     # Windows
```

### Run a single test scenario manually (without Maven)

You can also run the Gradle build for a single scenario directly using the bundled Gradle wrapper:

```bash
cd idempotence-gradle-integration-tests/my-new-scenario
./gradlew build
```

!!! warning

    Running the Gradle build directly bypasses Maven's dependency resolution. Make sure all SNAPSHOT artifacts
    (`idempotence-core`, `idempotence-gradle`) are already installed in your local Maven repository (`~/.m2`) before
    running Gradle directly.

