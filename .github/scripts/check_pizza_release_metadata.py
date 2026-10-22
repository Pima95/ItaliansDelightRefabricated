#!/usr/bin/env python3
"""Protect the separation between published 0.4.0 and WIP Pizza 1.0.0.

This CI guard is intentionally for the pre-release feature branch only.
It must be deliberately updated/removed as part of an author-approved
version bump, and cannot substitute for a real singleplayer/server test.
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

assert re.search(r"(?m)^mod_version=0\.4\.0$", properties), (
    "The 1.0.0 version bump needs explicit author approval and release testing"
)
assert mod.get("version") == "${version}", "Keep fabric.mod.json version supplied by Gradle"
assert mod.get("id") == "italiansdelight"
assert mod.get("environment") == "*"
assert mod.get("depends", {}).get("minecraft") == "~26.2"
assert mod.get("depends", {}).get("java") == ">=25"
assert mod.get("depends", {}).get("farmersdelight")
assert "Pizza Update 1.0.0 is in active development" in readme
assert "not part of the published 0.4.0 release" in readme
assert "## Unreleased — Pizza Update 1.0.0" in changelog
assert "## 0.4.0" in changelog
assert "RELEASE_READINESS.md" in readme and "RELEASE_READINESS.md" in changelog
assert "RELEASE_READINESS.md" in roadmap
for part in ("## A. Interazioni", "## B. Forni", "## C. Servizio",
             "## D. Compatibilità", "## E. Server"):
    assert part in checklist, f"Missing pre-release checklist: {part}"
for must_include in ("16", "multiplayer", "JEI", "Pizza mista",
                     "hopper", "version", "main"):
    assert must_include.lower() in checklist.lower(), (
        f"Missing expected release test topic: {must_include}"
    )
# Only these explicitly user-confirmed runtime results are currently marked
# complete. Future confirmations must be added deliberately, not inferred from CI.
confirmed = [
    "- [x] Gli spicchi dello stesso gusto si impilano fino a **16**.",
    "- [x] Gli hopper **non estraggono la pizza cruda** dallo slot di ingresso del Pizza Oven.",
    "- [x] Dopo il salvataggio e la riapertura del mondo, una **Pizza mista mantiene i quattro gusti**.",
]
found = [line for line in checklist.splitlines() if line.startswith("- [x]")]
assert found == confirmed, (
    "Only the three author-confirmed Minecraft runtime tests should be "
    "marked complete until further testing is explicitly reported"
)
print("OK: published 0.4.0 and development-only Pizza 1.0.0 are clearly separated")
print("OK: README/CHANGELOG, mod metadata and release checklist are consistent")
print("NOTE: version bump and releases require author approval and runtime testing")
