"""MkDocs macros module — exposes project variables to documentation templates."""
import os
import xml.etree.ElementTree as ET


def define_env(env):
    """Define macro variables available in all MkDocs pages."""

    docs_dir = os.path.dirname(os.path.abspath(__file__))
    pom_path = os.path.join(docs_dir, "..", "pom.xml")

    try:
        tree = ET.parse(pom_path)
        root = tree.getroot()
        ns = {"m": "http://maven.apache.org/POM/4.0.0"}
        version_element = root.find("m:version", ns)
        if version_element is None:
            raise ValueError(f"<version> element not found in {pom_path}")
        if version_element.text is None:
            raise ValueError(f"<version> element is empty in {pom_path}")
        version = version_element.text
    except (FileNotFoundError, ET.ParseError, ValueError) as exc:
        raise RuntimeError(f"Failed to read project version from {pom_path}: {exc}") from exc

    # Expose as {{ version }} macro in markdown pages
    env.variables["version"] = version

    # Expose via config.extra.pom_version for use in Jinja2 theme templates
    env.conf["extra"]["pom_version"] = version
