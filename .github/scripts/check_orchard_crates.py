#!/usr/bin/env python3
"""Static validation of all FDR-style Orchard crates and 9-to-1 recipes."""
import json
import hashlib
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
        wood={(0x78,0x58,0x36),(0x80,0x5e,0x36),(0x8b,0x67,0x3c),
              (0x55,0x3a,0x1f),(0x40,0x29,0x13),(0x99,0x71,0x40),
              (0x66,0x4d,0x28)}
        assert 80 <= sum(p[:3] in wood for row in pix for p in row) <= 236
        assert len({p[:3] for row in pix for p in row}) >= 10
        # FDR crate frame/plank pixel sequence is unchanged, including its shading.
        # The three art retouches are allowed to alter crop pixels ONLY.
        if crop in ("basil","cardoon","cherry"):
            raw=bytes(c for row in pix for p in row if p[:3] in wood for c in p)
            expected={"top":"07a09e121961a20a2880988d57c7d0c1c59b6b2bc5939d801fbf35049496931b",
                      "side":"095121d70793c963faf378a9562207f451d2242efec7232d5e452f21d7823129"}
            assert hashlib.sha256(raw).hexdigest()==expected[face], (crop,face,"FDR wood changed")
            crops=[p[:3] for row in pix for p in row if p[:3] not in wood]
            if crop=="basil":
                # Basil is green, with thin stems and leaves: no cabbage-derived color palette.
                assert sum(g>r*1.2 and g>b*1.05 for r,g,b in crops)>=20
            elif crop=="cardoon":
                # Delicate lilac flower tips and silver-green ribbed cardoon leaves.
                assert sum(r>b*0.75 and b>g*1.15 for r,g,b in crops)>=2
            elif face=="top":
                # Cherries are separate 2-pixel-class berries, not tomato-sized red disks.
                berry={(x,y) for y in range(2,14) for x in range(2,14)
                       if (lambda p:p[0]>p[1]*1.45 and p[0]>p[2]*1.25 and p[0]>=78)(pix[y][x][:3])}
                assert 25<=len(berry)<=45
                assert max(sum((xx,yy) in berry for xx in range(x,x+3) for yy in range(y,y+3))
                           for x in range(2,12) for y in range(2,12))<9

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
assert sum(p[3]==255 for row in pix for p in row)==77
assert pix[8][4][3]==255 and pix[8][10][3]==255
assert pix[8][11][3]==0 and pix[14][11][3]==255
# One berry must begin lower, but the upper foliage remains unchanged.
assert pix[9][11][:3]!=pix[8][11][:3]
print("OK: five registered crates, ten opaque textures, block/item assets, drops, tags")
print("OK: 9-in and 9-out recipes for olives, almonds, cherries, basil and cultivated cardoon")
print("OK: cherry berry lowered 1px (77 opaque pixels) while size remains unchanged")
print("OK: faithful FDR wood, basil leaves, cardoon purple buds, tiny cherry pairs")
