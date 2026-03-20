plugins {
	java
}

group = "org.carlspring.testing.idempotence"
description = "Idempotence Gradle Integration Tests - Parallel Flow"

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenLocal {
		content {
			includeGroupByRegex("org\\.carlspring\\.testing\\.idempotence")
			includeVersionByRegex("org\\.carlspring\\.testing\\.idempotence", "idempotence-.*", ".*-SNAPSHOT")
		}
	}
	mavenCentral()
}

dependencies {
	testImplementation("org.carlspring.testing.idempotence:idempotence-core:1.0.0-rc-8-SNAPSHOT")
	testImplementation("org.carlspring.testing.idempotence:idempotence-gradle:1.0.0-rc-8-SNAPSHOT")

	testImplementation(enforcedPlatform("org.junit:junit-bom:6.0.3"))
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
	withType<Test> {
		useJUnitPlatform()
		// Run each test class in a separate JVM worker to simulate concurrent class initialisation
		maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(2)
		// Display stdout at console when running tests
		testLogging {
			showStandardStreams = true
		}
	}

}
