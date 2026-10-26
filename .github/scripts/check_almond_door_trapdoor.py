#!/usr/bin/env python3
"""Check 16px Almond door/trapdoor art, model mappings and vanilla behavior."""
import json
import struct
import zlib
from pathlib import Path
A=Path("src/main/resources/assets/italiansdelight")
D=Path("src/main/resources/data")
B=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text()
T=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text()
def js(path):
    assert path.is_file(), f"Missing {path}"
    return json.loads(path.read_text(encoding="utf8"))
def png(path):
    blob=path.read_bytes()
    assert blob[:8]==b"\x89PNG\r\n\x1a\n",path
    off=8;compressed=b"";ctype=None
    while off<len(blob):
        n=struct.unpack_from(">I",blob,off)[0];off+=4
        ch=blob[off:off+4];off+=4
        data=blob[off:off+n];off+=n
        crc=struct.unpack_from(">I",blob,off)[0];off+=4
        assert zlib.crc32(ch+data)&0xffffffff==crc,path
        if ch==b"IHDR":
            w,h,depth,ctype,*_=struct.unpack(">IIBBBBB",data)
            assert (w,h,depth,ctype)==(16,16,8,6),path
        if ch==b"IDAT":compressed+=data
        if ch==b"IEND":break
    decoded=zlib.decompress(compressed)
    prior=bytearray(64);at=0;pixels=[]
    for y in range(16):
        f=decoded[at];at+=1
        assert f in (0,1,2,3,4)
        coded=decoded[at:at+64];at+=64
        row=bytearray(64)
        for i in range(64):
            left=row[i-4] if i>=4 else 0
            up=prior[i]
            up_left=prior[i-4] if i>=4 else 0
            guess=left+up-up_left
            pa,pb,pc=abs(guess-left),abs(guess-up),abs(guess-up_left)
            pred=(0,left,up,(left+up)//2,
                  left if pa<=pb and pa<=pc else up if pb<=pc else up_left)[f]
            row[i]=(coded[i]+pred)&255
        pixels.extend(tuple(row[i:i+4]) for i in range(0,64,4))
        prior=row
    assert len(pixels)==256 and at==len(decoded),path
    return pixels
palette={p[:3] for p in png(A/"textures/block/almond_planks.png")}
imgs={}
for name,path in (
 ("door_top","textures/block/almond_door_top.png"),
 ("door_bottom","textures/block/almond_door_bottom.png"),
 ("trapdoor","textures/block/almond_trapdoor.png"),
 ("door_icon","textures/item/almond_door.png")):
    pixels=png(A/path);imgs[name]=pixels
    assert {p[:3] for p in pixels if p[3]}<=palette,name
    assert len(set(p for p in pixels if p[3]))>=6,name
for name in ("door_top","door_bottom","trapdoor"):
    assert all(p[3]==255 for p in imgs[name]),name
assert imgs["door_top"]!=imgs["door_bottom"]
assert imgs["door_top"]!=imgs["trapdoor"]
assert all(imgs["door_icon"][y*16+x][3]==0 for y in range(16) for x in (0,1,2,13,14,15))
assert sum(p[3]==255 for p in imgs["door_icon"])>=140

# ART REGRESSION: The blossom has been re-centered precisely at x=7.5,
# rather than offset toward the right. The two central columns and the
# inner engraved outline must mirror one another on *all three sprites*.
# Check the engraving itself (not the surrounding asymmetric wood hardware).
def pixel(name,x,y):
    return imgs[name][16*y+x][:3]
for name in ("door_top","door_bottom","trapdoor"):
    for y in (5,6,7,8,9,10):
        assert pixel(name,7,y)==pixel(name,8,y),(
            f"{name}: center of the almond-blossom carving drifted sideways"
        )
    for y in (6,9):
        assert pixel(name,6,y)==pixel(name,9,y),(
            f"{name}: the inner carved petal outline became asymmetric"
        )
    # Two-level beveled recessed panel, brighter raised upper/left
    # edge against the dark inset right/bottom bevel.
    assert pixel(name,3,3)!=pixel(name,12,12),(
        f"{name}: inner panel lost the illusion of depth"
    )
