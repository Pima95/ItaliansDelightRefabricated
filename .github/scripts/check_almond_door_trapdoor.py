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
# Four restrained oxidized-iron tones make hinge pins and pulls *visible*.
# No invented new wood tint: metal is a tiny, physically located detail.
METAL={(77,76,71),(99,100,95),(145,147,137),(187,188,174)}
assert __import__("hashlib").sha256(
    (A/"textures/block/almond_planks.png").read_bytes()
).hexdigest()=="afef2d5ba69a3b978a58cfb60eb86f1a51a45165926560f58f13fa7a2c755322"
imgs={}
for name,path in (
 ("door_top","textures/block/almond_door_top.png"),
 ("door_bottom","textures/block/almond_door_bottom.png"),
 ("trapdoor","textures/block/almond_trapdoor.png"),
 ("door_icon","textures/item/almond_door.png")):
    pixels=png(A/path);imgs[name]=pixels
    assert {p[:3] for p in pixels if p[3]}<=palette|METAL,name
    assert len(set(p for p in pixels if p[3]))>=6,name
for name in ("door_top","door_bottom","trapdoor"):
    assert all(p[3]==255 for p in imgs[name]),name
assert imgs["door_top"]!=imgs["door_bottom"]
assert imgs["door_top"]!=imgs["trapdoor"]
assert all(imgs["door_icon"][y*16+x][3]==0 for y in range(16) for x in (0,1,2,13,14,15))
assert sum(p[3]==255 for p in imgs["door_icon"])==140
# 16x16 door item follows the exact *silhouette bounds* of vanilla oak
# and spruce: x 3..12, y 2..15, leaving upper two rows fully transparent.
for y in range(16):
    for x in range(16):
        expect=3<=x<=12 and 2<=y<=15
        assert (imgs["door_icon"][y*16+x][3]==255)==expect,(
            f"Door item did not match vanilla 10x14 silhouette at {x},{y}"
        )
# The previously accepted central symbol is kept verbatim for ALL 3
# front-face block sprites. This is a SHA of their actual core pixels.
import hashlib
accepted_core="df151e838d03722f56230b46f86fd5c91ba2bd293fa38cb3020db8b67657ecee"
for name in ("door_top","door_bottom","trapdoor"):
    area=bytes(v for y in range(5,11) for x in range(5,11)
               for v in imgs[name][y*16+x])
    assert hashlib.sha256(area).hexdigest()==accepted_core,(
        f"{name}: the user-approved emblem may not be changed"
    )
# Hardware is restricted to physical hinge / pull regions, never the
# ornament. Stronger contrast is intentional; this is NOT gray wood.
def metalcoords(name):
    return {(x,y) for y in range(16) for x in range(16)
            if imgs[name][y*16+x][:3] in METAL}
top_hinges={(x,y) for x in range(0,3) for y in range(3,7)}
bottom_hinges={(x,y) for x in range(0,3) for y in range(10,14)}
upper_grip={(x,y) for x in range(11,15) for y in range(12,16)}
lower_grip={(x,y) for x in range(12,15) for y in range(0,2)}
trap_hinges={(x,y) for x in (*range(2,5),*range(11,14)) for y in range(0,3)}
trap_pull={(x,y) for x in range(6,10) for y in range(12,15)}
assert metalcoords("door_top")<=top_hinges|upper_grip
assert metalcoords("door_bottom")<=bottom_hinges|lower_grip
assert metalcoords("trapdoor")<=trap_hinges|trap_pull
for name,a,b in (
    ("door_top",top_hinges,upper_grip),
    ("door_bottom",bottom_hinges,lower_grip),
    ("trapdoor",trap_hinges,trap_pull)):
    assert len(metalcoords(name)&a)>=9,name+" has no legible hinge"
    assert len(metalcoords(name)&b)>=5,name+" has no legible handle"
# The trapdoor's paired pins and lower pull are unmistakably present.
for center in (3,12):
    assert imgs["trapdoor"][1*16+center][:3]==(187,188,174)
assert imgs["trapdoor"][13*16+7][:3]==(187,188,174)
assert imgs["trapdoor"][13*16+8][:3]==(187,188,174)
# Item also includes two hinge highlights and its own latch.
assert metalcoords("door_icon")=={
    (3,5),(4,5),(3,12),(4,12),
    (11,8),(10,9),(11,9),(11,10)
}
# Continuous grain on beams prevents a full-width monochrome border.
assert len({imgs["door_top"][x] for x in range(16)})>=2
assert len({imgs["door_bottom"][15*16+x] for x in range(16)})>=3
assert len({imgs["trapdoor"][15*16+x] for x in range(16)})>=2

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
print("OK: approved emblem preserved pixel-perfect and all wooden areas use approved Almond palette")
print("OK: functional metal hinge pins & handles, recessed wood grain and vanilla 10x14 item silhouette")
print("OK: 32/16 states, 11 models, item art, recipes, loot, tags and orientations")
