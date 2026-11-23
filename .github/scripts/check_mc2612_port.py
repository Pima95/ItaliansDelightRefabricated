#!/usr/bin/env python3
"""Check the 1.2.0 Minecraft 26.1.2 port without assuming 26.2 data codecs."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java"
props = dict(x.split("=", 1) for x in (ROOT / "gradle.properties").read_text().splitlines()
             if "=" in x and not x.lstrip().startswith("#"))
for key, value in {
    "minecraft_version": "26.1.2",
    "loader_version": "0.20.1",
    "mod_version": "1.2.0-mc26.1.2",
    "fabric_version": "0.155.3+26.1.2",
    "fdrf_version": "26.1-3.6.30+refabricated",
}.items():
    assert props.get(key) == value, (key, props.get(key))
meta = json.loads((RES / "fabric.mod.json").read_text())
assert meta["depends"]["minecraft"] == "~26.1.2"
assert meta["version"] == "${version}"
gradle = (ROOT / "build.gradle").read_text()
for tok in ("jei-238222:9384703", "serene-seasons-291874:8351062",
            "it.options.release = 25"):
    assert tok in gradle, tok
for parent in (SRC, RES):
    for path in parent.rglob("*"):
        if not path.is_file():
            continue
        assert "poplar" not in path.name.lower(), path
        if path.suffix in (".java", ".json", ".mcmeta"):
            assert "poplar" not in path.read_text(encoding="utf-8").lower(), path
loot = list((RES / "data/italiansdelight/loot_table").rglob("*.json"))
assert len(loot) >= 170, len(loot)
for path in loot:
    data = json.loads(path.read_text(encoding="utf-8"))
    def verify(obj):
        if isinstance(obj, list):
            for x in obj: verify(x)
        elif isinstance(obj, dict):
            assert "modifier" not in obj, path
            assert not isinstance(obj.get("condition"), dict), path
            for value in obj.values(): verify(value)
    verify(data)
for path in (RES / "data/italiansdelight/advancement").rglob("*.json"):
    adv = json.loads(path.read_text(encoding="utf-8"))
    for criterion in adv.get("criteria", {}).values():
        if criterion.get("trigger") == "minecraft:recipe_unlocked":
            assert "recipe" in criterion.get("conditions", {}), path
            assert "recipes" not in criterion.get("conditions", {}), path
assert (RES / "data/italiansdelight/worldgen/configured_feature/wild_grape_vine.json").is_file()
print("OK: 1.2.0 for MC 26.1.2, legacy loot and advancements, no Poplar, 26.1 dependencies")
