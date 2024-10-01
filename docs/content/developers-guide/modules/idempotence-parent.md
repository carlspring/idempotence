# `idempotence-parent`

This Maven module is the parent module for the Idempotence project.
It contains the common configuration for all the other modules in the project, such as:

* Dependency management
* Plugin management
* Common properties

All Maven modules in the project must inherit the `idempotence-parent` module in their `pom.xml` files and this is the
only place where the versions of the dependencies and version properties should be defined.
