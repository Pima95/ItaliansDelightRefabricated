#!/usr/bin/env python3
"""Verify first eight Almond wooden derivatives use ONLY approved textures."""
from pathlib import Path
import json
R=Path("src/main/resources")
A=R/"assets/italiansdelight"
D=R/"data"
B=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text()
T=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text()
NAMES=("almond_wood","stripped_almond_wood","almond_stairs","almond_slab",
       "almond_fence","almond_fence_gate","almond_button","almond_pressure_plate")
COUNTS={"almond_wood":3,"stripped_almond_wood":3,"almond_stairs":40,
        "almond_slab":3,"almond_fence_gate":16,"almond_button":24,
        "almond_pressure_plate":2}
ALLOWED={"almond_log","almond_log_top","stripped_almond_log",
         "stripped_almond_log_top","almond_planks","almond_door_bottom","almond_door_top","almond_door_bottom_edge",
         "almond_door_top_edge","almond_trapdoor",
         "almond_leaves","almond_sapling"}
def j(p):
    assert p.is_file(),f"Missing {p}"
    return json.loads(p.read_text(encoding="utf-8"))
for name in NAMES:
    assert f'ModRegistries.id("{name}")' in B
    assert T.count(f"output.accept(ModBlocks.{name.upper()});")==1,name
    block=j(A/"blockstates"/(name+".json"))
    if name in COUNTS:assert len(block["variants"])==COUNTS[name],name
    else:assert len(block["multipart"])==5,name
    assert j(A/"items"/(name+".json"))["model"]["model"]=="italiansdelight:item/"+name
    assert j(A/"models/item"/(name+".json"))["parent"].startswith("italiansdelight:block/")
    loot=j(D/"italiansdelight/loot_table/blocks"/(name+".json"))
    assert loot["pools"][0]["entries"][0]["name"]=="italiansdelight:"+name,name
    assert j(D/"italiansdelight/recipe"/(name+".json"))["result"]["id"]=="italiansdelight:"+name
    assert "italiansdelight:"+name in j(D/"minecraft/tags/block/mineable/axe.json")["values"]
    for loc in ("en_us","it_it"):
        assert f"block.italiansdelight.{name}" in j(A/"lang"/(loc+".json"))
    assert "fire.add("+name.upper()+"," in B
assert "StrippableBlockRegistry.register(ALMOND_WOOD, STRIPPED_ALMOND_WOOD)" in B
assert "ALMOND_BLOCK_SET_TYPE" in B and "ALMOND_WOOD_TYPE" in B
for k in ("block","item"):
    for tag,n in (("wooden_stairs","almond_stairs"),("wooden_slabs","almond_slab"),
                  ("wooden_fences","almond_fence"),("fence_gates","almond_fence_gate"),
                  ("wooden_buttons","almond_button"),("wooden_pressure_plates","almond_pressure_plate")):
        assert "italiansdelight:"+n in j(D/f"minecraft/tags/{k}/{tag}.json")["values"]
    for tag in (D/f"minecraft/tags/{k}/logs_that_burn.json",D/f"italiansdelight/tags/{k}/almond_logs.json"):
        assert {"italiansdelight:almond_wood","italiansdelight:stripped_almond_wood"} <= set(j(tag)["values"])
for p in (A/"models/block").glob("*almond*.json"):
    data=j(p)
    textures=data.get("textures",{})
    for key,reference in textures.items():
        # Vanilla-style models can alias another texture variable, e.g.
        # "particle": "#texture". Resolve aliases, rejecting cycles and
        # missing targets instead of treating the reference as a PNG path.
        seen={key}
        ref=reference
        while ref.startswith("#"):
            alias=ref[1:]
            assert alias in textures and alias not in seen,(p,ref)
            seen.add(alias)
            ref=textures[alias]
        assert ref.startswith("italiansdelight:block/"),(p,ref)
        assert ref.rsplit("/",1)[-1] in ALLOWED,(p,ref)
        assert (A/"textures/block"/(ref.rsplit("/",1)[-1]+".png")).is_file(),(p,ref)
slab=j(D/"italiansdelight/loot_table/blocks/almond_slab.json")
assert any(f.get("function")=="minecraft:set_count" and f.get("count")==2
           and any(c.get("properties",{}).get("type")=="double" for c in f.get("conditions",[]))
           for f in slab["pools"][0]["entries"][0]["functions"])
for name in ("almond_log","almond_wood"):
    data=j(D/"italiansdelight/recipe/cutting"/(name+".json"))
    assert data["type"]=="farmersdelight:cutting"
    assert data["ingredients"]==["italiansdelight:"+name]
    assert any(x.get("item",{}).get("id")=="farmersdelight:tree_bark" for x in data["result"])
for name in ("almond_shelf",):
    assert f'ModRegistries.id("{name}")' not in B
print("OK: eight Almond wood derivatives with vanilla states, crafting, loot, tags and FD Cutting Board")
print("OK: models use only previously approved Almond wood PNGs")
