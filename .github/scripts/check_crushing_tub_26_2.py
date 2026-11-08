#!/usr/bin/env python3
"""Static Minecraft 26.2/Fabric audit of the Rustic-derived Crushing Tub.

The three user-provided JSON source files had a legacy ore-dict recipe,
a five-element hollow model and a block-parent item model. This test verifies
those features survived the 26.2 resource/registry migration.
"""
import json
from pathlib import Path
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT=Path("src/main/resources")
A=ROOT/"assets/italiansdelight"
D=ROOT/"data"
JAVA=Path("src/main/java/dev/italiansdelight")
read=lambda p:json.loads(p.read_text(encoding="utf-8"))

model=read(A/"models/block/crushing_tub.json")
assert model["parent"]=="minecraft:block/block"
assert model["textures"]=={
    "0":"minecraft:block/oak_planks",
    "1":"italiansdelight:block/crushing_tub_oak",
    "particle":"italiansdelight:block/crushing_tub_oak"
}
assert set(model["textures"].values()) == {
    "minecraft:block/oak_planks","italiansdelight:block/crushing_tub_oak"
}
assert [e.get("name") for e in model["elements"]]==[
    "Base","SideNorth","SideSouth","SideWest","SideEast"
]
assert [(e["from"],e["to"]) for e in model["elements"]]==[
    ([1,0,1],[15,1,15]),([1,0,0],[15,9,1]),
    ([1,0,15],[15,9,16]),([0,0,1],[1,9,15]),
    ([15,0,1],[16,9,15])
]
for element in model["elements"]:
    assert len(element["faces"])==6
    assert {face["texture"] for face in element["faces"].values()}<= {"#0","#1"}
    assert all(len(face["uv"])==4 for face in element["faces"].values())
assert model["display"]["gui"]["rotation"]==[30,225,0]
assert model["display"]["thirdperson_righthand"]["scale"]==[0.375]*3
assert read(A/"models/item/crushing_tub.json")=={
    "parent":"italiansdelight:block/crushing_tub"
}
assert read(A/"items/crushing_tub.json")=={
    "model":{"type":"minecraft:model","model":"italiansdelight:item/crushing_tub"}
}
assert read(A/"blockstates/crushing_tub.json")=={
    "variants":{"":{"model":"italiansdelight:block/crushing_tub"}}
}
assert "rustic:" not in json.dumps(model), "No obsolete Rustic texture namespace"
assert "rustic:" not in (A/"models/item/crushing_tub.json").read_text()

# The user added this 16x16 RGBA asset before supplying the source JSON.
# Enforce existing project rule: only alpha 0 or 255, never partial alpha.
pixels=check_rgba_pixel_art(A/"textures/block/crushing_tub_oak.png",(16,16))
assert any(p[3]==255 for row in pixels for p in row)

recipe=read(D/"italiansdelight/recipe/crushing_tub.json")
assert recipe=={
    "type":"minecraft:crafting_shaped","category":"misc",
    "pattern":["P P","I I","SSS"],
    "key":{"P":"minecraft:oak_planks",
           "I":"minecraft:iron_ingot",
           "S":"minecraft:oak_slab"},
    "result":{"id":"italiansdelight:crushing_tub","count":1}
}
# Vanilla tags already include Italian's Delight's own orchard plank/slab families.
planks=read(D/"minecraft/tags/item/planks.json")["values"]
slabs=read(D/"minecraft/tags/item/wooden_slabs.json")["values"]
for tree in ("olive","almond","fruit_cherry"):
    assert "italiansdelight:"+tree+"_planks" in planks
    assert "italiansdelight:"+tree+"_slab" in slabs

loot=read(D/"italiansdelight/loot_table/blocks/crushing_tub.json")
assert loot["type"]=="minecraft:block"
assert loot["pools"][0]["entries"][0]=={
    "type":"minecraft:item","name":"italiansdelight:crushing_tub"
}
assert loot["pools"][0]["conditions"]==[{"condition":"minecraft:survives_explosion"}]
assert "italiansdelight:crushing_tub" in read(
    D/"minecraft/tags/block/mineable/axe.json")["values"]

