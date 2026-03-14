---
title: idempotence-gradle
---

# `idempotence-gradle`

This Maven module is where the code for Gradle extension is implemented.

## Directory Structure (Gradle)

Under Gradle, test resources are copied to the `build/test-resources/${testClass}/${testMethod}` directory by default.

See the [Configuration](../configuration.md) page for the system properties that control the base directory,
the separator, and other behaviour.