# Original 16x16 vanilla wood details: two door hinges at the left,
# an unobtrusive latch on the right and paired hardware on the trapdoor.
assert pixel("door_top",1,4)!=pixel("door_top",0,4)
assert pixel("door_bottom",1,10)!=pixel("door_bottom",0,10)
assert pixel("door_top",12,13)!=pixel("door_top",13,13),(
    "Missing contrasting wooden door handle/plate"
)
assert pixel("trapdoor",3,1)!=pixel("trapdoor",4,1)
assert pixel("trapdoor",11,1)!=pixel("trapdoor",12,1)
assert pixel("trapdoor",7,13)==pixel("trapdoor",8,13),(
    "The trapdoor pull must be centered on the 16-pixel middle"
)
for name in ("almond_door","almond_trapdoor"):
    assert f'ModRegistries.id("{name}")' in B
    assert T.count(f"output.accept(ModBlocks.{name.upper()});")==1
    assert f"fire.add({name.upper()}, 5, 20);" in B
    assert (f"new DoorBlock(ALMOND_BLOCK_SET_TYPE" if name.endswith("_door") else
            f"new TrapDoorBlock(ALMOND_BLOCK_SET_TYPE") in B
    for locale in ("en_us","it_it"):
        assert "block.italiansdelight."+name in js(A/f"lang/{locale}.json")
    assert "italiansdelight:"+name in js(D/"minecraft/tags/block/mineable/axe.json")["values"]
    cat="wooden_doors" if name=="almond_door" else "wooden_trapdoors"
    for kind in ("block","item"):
        assert "italiansdelight:"+name in js(D/f"minecraft/tags/{kind}/{cat}.json")["values"]
    rec=js(D/f"italiansdelight/recipe/{name}.json")
    assert rec["key"]["P"]=="italiansdelight:almond_planks"
    assert rec["result"]=={"id":"italiansdelight:"+name,"count":3 if name=="almond_door" else 2}
    assert rec["pattern"]==(["PP","PP","PP"] if name=="almond_door" else ["PPP","PPP"])
    js(D/f"italiansdelight/loot_table/blocks/{name}.json")
    js(A/f"items/{name}.json")
    js(A/f"models/item/{name}.json")
door=js(A/"blockstates/almond_door.json")["variants"]
trap=js(A/"blockstates/almond_trapdoor.json")["variants"]
assert len(door)==32 and len(trap)==16
for half in ("bottom","top"):
    for hinge in ("left","right"):
        for suffix in ("","_open"):
            m=js(A/f"models/block/almond_door_{half}_{hinge}{suffix}.json")
            assert m["parent"]==f"minecraft:block/door_{half}_{hinge}{suffix}"
            assert m["textures"]=={"bottom":"italiansdelight:block/almond_door_bottom",
                                   "top":"italiansdelight:block/almond_door_top"}
for part in ("bottom","top","open"):
    m=js(A/f"models/block/almond_trapdoor_{part}.json")
    assert m["textures"]=={"texture":"italiansdelight:block/almond_trapdoor"}
    assert m["parent"]==f"minecraft:block/template_orientable_trapdoor_{part}"
for facing,angle in (("north",0),("east",90),("south",180),("west",270)):
    for half in ("bottom","top"):
        for opened in ("false","true"):
            k=f"facing={facing},half={half},open={opened}"
            v=trap[k];part="open" if opened=="true" else half
            assert v["model"]==f"italiansdelight:block/almond_trapdoor_{part}"
            assert v.get("y",0)==angle,k
assert js(A/"models/item/almond_door.json")["textures"]["layer0"]=="italiansdelight:item/almond_door"
assert js(A/"models/item/almond_trapdoor.json")["parent"]=="italiansdelight:block/almond_trapdoor_bottom"
loot=js(D/"italiansdelight/loot_table/blocks/almond_door.json")
assert any(c.get("properties",{}).get("half")=="lower" for c in loot["pools"][0]["conditions"])
print("OK: Almond 16px door/trapdoor sprites use only approved plank palette and alpha")
print("OK: carved symbols are centered; recessed bevels, hinges and handles are present")
print("OK: 32/16 states, 11 models, item art, recipes, loot, tags and orientations")
