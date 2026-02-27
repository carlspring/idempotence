import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "org.carlspring.testing.idempotence"
description = "Idempotence Gradle Integration Tests - Slash Separator Flow"

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
	mavenLocal()
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
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
		// Use slash as the separator between class name and method name in test resource paths
		jvmArgs("-Dorg.carlspring.testing.idempotence.target.separator=/")
		// Display stdout at console when running tests
		testLogging {
			showStandardStreams = true
		}
	}

	named<ProcessResources>("processResources") {
	}

	named<BootRun>("bootRun") {
	}

	named<Jar>("jar") {
	}

	named<BootJar>("bootJar") {
		mainClass = "com.carlspring.accounting.AccountingApplication"
		layered {
			application {
				intoLayer("spring-boot-loader") {
					include("org/springframework/boot/loader/**")
				}
				intoLayer("application")
			}
			dependencies {
				intoLayer("application") {
					includeProjectDependencies()
				}
				intoLayer("snapshot-dependencies") {
					include("*:*:*SNAPSHOT")
				}
				intoLayer("dependencies")
			}
			layerOrder.set(listOf("dependencies", "spring-boot-loader", "snapshot-dependencies", "application"))
		}
	}

}
