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
def png(path, require_opaque=True):
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
    if require_opaque:
        assert all(px[3]==255 for row in rows for px in row),path
    assert len({px[:3] for row in rows for px in row})>=4,path
    return rows
tex=("fruit_cherry_planks","fruit_cherry_log","fruit_cherry_log_top","stripped_fruit_cherry_log","stripped_fruit_cherry_log_top")
pixels={k:png(a/f"textures/block/{k}.png") for k in tex}
# Approved Almond supplies the directional reference, but both Cherry
# sides have a different copper palette and reorganized connected grain.
# Avoid pixel noise: visible grain must have more longitudinal continuity
# than lateral continuity, while preserving nuanced shading.
bark=pixels["fruit_cherry_log"]
stripped=pixels["stripped_fruit_cherry_log"]
def continuity(rows):
    vertical=sum(rows[y][x]==rows[y+1][x] for y in range(15) for x in range(16))
    horizontal=sum(rows[y][x]==rows[y][x+1] for y in range(16) for x in range(15))
    return vertical,horizontal
for name,tex in (("bark",bark),("stripped",stripped)):
    # Natural Cherry bark uses six deliberate wood shades rather than isolated extra dark specks.
    # The separately approved stripped Cherry still uses its eight original tones.
    color_count=len({p[:3] for row in tex for p in row})
    assert 6<=color_count<=10 if name=='bark' else 7<=color_count<=10, (name,color_count)
    vertical,horizontal=continuity(tex)
    assert vertical>=horizontal*1.6, (name,"wood fibers must read longitudinally")
    assert 85<=vertical<=160, (name,"avoid fragmented noise and mechanical stripes")
