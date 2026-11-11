#!/usr/bin/env python3
"""Validate Fruit Cherry building derivatives without touching approved artwork."""
import hashlib
import json
from pathlib import Path

R = Path("src/main/resources")
A = R / "assets/italiansdelight"
D = R / "data"
B = Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text(encoding="utf-8")
C = Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
names = ("stairs", "slab", "fence", "fence_gate", "button", "pressure_plate")
states = {"stairs":40,"slab":3,"fence":5,"fence_gate":16,"button":24,"pressure_plate":2}
tags = {"stairs":"wooden_stairs","slab":"wooden_slabs","fence":"wooden_fences",
        "fence_gate":"fence_gates","button":"wooden_buttons","pressure_plate":"wooden_pressure_plates"}

def read(path):
    assert path.is_file(), path
    return json.loads(path.read_text(encoding="utf-8"))

for n in names:
    key = "fruit_cherry_" + n
    rid = "italiansdelight:" + key
    assert 'ModRegistries.id("' + key + '")' in B
    assert C.count("output.accept(ModBlocks." + key.upper() + ");") == 1
    s = read(A / "blockstates" / (key+".json"))
    assert len(s["multipart"] if n == "fence" else s["variants"]) == states[n], n
    assert read(A / "items" / (key+".json"))["model"]["model"] == "italiansdelight:item/" + key
    assert read(A / "models/item" / (key+".json"))["parent"].startswith("italiansdelight:block/")
    assert read(D / "italiansdelight/loot_table/blocks" / (key+".json"))["pools"][0]["entries"][0]["name"] == rid
    assert read(D / "italiansdelight/recipe" / (key+".json"))["result"]["id"] == rid
    for kind in ("block", "item"):
        assert rid in read(D / f"minecraft/tags/{kind}/{tags[n]}.json")["values"]
    assert rid in read(D / "minecraft/tags/block/mineable/axe.json")["values"]
    assert f"fire.add({key.upper()}, 5, 20)" in B
    for lang in ("en_us", "it_it"):
        assert read(A / f"lang/{lang}.json")["block.italiansdelight."+key]

assert 'BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(ModRegistries.id("fruit_cherry"))' in B
assert 'new WoodTypeBuilder().register(ModRegistries.id("fruit_cherry"), FRUIT_CHERRY_BLOCK_SET_TYPE)' in B

slab = read(D / "italiansdelight/loot_table/blocks/fruit_cherry_slab.json")
assert any(f.get("function") == "minecraft:set_count" and f.get("count") == 2
    and any(c.get("properties", {}).get("type") == "double" for c in f.get("conditions", []))
    for f in slab["pools"][0]["entries"][0]["functions"])

# Validate every new block model uses ONLY the approved fruit cherry planks.
models = [p for p in (A/"models/block").glob("fruit_cherry_*.json")
          if any(p.stem == "fruit_cherry_"+n or p.stem.startswith("fruit_cherry_"+n+"_") for n in names)]
for path in models:
    model = read(path)
    for tex in model.get("textures", {}).values():
        if tex.startswith("italiansdelight:block/"):
            assert tex == "italiansdelight:block/fruit_cherry_planks", (path,tex)
assert len(models) >= 17

# Only the two user-requested Cherry PNGs are updated; remaining approved artwork is identical.
approved = {
    "fruit_cherry_log.png":"630180b174460c2c53e2a75d3b29efe0fa403da8",
    "stripped_fruit_cherry_log.png":"3ebad8726bf7a0fe64cd6ffde7546ef7e6dd2184",
    "fruit_cherry_planks.png":"d45def1e1a00bb93f2372f11b4dc79185ab343ff",
    "fruit_cherry_log_top.png":"7a44276cce4f6d3d30ebee10383c2649c45136f2",
    "stripped_fruit_cherry_log_top.png":"a95a0fade44f58854bbfd5560f41d2e4e7daef21",
}
for filename, expected in approved.items():
    payload = (A/"textures/block"/filename).read_bytes()
    sha = hashlib.sha1(b"blob " + str(len(payload)).encode("ascii") + b"\0" + payload).hexdigest()
    assert sha == expected, (filename, "approved texture changed", sha)

print("OK: six fruit cherry building derivatives, vanilla states, recipes, models, tags and loot")
print("OK: revised cherry bark/planks pinned; stripped wood and both cut ends remain unchanged")
