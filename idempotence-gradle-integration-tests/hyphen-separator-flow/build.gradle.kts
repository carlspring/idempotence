plugins {
	java
}

group = "org.carlspring.testing.idempotence"
description = "Idempotence Gradle Integration Tests - Hyphen Separator Flow"

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
	testImplementation("org.carlspring.testing.idempotence:idempotence-core:1.0.0-rc-7-SNAPSHOT")
	testImplementation("org.carlspring.testing.idempotence:idempotence-gradle:1.0.0-rc-7-SNAPSHOT")

	testImplementation(enforcedPlatform("org.junit:junit-bom:6.0.3"))
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
	withType<Test> {
		useJUnitPlatform()
		// Use hyphen as the separator between class name and method name in test resource paths
		jvmArgs("-Dorg.carlspring.testing.idempotence.target.separator=-")
		// Display stdout at console when running tests
		testLogging {
			showStandardStreams = true
		}
	}

	named<ProcessResources>("processResources") {
	}

}
