#!/usr/bin/env python3
"""Guard the decorative olive wall shelf; distinct from cheese-aging racks."""
import json
from pathlib import Path

R = Path("src/main/resources")
A = R / "assets/italiansdelight"
D = R / "data/italiansdelight"
J = Path("src/main/java/dev/italiansdelight")

def load(path):
    assert path.is_file(), f"Missing: {path}"
    return json.loads(path.read_text(encoding="utf-8"))

shelf = (J / "common/block/OliveShelfBlock.java").read_text()
registry = (J / "common/registry/ModBlocks.java").read_text()
tab = (J / "common/item/group/ModItemGroups.java").read_text()

assert "extends Block" in shelf and "simpleCodec(OliveShelfBlock::new)" in shelf
assert "BlockStateProperties.HORIZONTAL_FACING" in shelf
assert "candidate.canSurvive" in shelf and "isFaceSturdy" in shelf
assert "level.destroyBlock(pos, true)" in shelf
for token in ("getShape(", "getCollisionShape(", "StateDefinition.Builder",
              "return shape(state.getValue(FACING))", "Rotation rotation",
              "Mirror mirror", "case NORTH", "case SOUTH", "case EAST", "case WEST"):
    # Java's 'default -> NORTH' covers north without a separate case.
    if token != "case NORTH":
        assert token in shelf, token
assert "BlockEntity" not in shelf and "useItemOn" not in shelf

assert 'ModRegistries.id("olive_shelf")' in registry
assert "new OliveShelfBlock(" in registry
assert "fire.add(OLIVE_SHELF, 5, 20)" in registry
assert tab.count("output.accept(ModBlocks.OLIVE_SHELF);") == 1
assert "registerRack(\"olive\", OLIVE_PLANKS)" in registry
assert "OLIVE_CHEESE_AGING_RACK" in registry

blockstate = load(A / "blockstates/olive_shelf.json")
assert set(blockstate["variants"]) == {"facing=north", "facing=east",
                                     "facing=south", "facing=west"}
for direction, rotation in (("north",0),("east",90),("south",180),("west",270)):
    v = blockstate["variants"][f"facing={direction}"]
    assert v["model"] == "italiansdelight:block/olive_shelf"
    assert v.get("y",0) == rotation

model = load(A / "models/block/olive_shelf.json")
assert model["textures"] == {"wood":"italiansdelight:block/olive_planks",
                             "particle":"italiansdelight:block/olive_planks"}
assert len(model["elements"]) == 4
for element in model["elements"]:
    assert all(face["texture"] == "#wood" for face in element["faces"].values())
    assert all(0 <= v <= 16 for v in element["from"] + element["to"])
assert load(A/"models/item/olive_shelf.json")["parent"] == "italiansdelight:block/olive_shelf"
assert load(A/"items/olive_shelf.json")["model"]["model"] == "italiansdelight:item/olive_shelf"

recipe = load(D/"recipe/olive_shelf.json")
assert recipe["type"] == "minecraft:crafting_shaped"
assert recipe["pattern"] == ["PPP","S S"]
assert recipe["key"] == {"P":"italiansdelight:olive_planks","S":"minecraft:stick"}
assert recipe["result"] == {"id":"italiansdelight:olive_shelf","count":2}
loot = load(D/"loot_table/blocks/olive_shelf.json")
assert loot["pools"][0]["entries"][0]["name"] == "italiansdelight:olive_shelf"
assert "italiansdelight:olive_shelf" in load(R/"data/minecraft/tags/block/mineable/axe.json")["values"]
assert load(A/"lang/en_us.json")["block.italiansdelight.olive_shelf"] == "Olive Shelf"
assert load(A/"lang/it_it.json")["block.italiansdelight.olive_shelf"] == "Mensola d'Ulivo"

# The former functional cheese rack must remain intact.
assert load(A/"models/block/olive_cheese_aging_rack.json")["textures"]["wood"] == "italiansdelight:block/olive_planks"
assert load(D/"recipe/crafting/olive_cheese_aging_rack.json")["result"]["id"] == "italiansdelight:olive_cheese_aging_rack"

print("OK: wall-mounted olive decorative shelf: model, rotation, collision, crafting, drops, tags, translations")
print("OK: existing olive cheese-aging rack preserved")
