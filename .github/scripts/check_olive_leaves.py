#!/usr/bin/env python3
"""Orchard 1.1.0: enforce non-Rustic olive leaves, vanilla data and drop mechanics."""
from pathlib import Path
import json
import struct

ROOT=Path("src/main/resources")
A=ROOT/"assets/italiansdelight"
D=ROOT/"data"
BLOCK="italiansdelight:olive_leaves"

def read(path):
    if not path.is_file():raise ValueError("Missing "+str(path))
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    src=(A/"textures/block/olive_leaves.png")
    data=src.read_bytes()
    if data[:8]!=b"\x89PNG\r\n\x1a\n" or struct.unpack(">II",data[16:24])!=(16,16):
        raise ValueError("Approved olive leaves must be a 16x16 PNG")
    old=A/"textures/block/leaves_olive.png"
    if old.exists():
        raise ValueError("Rustic leaves_olive.png must be removed from the Orchard branch")
    model=read(A/"models/block/olive_leaves.json")
    if model != {"parent":"minecraft:block/leaves",
                  "textures":{"all":"italiansdelight:block/olive_leaves"}}:
        raise ValueError("Olive leaves must use vanilla leaves geometry and approved art")
    state=read(A/"blockstates/olive_leaves.json")
    assert state=={"variants":{"":{"model":"italiansdelight:block/olive_leaves"}}}
    item=read(A/"items/olive_leaves.json")
    assert item["model"]["model"]=="italiansdelight:item/olive_leaves"
    assert read(A/"models/item/olive_leaves.json")["parent"]=="italiansdelight:block/olive_leaves"
    for type in ("block","item"):
        tag=read(D/"minecraft/tags"/type/"leaves.json")
        assert BLOCK in tag["values"]
    # No dedicated mineable:hoe needed: vanilla #minecraft:mineable/hoe
    # references #minecraft:leaves, which is extended above.
    logs=read(D/"minecraft/tags/block/logs_that_burn.json")
    assert all("italiansdelight:"+name in logs["values"]
               for name in ("olive_log","olive_wood","stripped_olive_log","stripped_olive_wood"))
    srcjava=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text()
    assert "new LeavesBlock(" in srcjava and "OLIVE_LEAVES" in srcjava
    assert "fire.add(OLIVE_LEAVES, 30, 60)" in srcjava
    group=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text()
    assert group.count("output.accept(ModBlocks.OLIVE_LEAVES);")==1
    loot=read(D/"italiansdelight/loot_table/blocks/olive_leaves.json")
    drop=loot["pools"][0]["entries"][0]
    assert drop["name"]==BLOCK
    tool=drop["conditions"][0]
    assert tool["condition"]=="minecraft:any_of"
    assert any(t.get("predicate",{}).get("items")=="minecraft:shears" for t in tool["terms"])
    assert any(t.get("predicate",{}).get("predicates",{}).get("minecraft:enchantments")
               for t in tool["terms"])
    assert any(e.get("name")=="minecraft:stick" for pool in loot["pools"][1:] for e in pool["entries"])
    assert not any(e.get("name","").endswith(("sapling","apple","olives"))
                   for pool in loot["pools"] for e in pool["entries"])
    print("OK: real olive LeavesBlock registered; textures and model use approved non-Rustic 16x16 art")
    print("OK: vanilla distance via logs_that_burn, leaf tags, fire, Silk Touch/shears, sticks, no fruits/saplings")
    print("OK: Rustic leaves_olive.png removed from Orchard branch; no automatic user test claimed")

if __name__=="__main__":
    main()