assert sum(p[0] for row in stripped for p in row)>sum(p[0] for row in bark for p in row), (
    "Stripped fruit cherry must be lighter than its bark"
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
for source_block, target_block in (("FRUIT_CHERRY_LOG","STRIPPED_FRUIT_CHERRY_LOG"),("FRUIT_CHERRY_WOOD","STRIPPED_FRUIT_CHERRY_WOOD")):
    assert f"StrippableBlockRegistry.register({source_block}, {target_block})" in java
for n in blocks:
    assert "fire.add("+n.upper()+", 5, "+("20" if n=="fruit_cherry_planks" else "5")+")" in java
# Controlled clusters can be longer than the previous noise-forcing
# thresholds (13/10 pixels). A few connected timber patches are natural.
# Reject sprawling flat fields, not coherent vertical wood grain.
def largest_flat_area(rows):
    seen=set()
    greatest=0
    for y in range(16):
        for x in range(16):
            if (x,y) in seen: continue
            wanted=rows[y][x]
            todo=[(x,y)];seen.add((x,y));size=0
            while todo:
                xx,yy=todo.pop();size+=1
                for nx,ny in ((xx+1,yy),(xx-1,yy),(xx,yy+1),(xx,yy-1)):
                    if 0<=nx<16 and 0<=ny<16 and (nx,ny) not in seen and rows[ny][nx]==wanted:
                        seen.add((nx,ny));todo.append((nx,ny))
            greatest=max(greatest,size)
    return greatest
assert largest_flat_area(bark)<=24  # coherent connected bark plates, not artificially noisy
assert largest_flat_area(stripped)<=12
# The user found objectionable vertical line artifacts when this 16x16
# side was repeated over a 2x2 wall. Inspect the *repeated* texture, not
# merely the single-block pattern. Keep dark lines short and prevent a
# shadow from connecting itself between the bottom and top of each tile.
def repeated_vertical_streaks(rows, quantile=.24):
    brightness=sorted(sum(p[:3])/3 for row in rows for p in row)
    threshold=brightness[int(quantile*(len(brightness)-1))]
    mask=[[sum(rows[y][x][:3])/3 <= threshold for x in range(16)]
          for y in range(16)]
    max_streak=0
    for x in range(16):
        length=0
        for y in range(32):
            length=length+1 if mask[y%16][x] else 0
            max_streak=max(max_streak,length)
    joined=sum(mask[0][x] and mask[15][x] for x in range(16))
    return max_streak, joined
for name,tex in (("bark",bark),("stripped",stripped)):
    longest,joined=repeated_vertical_streaks(tex)
    assert longest<=7, (name,"continuous streak visible in 2x2 tiling",longest)
    assert joined==0, (name,"dark crevice joins at 16px tile boundary")
# Surface texture cleanup: avoid detached high-contrast bark specks
# without breaking the short grooves already tested above. The stripped
# texture has been expressly approved and is protected byte-for-byte.
from statistics import median
import hashlib
palette=sorted({px for row in bark for px in row},key=lambda p:sum(p[:3]))
ordinal={px:i for i,px in enumerate(palette)}
def isolated_contrast_pixels(texture):
    values=[[ordinal[texture[y][x]] for x in range(16)] for y in range(16)]
    total=0
    for y in range(16):
        for x in range(16):
            value=values[y][x]
            neighbours=[values[ny][nx]
                for dx,dy in ((0,-1),(0,1),(-1,0),(1,0),(-1,-1),(1,-1),(-1,1),(1,1))
                if 0<=(nx:=x+dx)<16 and 0<=(ny:=y+dy)<16]
            if len(neighbours)>=3 and sum(abs(v-value)<=1 for v in neighbours)<=1 and abs(value-round(median(neighbours)))>=2:
                total+=1
    return total
assert isolated_contrast_pixels(bark)<=5,"Bark has random detached pixels"
approved_stripped=(a/"textures/block/stripped_fruit_cherry_log.png").read_bytes()
assert hashlib.sha256(approved_stripped).hexdigest()=="27fca296d9ee001f05d5b24c56e0116b2ff8b9ccf0406c34465d0104c3c99546","Do not modify approved stripped Cherry wood"
# User-requested seamless stacked bark and milder horizontal plank joints:
# measure the exact 16px texture boundary that repeats under each log.
def average_luma_diff(left,right):
    return sum(abs(sum(a[:3])/3-sum(b[:3])/3) for a,b in zip(left,right))/len(left)
assert average_luma_diff(bark[0],bark[15]) < 14, "Stacked Cherry bark has a visible horizontal seam"
# This entire border was expressly approved by the author and MUST NOT move.
outer=bytes(channel for y in range(16) for x in range(16)
            if x in (0,15) or y in (0,15)
            for channel in bark[y][x])
assert hashlib.sha256(outer).hexdigest()=="063376484236f42e9ed88c11f678f674b97e7bb80c62cc436068fb5e0f58057a", (
    "User-approved cherry log 1px perimeter must not change"
)
planks=pixels["fruit_cherry_planks"]
def average_luma(row):
    return sum(sum(p[:3])/3 for p in row)/len(row)
for y in (3,7,11,15):
    assert 14 < average_luma(planks[y-1])-average_luma(planks[y]) < 24, (
        "Cherry plank seam should remain visible without an excessive dark line",y)
# The five-shade wood grain geometry is approved: only its palette may soften.
palette=sorted({px[:3] for row in planks for px in row},key=lambda rgb:sum(rgb))
rank={rgb:index for index,rgb in enumerate(palette)}
grain=bytes(rank[px[:3]] for row in planks for px in row)
assert hashlib.sha256(grain).hexdigest()=="77244b3d13323cf30f8298ec682e3ab01ebfd21a8876bd9878dc377b2a44bcc5", (
    "Cherry plank pixel pattern must remain unchanged"
)
def tone(px): return sum(px[:3])/3
max_jump=max(abs(tone(planks[y][x])-tone(planks[ny][nx]))
             for y in range(16) for x in range(16)
             for nx,ny in ((x+1,y),(x,y+1)) if nx<16 and ny<16)
assert max_jump <= 40, ("Cherry planks have excessive adjacent-pixel contrast",max_jump)
print("OK: immutable 1px cherry bark perimeter, coherent internal grain and visible moderate plank joints")
print("OK: cleaned disconnected bark specks; approved stripped texture unchanged")
print("OK: tested both repeated 2x2 log textures; no extended continuous dark lines")
print("OK: long directional Cherry grain, short coherent lenticels, limited flat fields")
print("OK: five original fruit cherry blocks, 5 PNGs, trunk rings, vanilla models")
print("OK: creative, axe stripping, flammability, crafting, tags and translations")
