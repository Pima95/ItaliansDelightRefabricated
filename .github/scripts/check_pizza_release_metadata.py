#!/usr/bin/env python3
"""Verify published 1.0.0 metadata and historical pizza regression documentation.

Originally this guarded a development-only Pizza 1.0.0 branch. Since the
author released 1.0.0, it now checks truthful published metadata without
discarding the explicit release/test record.
"""
import json
import re
from pathlib import Path

readme = Path("README.md").read_text(encoding="utf-8")
changelog = Path("CHANGELOG.md").read_text(encoding="utf-8")
roadmap = Path("docs/pizza-1.0.0/ROADMAP.md").read_text(encoding="utf-8")
checklist = Path("docs/pizza-1.0.0/RELEASE_READINESS.md").read_text(encoding="utf-8")
properties = Path("gradle.properties").read_text(encoding="utf-8")
mod = json.loads(Path("src/main/resources/fabric.mod.json").read_text(encoding="utf-8"))

# The 1.1.0 work branch deliberately keeps the latest released JAR version:
# building on a feature branch is not publication of 1.1.0.
assert re.search(r"(?m)^mod_version=1\.0\.0$", properties)
assert mod.get("version") == "${version}"
assert mod.get("id") == "italiansdelight"
assert mod.get("environment") == "*"
assert mod.get("depends", {}).get("minecraft") == "~26.2"
assert mod.get("depends", {}).get("fabricloader") == ">=0.20.1"
assert re.search(r"(?m)^loader_version=0\.20\.1$", properties)
assert re.search(r"(?m)^fabric_version=0\.161\.0\+26\.2$", properties)
assert re.search(r"(?m)^fdrf_version=26\.2-3\.6\.29\+refabricated$", properties)
assert mod.get("depends", {}).get("java") == ">=25"
assert mod.get("depends", {}).get("farmersdelight")
assert "Pizza Update 1.0.0" in readme and "is now published" in readme
assert "Orchard Update 1.1.0" in readme
assert "## 1.0.0 — Pizza Update" in changelog
assert "## 0.4.0" in changelog
assert "RELEASE_READINESS.md" in changelog
assert "RELEASE_READINESS.md" in roadmap
for part in ("## A. Interazioni", "## B. Forni", "## C. Servizio",
             "## D. Compatibilità", "## E. Server"):
    assert part in checklist, f"Missing historical test checklist section: {part}"
for topic in ("16", "multiplayer", "JEI", "Pizza mista", "hopper", "version", "main"):
    assert topic.lower() in checklist.lower(), f"Missing historical test topic: {topic}"
# Preserve exact author-confirmed outcomes; do not silently mark the untested
# dedicated server or exceptional inventory states as verified.
confirmed = [
    "- [x] Gli spicchi dello stesso gusto si impilano fino a **16**.",
    "- [x] Gli hopper **non estraggono la pizza cruda** dallo slot di ingresso del Pizza Oven.",
    "- [x] Dopo il salvataggio e la riapertura del mondo, una **Pizza mista mantiene i quattro gusti**.",
    "- [x] Le 18 pizze riconosciute dispongono delle nuove istruzioni personalizzate per stendere e condire l'impasto in JEI.",
    "- [x] La categoria degli spicchi utilizza il layout originale Cutting Board di Farmer's Delight Refabricated.",
    "- [x] Il siero nella ricetta JEI della Ricotta è allineato nel tank destro, dopo la correzione di 4 pixel.",
]
found = [line for line in checklist.splitlines() if line.startswith("- [x]")]
assert found == confirmed, "Historical runtime confirmations have changed without review"

print("OK: published 1.0.0 and feature branch share truthful version metadata")
print("OK: archived Pizza 1.0.0 checklist and confirmed regressions remain intact")
print("NOTE: 1.1.0 remains in development; no new version/tag/publication")
