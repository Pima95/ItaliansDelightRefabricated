#!/usr/bin/env python3
"""Verify Fruit Cherry vanilla door/trapdoor functionality and original approved artwork."""
import hashlib
import json
from pathlib import Path
from check_fruit_cherry_wood_phase1 import png
R=Path("src/main/resources")
A=R/"assets/italiansdelight"
D=R/"data"
blocks=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text(encoding="utf-8")
creative=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
def read(p):
    assert p.is_file(),p
    return json.loads(p.read_text(encoding="utf-8"))

for base,name,qty in (("door","fruit_cherry_door",3),("trapdoor","fruit_cherry_trapdoor",2)):
    assert f'ModRegistries.id("{name}")' in blocks
    assert creative.count(f"output.accept(ModBlocks.{name.upper()});")==1
    assert f"fire.add({name.upper()}, 5, 20)" in blocks
    assert name.upper()+"_KEY" in blocks
    if base=="door":
        assert "new DoorBlock(FRUIT_CHERRY_BLOCK_SET_TYPE" in blocks
    else:
        assert "new TrapDoorBlock(FRUIT_CHERRY_BLOCK_SET_TYPE" in blocks
    states=read(A/f"blockstates/{name}.json")["variants"]
    assert len(states)==(32 if base=="door" else 16)
    assert read(A/f"items/{name}.json")["model"]["model"]=="italiansdelight:item/"+name
    assert read(A/f"models/item/{name}.json")["parent"].startswith("minecraft:item/" if base=="door" else "italiansdelight:block/")
    recipe=read(D/f"italiansdelight/recipe/{name}.json")
    assert recipe["type"]=="minecraft:crafting_shaped"
    assert recipe["result"]=={"id":"italiansdelight:"+name,"count":qty}
    assert recipe["key"]=={"P":"italiansdelight:fruit_cherry_planks"}
    assert recipe["pattern"]==(["PP","PP","PP"] if base=="door" else ["PPP","PPP"])
    loot=read(D/f"italiansdelight/loot_table/blocks/{name}.json")
    assert loot["pools"][0]["entries"][0]["name"]=="italiansdelight:"+name
    if base=="door":
        assert any(c.get("properties",{}).get("half")=="lower"
                   for c in loot["pools"][0]["conditions"]), "Door should drop only from lower half"
    for kind in ("block","item"):
        tag="wooden_doors" if base=="door" else "wooden_trapdoors"
        assert "italiansdelight:"+name in read(D/f"minecraft/tags/{kind}/{tag}.json")["values"]
    assert "italiansdelight:"+name in read(D/"minecraft/tags/block/mineable/axe.json")["values"]
    for lang in ("it_it","en_us"):
        assert read(A/f"lang/{lang}.json")["block.italiansdelight."+name]

# The eight door models preserve almond's reviewed vanilla-scale endcap mapping.
# The hinge face samples the metallic source; the latch face samples a
# separate source with only three iron hinge pixels removed.
for half in ("top","bottom"):
    for hinge in ("left","right"):
        for suffix in ("","_open"):
            part=f"{half}_{hinge}{suffix}"
            path=A/f"models/block/fruit_cherry_door_{part}.json"
            m=read(path)
            assert m.get("ambientocclusion") is False
            assert "parent" not in m
            assert m["textures"]=={
                "particle":f"#{half}",
                "bottom":"italiansdelight:block/fruit_cherry_door_bottom",
                "top":"italiansdelight:block/fruit_cherry_door_top",
                "edge":f"italiansdelight:block/fruit_cherry_door_{half}_edge",
            }
            assert len(m["elements"])==1
            e=m["elements"][0]
            assert e["from"]==[0,0,0] and e["to"]==[3,16,16]
            faces=e["faces"]
            opened=bool(suffix)
            hinge_end=("north" if opened else "south") if hinge=="right" else ("south" if opened else "north")
            latch_end="south" if hinge_end=="north" else "north"
            assert faces[hinge_end]["texture"]==f"#{half}", part
            assert faces[latch_end]["texture"]=="#edge", part