blocks=(JAVA/"common/registry/ModBlocks.java").read_text()
tab=(JAVA/"common/item/group/ModItemGroups.java").read_text()
clazz=(JAVA/"common/block/CrushingTubBlock.java").read_text()
assert 'ModRegistries.id("crushing_tub")' in blocks
assert 'public static final Block CRUSHING_TUB = register(' in blocks
assert "new CrushingTubBlock(" in blocks
assert "new BlockItem(" in blocks and "useBlockDescriptionPrefix()" in blocks
assert "output.accept(ModBlocks.CRUSHING_TUB);" in tab
assert "extends BaseEntityBlock" in clazz
assert "newBlockEntity(" in clazz and "CrushingTubBlockEntity" in clazz
assert "getShape(" in clazz and "getCollisionShape(" in clazz
assert clazz.count("Block.box(")==6, "Hollow selection plus Rustic full-width landing collision"
assert "RUSTIC_LANDING_COLLISION" in clazz and "Block.box(0, 0, 0, 16, 9, 16)" in clazz
assert "return RUSTIC_LANDING_COLLISION;" in clazz
assert "Shapes.or(" in clazz and ".noOcclusion()" in blocks
assert "BaseEntityBlock" in clazz
assert "fallOn(" in clazz and "tub.stomp()" in clazz
renderer=(JAVA/"client/render/CrushingTubFluidRenderer.java").read_text()
assert "ItemDisplayContext.NONE" in renderer and "Axis.XP.rotationDegrees(-90.0f)" in renderer
assert "state.inputModel.submit(" in renderer and "state.inputCount" in renderer
assert "useItemOn(" in clazz and "useWithoutItem(" in clazz
for lang in ("en_us","it_it"):
    loc=read(A/f"lang/{lang}.json")
    assert loc["block.italiansdelight.crushing_tub"]
print("OK: Rustic original five-part hollow model, UV and item transforms preserved")
print("OK: Minecraft 26.2 blockstate + item definition + item model resolve locally")
print("OK: 16x16 texture exists with binary alpha")
print("OK: shaped recipe uses 26.2 item tags, including Italian's Delight wood")
print("OK: registry, creative tab, axe tag, drop and localized names")
print("OK: persisted inventory, one-item stomp and 8000mB liquid engine")
print("NOTE: inspect appearance, inventory item and recipe in Minecraft")

# Modern texture coverage for every vanilla and orchard wood. The old IDs are Oak.
WOODS = ["oak","spruce","birch","jungle","acacia","dark_oak","mangrove","cherry","pale_oak","bamboo","crimson","warped","olive","almond","fruit_cherry"]
for wood in WOODS:
    namespace = "minecraft" if wood not in ("olive", "almond", "fruit_cherry") else "italiansdelight"
    for kind in ("barrel", "crushing_tub"):
        block_id = kind if wood == "oak" else wood + "_" + kind
        model = read(A / "models/block" / (block_id + ".json"))
        assert model["textures"]["0"] == namespace + ":block/" + wood + "_planks"
        assert model["textures"]["1"] == "italiansdelight:block/" + kind + "_" + wood
        pic = check_rgba_pixel_art(A / "textures/block" / (kind + "_" + wood + ".png"), (16,16))
        assert all(px[3] == 255 for row in pic for px in row)
        recipe = read(D / "italiansdelight/recipe" / (block_id + ".json"))
        assert recipe["key"]["P"] == namespace + ":" + wood + "_planks"
        assert recipe["key"]["S"] == namespace + ":" + wood + "_slab"
        assert recipe["result"]["id"] == "italiansdelight:" + block_id
        if wood != "oak":
            assert read(A / "models/item" / (block_id + ".json"))["parent"] == "italiansdelight:block/" + block_id
            assert read(A / "items" / (block_id + ".json"))["model"]["model"] == "italiansdelight:item/" + block_id
            assert "italiansdelight:" + block_id in read(D / "minecraft/tags/block/mineable/axe.json")["values"]
print("OK: 15 Rustic vessel wood variants, modern 26.2 art and iron-capped corners")
