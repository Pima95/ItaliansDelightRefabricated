"""Release-candidate *preparation* gate for Orchard 1.1.0 (not publication).

Checks truthful public/development version separation, logo path,
important Orchard resources, docs and CI wiring; it does NOT claim that
Minecraft survival/server/legacy-world tests have been performed.
"""
from pathlib import Path
import json, re, struct

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "src/main/resources"
DATA = SRC / "data/italiansdelight/worldgen"
r = lambda path: (ROOT / path).read_text(encoding="utf-8")
mod = json.loads((SRC / "fabric.mod.json").read_text(encoding="utf-8"))
properties = r("gradle.properties")
readme = r("README.md")
changelog = r("CHANGELOG.md")
roadmap = r("docs/orchards-1.1.0/ROADMAP.md")
checklist = r("docs/orchards-1.1.0/RELEASE_READINESS.md")
notes = r("docs/orchards-1.1.0/RELEASE_NOTES_DRAFT.md")

# The Orchard candidate stays non-public until separately authorized.
assert re.search(r"(?m)^mod_version=1\.1\.0$", properties)
assert "1.1.0-rc.1" in readme
assert mod["version"] == "${version}"
assert mod["id"] == "italiansdelight"
assert mod["depends"]["minecraft"] == "~26.2"
assert mod["depends"]["java"] == ">=25"
assert mod["license"] == "Apache-2.0"
# Keep MC 26.2 / Java 25. Java 21 requires a separate Minecraft port.
build = r("build.gradle")
workflow = r(".github/workflows/pizza-build.yml")
assert "it.options.release = 25" in build
assert "sourceCompatibility = JavaVersion.VERSION_25" in build
assert "targetCompatibility = JavaVersion.VERSION_25" in build
assert "java-version: '25'" in workflow
assert re.search(r"(?m)^minecraft_version=26\.2$", properties)
assert "Apache License" in r("LICENSE")
assert "Version 2.0, January 2004" in r("LICENSE")
assert "Piergiuseppe Mastroserio (Pima95)" in r("NOTICE")
assert 'from("NOTICE")' in build
assert 'from("THIRD_PARTY_NOTICES.md")' in build
assert 'from("docs/orchards-1.1.0/FDR_CRATES_LICENSE.md")' in build
assert mod["depends"]["farmersdelight"]
assert mod["icon"] == "assets/italiansdelight/textures/icon.png"
icon = SRC / mod["icon"]
assert icon.is_file()
binary = icon.read_bytes()
assert binary[:8] == b"\x89PNG\r\n\x1a\n"
width, height = struct.unpack(">II", binary[16:24])
assert 32 <= width <= 512 and 32 <= height <= 512
assert not (SRC / "assets/italiansdelight/icon.png").exists(), "Removed erroneous icon copy came back"

# Existing and meadow-only configurations must all be inside the jar.
for name, ref in (
    ("olive_tree_wild","olive_tree"),
    ("ancient_olive_tree_wild","ancient_olive_tree"),
    ("almond_tree_wild","almond_tree"),
    ("almond_tree_meadow_wild","almond_tree"),
    ("fruit_cherry_tree_wild","fruit_cherry_tree"),
):
    placement = json.loads((DATA / "placed_feature" / (name + ".json")).read_text())
    assert placement["feature"] == "italiansdelight:" + ref

# The last author-approved probabilities must never drift during release prep.
chances = {name:json.loads((DATA / "placed_feature" / (name + ".json")).read_text())["placement"][0]["chance"]
           for name in ("olive_tree_wild","ancient_olive_tree_wild","almond_tree_wild",
                        "almond_tree_meadow_wild","fruit_cherry_tree_wild")}
assert chances == {"olive_tree_wild":40, "ancient_olive_tree_wild":96,
                   "almond_tree_wild":40, "almond_tree_meadow_wild":100,
                   "fruit_cherry_tree_wild":7}
for name in ("olive_tree","ancient_olive_tree","almond_tree","fruit_cherry_tree"):
    assert (DATA / "configured_feature" / (name + ".json")).is_file()
assert "ELIGIBILITY_PERCENT = 65" in r(
    "src/main/java/dev/italiansdelight/common/orchard/OrchardFruitingCycle.java")

# Existing functionality intentionally excluded from this release stays excluded.
assert "Grapes, grape juice and wine" in notes
assert "Grapes, grape juice and wine" in readme
assert "1.1.0" in roadmap
assert "1.1.0" in changelog
assert "The public release is still **1.0.0**" in readme
assert "no 1.1.0 tag or public publication yet" in checklist
assert "**Draft release notes — NOT YET PUBLISHED**" in notes
assert "- [ ] **Dedicated Fabric server:**" in checklist
assert "- [ ] **Packaging and version:**" in checklist

# No need to call or repackage a new version during readiness preparation.
print("OK: development Orchard 1.1.0 readiness files and real Fabric mod icon")
print("OK: confirmed worldgen probabilities, 65% fruiting and no grape feature")
print("OK: release branch stages 1.1.0; public main remains at 1.0.0 until merged")
print("OK: MC 26.2, Java 25, Apache-2.0 and third-party notice packaging configured")