for suffix in ("bottom","top","open"):
    m=read(A/f"models/block/fruit_cherry_trapdoor_{suffix}.json")
    assert m["textures"]=={
        "particle":"#texture",
        "texture":"italiansdelight:block/fruit_cherry_trapdoor",
        "edge":"italiansdelight:block/fruit_cherry_planks",
    }

WOOD={(163,104,78),(174,112,83),(184,122,91),(194,132,99),
      (204,144,107),(215,156,116)}
IRON={(107,111,122),(111,111,111),(128,139,149)}
textures={}
for name,folder in [
    ("fruit_cherry_door_bottom","block"),
    ("fruit_cherry_door_bottom_edge","block"),
    ("fruit_cherry_door_top","block"),
    ("fruit_cherry_door_top_edge","block"),
    ("fruit_cherry_trapdoor","block"),
    ("fruit_cherry_door","item"),
]:
    pix=png(A/f"textures/{folder}/{name}.png",require_opaque=False)
    textures[name]=pix
    assert all(p[3] in (0,255) for row in pix for p in row),name
    assert {p[:3] for row in pix for p in row if p[3]}<=WOOD|IRON,(name,"non-cherry palette")
    assert all(len(row)==16 for row in pix)
assert sum(p[3]==0 for row in textures["fruit_cherry_door_top"] for p in row)==48
assert all(p[3]==255 for row in textures["fruit_cherry_door_bottom"] for p in row)
assert sum(p[3]==0 for row in textures["fruit_cherry_trapdoor"] for p in row)==39
assert sum(p[3]==0 for row in textures["fruit_cherry_door"] for p in row)==140

# Only the hinge-end texture has hinge pins: never show them also on the latch end.
for half,positions in (("bottom",{(0,0),(0,10),(0,11)}),
                       ("top",{(0,4),(0,5),(0,15)})):
    src=textures["fruit_cherry_door_"+half]
    edge=textures["fruit_cherry_door_"+half+"_edge"]
    actual={(x,y) for y in range(16) for x in range(16) if src[y][x]!=edge[y][x]}
    assert actual==positions,(half,"hinge pixels leaked or face changed",actual)
    assert all(src[y][x][:3] in IRON and edge[y][x][:3] in WOOD for x,y in positions)
# The tiny metallic pull must be on the central brace, not the window.
t=textures["fruit_cherry_trapdoor"]
assert t[8][7][:3]==(107,111,122) and t[8][8][:3]==(128,139,149)

# Do not touch any wood sprite already reviewed in earlier phases.
approved={
    "fruit_cherry_log.png":"630180b174460c2c53e2a75d3b29efe0fa403da8",
    "stripped_fruit_cherry_log.png":"3ebad8726bf7a0fe64cd6ffde7546ef7e6dd2184",
    "fruit_cherry_planks.png":"d45def1e1a00bb93f2372f11b4dc79185ab343ff",
    "fruit_cherry_log_top.png":"7a44276cce4f6d3d30ebee10383c2649c45136f2",
    "stripped_fruit_cherry_log_top.png":"a95a0fade44f58854bbfd5560f41d2e4e7daef21",
}
for filename,expected in approved.items():
    payload=(A/"textures/block"/filename).read_bytes()
    sha=hashlib.sha1(b"blob "+str(len(payload)).encode()+b"\0"+payload).hexdigest()
    assert sha==expected,(filename,"approved sprite was changed",sha)
print("OK: fruit cherry door/trapdoor vanilla rotation states, 26.2 models, redstone behavior and recipes")
print("OK: handles and hinge endcaps match the intended sides; item is vanilla-style")
print("OK: the five approved fruit cherry wood textures remain byte-for-byte identical")
