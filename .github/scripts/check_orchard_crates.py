#!/usr/bin/env python3
"""Static validation of all FDR-style Orchard crates and 9-to-1 recipes."""
import json
from pathlib import Path
from check_orchard_signs_and_cutting import check_rgba_pixel_art
A=Path("src/main/resources/assets/italiansdelight")
D=Path("src/main/resources/data")
J=Path("src/main/java/dev/italiansdelight/common")
crops={"olive":"olives","almond":"almonds","cherry":"cherries",
       "basil":"basil","cardoon":"cultivated_cardoon"}
blocks=(J/"registry/ModBlocks.java").read_text()
creative=(J/"item/group/ModItemGroups.java").read_text()
axe=json.loads((D/"minecraft/tags/block/mineable/axe.json").read_text())
for crop,item in crops.items():
    name=f"{crop}_crate";rid=f"italiansdelight:{name}"
    sym=f"{crop.upper()}_CRATE"
    assert blocks.count(f"public static final Block {sym} = register(")==1
    assert creative.count(f"output.accept(ModBlocks.{sym});")==1
    assert rid in axe["values"]
    assert json.loads((A/f"blockstates/{name}.json").read_text())=={
        "variants":{"":{"model":f"italiansdelight:block/{name}"}}}
    assert json.loads((A/f"items/{name}.json").read_text())=={
        "model":{"type":"minecraft:model","model":f"italiansdelight:block/{name}"}}
    assert json.loads((A/f"models/item/{name}.json").read_text())=={
        "parent":f"italiansdelight:block/{name}"}
    assert json.loads((A/f"models/block/{name}.json").read_text())=={
        "parent":"minecraft:block/cube_bottom_top",
        "textures":{"bottom":"farmersdelight:block/crate_bottom",
          "side":f"italiansdelight:block/{name}_side","top":f"italiansdelight:block/{name}_top"}}
    for face in ("side","top"):
        pix=check_rgba_pixel_art(A/f"textures/block/{name}_{face}.png",(16,16))
        assert all(p[3]==255 for row in pix for p in row)
    pack=json.loads((D/f"italiansdelight/recipe/{name}.json").read_text())
    unpack=json.loads((D/f"italiansdelight/recipe/{item}_from_crate.json").read_text())
    assert pack=={"type":"minecraft:crafting_shaped","category":"building",
      "pattern":["###"]*3,"key":{"#":f"italiansdelight:{item}"},"result":{"id":rid}}
    assert unpack=={"type":"minecraft:crafting_shapeless","category":"misc",
      "ingredients":[rid],"result":{"id":f"italiansdelight:{item}","count":9}}
    loot=json.loads((D/f"italiansdelight/loot_table/blocks/{name}.json").read_text())
    assert loot["pools"][0]["entries"]==[{"type":"minecraft:item","name":rid}]
    assert {"condition":"minecraft:survives_explosion"} in loot["pools"][0]["conditions"]
    for lang in ("it_it","en_us"):
        assert json.loads((A/f"lang/{lang}.json").read_text())[f"block.italiansdelight.{name}"]
    for kind in ("item","block"):
        specific=json.loads((D/f"c/tags/{kind}/storage_blocks/{crop}.json").read_text())
        general=json.loads((D/f"c/tags/{kind}/storage_blocks.json").read_text())
        assert specific["values"]==[rid] and rid in general["values"]
pix=check_rgba_pixel_art(A/"textures/item/cherries.png",(16,16))
assert all(p[3] in (0,255) for row in pix for p in row)
assert sum(p[3]==255 for row in pix for p in row)==76
print("OK: five registered crates, ten opaque textures, block/item assets, drops, tags")
print("OK: 9-in and 9-out recipes for olives, almonds, cherries, basil and cultivated cardoon")
print("OK: smaller 76-pixel cherry sprite retained with alpha 0/255")
