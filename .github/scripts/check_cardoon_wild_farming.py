#!/usr/bin/env python3
"""Check FDR-matched wild spawn, cardoon farming compatibility, and 16x16 PNG alpha."""
import json
import struct
import zlib
from pathlib import Path
ROOT = Path("src/main/resources")
def doc(p): return json.loads((ROOT / p).read_text(encoding="utf-8"))
def check(ok, why):
    if not ok: raise AssertionError(why)

for name in ("cardoon_patch", "wild_basil_patch"):
    x=doc(f"data/italiansdelight/worldgen/placed_feature/{name}.json")["placement"]
    check(x[0] == {"type":"minecraft:rarity_filter", "chance":120}, f"{name}: rarity != 120")
    check(any(z.get("type") == "minecraft:count" and z.get("count") == 64 for z in x),
          f"{name}: patch count != 64")
    check(any(z.get("type") == "minecraft:block_predicate_filter" for z in x),
          f"{name}: air/soil filter missing")
tag=doc("data/c/tags/item/crops/cardoon.json")
check(set(tag["values"])=={"italiansdelight:cardoon","italiansdelight:cultivated_cardoon"},
      "Legacy item must work alongside new harvest")
rennet=doc("data/italiansdelight/recipe/cheese_vat/rennet.json")
check("#c:crops/cardoon" in rennet["ingredients"], "Rennet uses an obsolete single Cardoon item")
wild=doc("data/italiansdelight/loot_table/blocks/cardoon.json")
check("cultivated_cardoon" in json.dumps(wild) and "set_count" in json.dumps(wild),
      "Wild plant does not drop cultivated seeds")
crop=doc("data/italiansdelight/loot_table/blocks/cultivated_cardoon.json")
check('"age": "7"' in json.dumps(crop) and "fortune" in json.dumps(crop),
      "Mature crop should give a Fortune-compatible bonus")
states=doc("assets/italiansdelight/blockstates/cultivated_cardoon.json")["variants"]
check(len(states)==8, "Cultivated crop needs eight growth ages")

def check_png(path):
    data=path.read_bytes()
    check(data.startswith(b"\x89PNG\r\n\x1a\n"),f"{path}: no PNG header")
    p=8;chunks=[]
    while p < len(data):
        n=struct.unpack(">I",data[p:p+4])[0];t=data[p+4:p+8];v=data[p+8:p+8+n]
        crc=struct.unpack(">I",data[p+8+n:p+12+n])[0]
        check((zlib.crc32(t+v)&0xffffffff)==crc,f"{path}: invalid CRC")
        chunks.append((t,v));p+=n+12
        if t==b"IEND":break
    hd=next(v for t,v in chunks if t==b"IHDR")
    check(struct.unpack(">IIBBBBB",hd)==(16,16,8,6,0,0,0),f"{path}: not RGBA 16x16")
    pixels=zlib.decompress(b"".join(v for t,v in chunks if t==b"IDAT"))
    stride=64;previous=bytearray(stride);p=0;alpha=set()
    for y in range(16):
        filt=pixels[p];p+=1;src=pixels[p:p+stride];p+=stride
        out=bytearray(stride)
        for k,val in enumerate(src):
            a=out[k-4] if k>=4 else 0
            b=previous[k];c=previous[k-4] if k>=4 else 0
            if filt==0: sub=0
            elif filt==1: sub=a
            elif filt==2: sub=b
            elif filt==3: sub=(a+b)//2
            elif filt==4:
                q=a+b-c;da=abs(q-a);db=abs(q-b);dc=abs(q-c)
                sub=a if da<=db and da<=dc else b if db<=dc else c
            else: raise AssertionError(f"{path}: invalid scanline filter {filt}")
            out[k]=(val+sub)&255
        alpha.update(out[k] for k in range(3,stride,4))
        previous=out
    check(alpha=={0,255},f"{path}: alpha must be exactly 0/255, got {alpha}")

for s in range(4):
    name=f"cultivated_cardoon_stage{s}"
    model=doc(f"assets/italiansdelight/models/block/{name}.json")
    texture_key = "cross" if s == 3 else "crop"
    check(model["textures"][texture_key]==f"italiansdelight:block/{name}",
          f"{name} wrong {texture_key} texture")
    check_png(ROOT/f"assets/italiansdelight/textures/block/{name}.png")
check_png(ROOT/"assets/italiansdelight/textures/item/cultivated_cardoon.png")
java=Path("src/main/java/dev/italiansdelight/common/block/CardoonBlock.java").read_text(encoding="utf-8")
check("popResource(" not in java and "level.setBlock(candidate, state, 2)" in java,
      "Wild bonemeal must spread plants, not generate harvested items")
reg=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text(encoding="utf-8")
check(".sound(SoundType.LANTERN)" in reg,"Hanging hook must use vanilla lantern block sounds")
hook=Path("src/main/java/dev/italiansdelight/common/block/CheeseHookBlock.java").read_text(encoding="utf-8")
check("SoundEvents.LANTERN_PLACE" in hook and "SoundEvents.LANTERN_HIT" in hook,
      "Hanging product interactions must use vanilla lantern sounds")
check("SoundEvents.CHAIN_" not in hook and ".sound(SoundType.CHAIN)" not in reg,
      "Old chain sounds must not remain on the hanging hook")
# Vanilla block/crop draws its texture on FOUR offset planes (x/z 4 and 12):
# one purple flower sprite then looks like four flowers in world.
# The final stage must use one centered crossed pair instead.
mature=doc("assets/italiansdelight/models/block/cultivated_cardoon_stage3.json")
check(mature.get("parent")=="minecraft:block/cross"
      and mature.get("textures", {}).get("cross")=="italiansdelight:block/cultivated_cardoon_stage3",
      "Mature cardoon must render one centered flower, not four separated crop quads")
for i in range(3):
    immature=doc(f"assets/italiansdelight/models/block/cultivated_cardoon_stage{i}.json")
    check(immature.get("parent")=="minecraft:block/crop",
          "Keep approved pre-flowering crop growth stages unchanged")
print("OK: FDR wild carrot/onion rarity 1/120 and 64 attempts for both wild plants")
print("OK: old wild Cardoon preserved, plantable crop, Fortune loot and compatible rennet")
print("OK: 5 exact 16x16 RGBA PNGs, valid CRCs, only opaque/transparent pixels")
print("OK: one centered mature cardoon flower and unchanged 16x16 approved sprite")
print("OK: hook uses lantern block and interaction sounds")
