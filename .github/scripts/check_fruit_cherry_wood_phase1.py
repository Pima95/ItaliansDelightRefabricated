#!/usr/bin/env python3
"""Audit the first original fruit cherry wood family (16x16 PNG, vanilla 26.2)."""
from pathlib import Path
import json,struct,zlib
root=Path("src/main/resources")
a=root/"assets/italiansdelight"
d=root/"data"
def load(path):
    assert path.is_file(),path
    return json.loads(path.read_text(encoding="utf-8"))
def png(path):
    raw=path.read_bytes()
    assert raw.startswith(b"\x89PNG\r\n\x1a\n")
    assert struct.unpack(">IIBBBBB",raw[16:29])==(16,16,8,6,0,0,0)
    pos=8;parts=[]
    while pos<len(raw):
        n=struct.unpack(">I",raw[pos:pos+4])[0]
        typ=raw[pos+4:pos+8];value=raw[pos+8:pos+8+n]
        assert zlib.crc32(typ+value)==struct.unpack(">I",raw[pos+8+n:pos+12+n])[0]
        parts.append((typ,value));pos+=12+n
    assert pos==len(raw)
    z=zlib.decompress(b"".join(v for k,v in parts if k==b"IDAT"))
    rows=[];prev=bytearray(64)
    for y in range(16):
        filt=z[y*65];s=z[y*65+1:y*65+65];cur=bytearray(64)
        assert filt in range(5)
        for i,enc in enumerate(s):
            left=cur[i-4] if i>=4 else 0
            above=prev[i];ul=prev[i-4] if i>=4 else 0
            p=left+above-ul
            paeth=(left,above,ul)[[abs(p-left),abs(p-above),abs(p-ul)].index(min(abs(p-left),abs(p-above),abs(p-ul)))]
            predictor=(0,left,above,(left+above)//2,paeth)[filt]
            cur[i]=(enc+predictor)&255
        rows.append(tuple(tuple(cur[i:i+4]) for i in range(0,64,4)))
        prev=cur
    assert all(px[3]==255 for row in rows for px in row),path
    assert len({px[:3] for row in rows for px in row})>=4,path
    return rows
tex=("fruit_cherry_planks","fruit_cherry_log","fruit_cherry_log_top","stripped_fruit_cherry_log","stripped_fruit_cherry_log_top")
pixels={k:png(a/f"textures/block/{k}.png") for k in tex}
# Distinctive vanilla-scale fruit cherry artwork: fine horizontal bark
# lenticels and lighter continuous stripped grain, both opaque 16x16.
bark=pixels["fruit_cherry_log"]
stripped=pixels["stripped_fruit_cherry_log"]
assert len({p[:3] for row in bark for p in row})<=8, "Bark palette too noisy"
assert len({p[:3] for row in stripped for p in row})<=10, "Stripped palette too noisy"
assert sum(bark[y][x][0]<=91 and bark[y][x+1][0]<=91
           for y in range(16) for x in range(15))>=6, "Horizontal bark lenticels missing"
assert sum(p[0] for row in stripped for p in row)>sum(p[0] for row in bark for p in row), (
    "Stripped fruit cherry must be lighter than bark"
)

normal,skinned=pixels["fruit_cherry_log_top"],pixels["stripped_fruit_cherry_log_top"]
assert all(normal[y][x]==skinned[y][x] for y in range(1,15) for x in range(1,15))
assert any(normal[y][x]!=skinned[y][x] for y in range(16) for x in range(16) if x in (0,15) or y in (0,15))
blocks=("fruit_cherry_log","fruit_cherry_wood","stripped_fruit_cherry_log","stripped_fruit_cherry_wood","fruit_cherry_planks")
java=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text()
creative=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text()
for n in blocks:
    assert f'ModRegistries.id("{n}")' in java
    assert creative.count("output.accept(ModBlocks."+n.upper()+");")==1
    for sub in ("blockstates","models/block","models/item","items"):
        assert load(a/f"{sub}/{n}.json")
    assert load(d/f"italiansdelight/loot_table/blocks/{n}.json")["pools"][0]["entries"][0]["name"]=="italiansdelight:"+n
    assert "italiansdelight:"+n in load(d/"minecraft/tags/block/mineable/axe.json")["values"]
    for lang in ("it_it","en_us"):
        assert "block.italiansdelight."+n in load(a/f"lang/{lang}.json")
for kind in ("block","item"):
    logs=set(load(d/f"italiansdelight/tags/{kind}/fruit_cherry_logs.json")["values"])
    assert logs=={"italiansdelight:"+n for n in blocks if n!="fruit_cherry_planks"}
    assert logs<=set(load(d/f"minecraft/tags/{kind}/logs_that_burn.json")["values"])
assert "italiansdelight:fruit_cherry_planks" in load(d/"minecraft/tags/item/planks.json")["values"]
for n in ("fruit_cherry_planks","fruit_cherry_wood","stripped_fruit_cherry_wood"):
    assert load(d/f"italiansdelight/recipe/{n}.json")["result"]["id"]=="italiansdelight:"+n
for a,b in (("FRUIT_CHERRY_LOG","STRIPPED_FRUIT_CHERRY_LOG"),("FRUIT_CHERRY_WOOD","STRIPPED_FRUIT_CHERRY_WOOD")):
    assert f"StrippableBlockRegistry.register({a}, {b})" in java
for n in blocks:
    assert "fire.add("+n.upper()+", 5, "+("20" if n=="fruit_cherry_planks" else "5")+")" in java
print("OK: five original fruit cherry blocks, 5 PNGs, trunk rings, vanilla models")
print("OK: creative, axe stripping, flammability, crafting, tags and translations")
