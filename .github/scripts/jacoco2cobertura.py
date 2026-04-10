#!/usr/bin/env python3
"""
Convert a JaCoCo XML coverage report to the Cobertura XML format.

Usage:
    python jacoco2cobertura.py <jacoco.xml> <source_dir> > cobertura.xml

Arguments:
    jacoco.xml   - Path to the JaCoCo XML report file
    source_dir   - Path to the Java source root (e.g. src/main/java)
"""

import sys
import time
from xml.etree import ElementTree as ET


def get_counter(element, counter_type):
    """Return (missed, covered) for the given counter type."""
    for counter in element.findall("counter"):
        if counter.get("type") == counter_type:
            return int(counter.get("missed", 0)), int(counter.get("covered", 0))
    return 0, 0


def rate(missed, covered):
    total = missed + covered
    return covered / total if total > 0 else 0.0


def convert(jacoco_path, source_dir):
    tree = ET.parse(jacoco_path)
    root = tree.getroot()

    total_lm, total_lc = 0, 0
    total_bm, total_bc = 0, 0

    cov = ET.Element("coverage")
    sources_el = ET.SubElement(cov, "sources")
    ET.SubElement(sources_el, "source").text = source_dir
    packages_el = ET.SubElement(cov, "packages")

    for package in root.findall("package"):
        pkg_name = package.get("name", "").replace("/", ".")
        pkg_lm, pkg_lc, pkg_bm, pkg_bc = 0, 0, 0, 0

        pkg_el = ET.SubElement(packages_el, "package")
        pkg_el.set("name", pkg_name)
        pkg_el.set("complexity", "0")
        classes_el = ET.SubElement(pkg_el, "classes")

        # Build a map of sourcefile name -> line elements for fast lookup.
        sourcefiles = {}
        for sf in package.findall("sourcefile"):
            sourcefiles[sf.get("name")] = sf

        for cls in package.findall("class"):
            cls_name = cls.get("name", "")             # e.g. "org/example/MyClass"
            source_file_name = cls.get("sourcefilename", "")  # e.g. "MyClass.java"

            lm, lc = get_counter(cls, "LINE")
            bm, bc = get_counter(cls, "BRANCH")
            pkg_lm += lm; pkg_lc += lc
            pkg_bm += bm; pkg_bc += bc

            cls_el = ET.SubElement(classes_el, "class")
            cls_el.set("name", cls_name.replace("/", "."))
            # filename is relative to the source root
            if "/" in cls_name:
                pkg_path = "/".join(cls_name.split("/")[:-1])
                cls_el.set("filename", f"{pkg_path}/{source_file_name}")
            else:
                cls_el.set("filename", source_file_name)
            cls_el.set("line-rate", f"{rate(lm, lc):.4f}")
            cls_el.set("branch-rate", f"{rate(bm, bc):.4f}")
            cls_el.set("complexity", "0")

            methods_el = ET.SubElement(cls_el, "methods")
            for method in cls.findall("method"):
                m_lm, m_lc = get_counter(method, "LINE")
                m_bm, m_bc = get_counter(method, "BRANCH")
                m_el = ET.SubElement(methods_el, "method")
                m_el.set("name", method.get("name", ""))
                m_el.set("signature", method.get("desc", ""))
                m_el.set("line-rate", f"{rate(m_lm, m_lc):.4f}")
                m_el.set("branch-rate", f"{rate(m_bm, m_bc):.4f}")
                m_el.set("complexity", "0")
                ET.SubElement(m_el, "lines")

            lines_el = ET.SubElement(cls_el, "lines")
            sf = sourcefiles.get(source_file_name)
            if sf is not None:
                for line in sf.findall("line"):
                    ci = int(line.get("ci", 0))
                    cb = int(line.get("cb", 0))
                    mb = int(line.get("mb", 0))
                    branch = (cb + mb) > 0
                    ln = ET.SubElement(lines_el, "line")
                    ln.set("number", line.get("nr", "0"))
                    ln.set("hits", str(ci))
                    ln.set("branch", "true" if branch else "false")
                    if branch:
                        total_b = cb + mb
                        pct = int(100 * cb / total_b) if total_b else 0
                        ln.set("condition-coverage", f"{pct}% ({cb}/{total_b})")

        total_lm += pkg_lm; total_lc += pkg_lc
        total_bm += pkg_bm; total_bc += pkg_bc
        pkg_el.set("line-rate", f"{rate(pkg_lm, pkg_lc):.4f}")
        pkg_el.set("branch-rate", f"{rate(pkg_bm, pkg_bc):.4f}")

    cov.set("line-rate", f"{rate(total_lm, total_lc):.4f}")
    cov.set("branch-rate", f"{rate(total_bm, total_bc):.4f}")
    cov.set("lines-covered", str(total_lc))
    cov.set("lines-valid", str(total_lc + total_lm))
    cov.set("branches-covered", str(total_bc))
    cov.set("branches-valid", str(total_bc + total_bm))
    cov.set("complexity", "0")
    cov.set("version", "0.1")
    cov.set("timestamp", str(int(time.time())))

    ET.indent(cov, space="  ")
    print('<?xml version="1.0" ?>')
    print('<!DOCTYPE coverage SYSTEM "http://cobertura.sourceforge.net/xml/coverage-04.dtd">')
    print(ET.tostring(cov, encoding="unicode"))


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(f"Usage: {sys.argv[0]} <jacoco.xml> <source_dir>", file=sys.stderr)
        sys.exit(1)
    convert(sys.argv[1], sys.argv[2])
