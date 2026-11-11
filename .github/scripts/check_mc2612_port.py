#!/usr/bin/env python3
"""Fail a 26.1.2 port if it contains 26.3-only Poplar assets or metadata."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java"
P = dict(line.split("=", 1) for line in (ROOT / "gradle.properties").read_text(
    encoding="utf-8").splitlines() if "=" in line and not line.startswith("#"))
for k, v in {
    "minecraft_version": "26.1.2",
    "loader_version": "0.20.1",
    "mod_version": "1.1.0-mc26.1.2",
    "fabric_version": "0.155.3+26.1.2",
    "fdrf_version": "26.1-3.6.30+refabricated",
}.items():
    assert P.get(k) == v, f"{k}: {P.get(k)!r} != {v!r}"
meta = json.loads((RES / "fabric.mod.json").read_text(encoding="utf-8"))
assert meta["depends"]["minecraft"] == "26.1.2"
assert meta["depends"]["java"] == ">=25"
assert meta["version"] == "${version}"
gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
for token in ("JavaVersion.VERSION_25", "it.options.release = 25",
              "jei-238222:9384703", "serene-seasons-291874:8351062"):
    assert token in gradle, token
for root in (SRC, RES):
    for path in root.rglob("*"):
        if not path.is_file():
            continue
        assert "poplar" not in path.name.lower(), f"26.3-only asset: {path}"
        if path.suffix in (".java", ".json", ".mcmeta"):
            assert "poplar" not in path.read_text(encoding="utf-8").lower(), (
                f"26.3-only reference: {path}")
for language in ("en_us", "it_it"):
    json.loads((RES / "assets/italiansdelight/lang" /
                f"{language}.json").read_text(encoding="utf-8"))
jei = (SRC / "dev/italiansdelight/integration/jei/ItaliansDelightJeiPlugin.java"
       ).read_text(encoding="utf-8")
begin = jei.index("for (net.minecraft.world.level.block.Block tub")
end = jei.index("}) {", begin)
assert jei[begin:end].count("_CRUSHING_TUB") == 15, "Wrong JEI tub count"
print("OK: MC 26.1.2, Fabric, FDR, JEI, optional Serene, Java 25")
print("OK: no 26.3 Poplar references; base + 14 Crushing Tub variants")
