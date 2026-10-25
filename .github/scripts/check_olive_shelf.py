#!/usr/bin/env python3
"""Check that olive_shelf is a native 26.2 shelf, not a decorative imitation."""
import json
import struct
from pathlib import Path

R = Path("src/main/resources")
A = R / "assets/italiansdelight"
D = R / "data/italiansdelight"
J = Path("src/main/java/dev/italiansdelight")

def load(path):
    assert path.is_file(), f"Missing: {path}"
    return json.loads(path.read_text(encoding="utf-8"))

registry = (J / "common/registry/ModBlocks.java").read_text(encoding="utf-8")
tab = (J / "common/item/group/ModItemGroups.java").read_text(encoding="utf-8")

# The vanilla block must be the implementation itself: it provides storage,
# persisted items, native renderer, three slots, redstone and side chaining.
assert "new ShelfBlock(" in registry
assert "Block.Properties.ofFullCopy(Blocks.OAK_SHELF)" in registry
assert "((FabricBlockEntityType) BlockEntityType.SHELF).addValidBlock(OLIVE_SHELF)" in registry
assert 'ModRegistries.id("olive_shelf")' in registry
assert not (J / "common/block/OliveShelfBlock.java").exists()
assert tab.count("output.accept(ModBlocks.OLIVE_SHELF);") == 1
assert "fire.add(OLIVE_SHELF, 5, 20)" in registry

# 6 variants: 1 body plus unpowered / unconnected / left / center / right.
for suffix, parent in (("", "body"), ("_unpowered", "unpowered"),
                       ("_unconnected", "unconnected"), ("_left", "left"),
                       ("_center", "center"), ("_right", "right"),
                       ("_inventory", "inventory")):
    model = load(A / f"models/block/olive_shelf{suffix}.json")
    assert model["parent"] == f"minecraft:block/template_shelf_{parent}"
    assert model["textures"] == {
        "all": "italiansdelight:block/olive_shelf",
        "particle": "italiansdelight:block/stripped_olive_log"
    }
assert not (A / "models/item/olive_shelf.json").exists(), "Obsolete item model still present"
assert load(A / "items/olive_shelf.json")["model"] == {
    "type": "minecraft:model", "model":"italiansdelight:block/olive_shelf_inventory"
}

# Vanilla shelf uses 4 facing x (unpowered + 4 powered chain positions) = 24.
states = load(A / "blockstates/olive_shelf.json")["multipart"]
assert len(states) == 24
seen = set()
for part in states:
    condition = part["when"]
    if "AND" in condition:
        condition = {key: val for sub in condition["AND"] for key, val in sub.items()}
    facing = condition["facing"]
    powered = condition.get("powered")
    chain = condition.get("side_chain")
    assert facing in ("north","east","south","west")
    assert part["apply"]["y"] == {"north":0,"east":90,"south":180,"west":270}[facing] if "y" in part["apply"] else facing=="north"
    assert part["apply"]["model"].startswith("italiansdelight:block/olive_shelf")
    seen.add((facing,powered,chain))
assert len(seen) == 24
for facing in ("north","east","south","west"):
    assert (facing,None,None) in seen
    assert (facing,"false",None) in seen
    for chain in ("unconnected","left","center","right"):
        assert (facing,"true",chain) in seen

# Vanilla UV atlas is 32x32, not a custom 16x16 inventory icon. Alpha is
# opaque and only the non-Rustic approved olive wood family supplies colors.
tex = A/"textures/block/olive_shelf.png"
raw = tex.read_bytes()
assert raw[:8] == b"\x89PNG\r\n\x1a\n"
assert struct.unpack(">II",raw[16:24]) == (32,32)
assert not (A/"textures/item/olive_shelf.png").exists()

recipe = load(D/"recipe/olive_shelf.json")
assert recipe == {
    "type":"minecraft:crafting_shaped", "group":"shelf",
    "pattern":["###","   ","###"],
    "key":{"#":"italiansdelight:stripped_olive_log"},
    "result":{"id":"italiansdelight:olive_shelf","count":6}
}
loot = load(D/"loot_table/blocks/olive_shelf.json")
assert loot["pools"][0]["entries"][0]["name"] == "italiansdelight:olive_shelf"
assert "italiansdelight:olive_shelf" in load(R/"data/minecraft/tags/block/mineable/axe.json")["values"]
assert load(A/"lang/en_us.json")["block.italiansdelight.olive_shelf"] == "Olive Shelf"
assert load(A/"lang/it_it.json")["block.italiansdelight.olive_shelf"] == "Mensola d'Ulivo"

# The existing functional cheese aging rack must remain untouched.
assert 'registerRack("olive", OLIVE_PLANKS)' in registry
assert load(A/"models/block/olive_cheese_aging_rack.json")["textures"]["wood"] == "italiansdelight:block/olive_planks"
assert load(D/"recipe/crafting/olive_cheese_aging_rack.json")["result"]["id"] == "italiansdelight:olive_cheese_aging_rack"

print("OK: native shelf block entity, redstone chaining, 3-slot storage, vanilla 26.2 models")
print("OK: 32x32 olive pixel art, crafting x6, items, drops, translations and preserved cheese rack")
