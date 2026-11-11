#!/usr/bin/env python3
"""Orchard 1.1.0: enforce non-Rustic olive leaves, vanilla data and drop mechanics."""
from pathlib import Path
import json
import struct
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT=Path("src/main/resources")
A=ROOT/"assets/italiansdelight"
D=ROOT/"data"
BLOCK="italiansdelight:olive_leaves"

def read(path):
    if not path.is_file():raise ValueError("Missing "+str(path))
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    src=(A/"textures/block/olive_leaves.png")
    data=src.read_bytes()
    if data[:8]!=b"\x89PNG\r\n\x1a\n" or struct.unpack(">II",data[16:24])!=(16,16):
        raise ValueError("Approved olive leaves must be a 16x16 PNG")
    old=A/"textures/block/leaves_olive.png"
    if old.exists():
        raise ValueError("Rustic leaves_olive.png must be removed from the Orchard branch")
    rustic_sapling=A/"textures/block/sapling_olive.png"
    if rustic_sapling.exists():
        raise ValueError("Rustic sapling_olive.png must be removed from the Orchard branch")
    approved_sapling=A/"textures/block/olive_sapling.png"
    assert approved_sapling.is_file(), "Preserve existing olive_sapling.png for a future growable sapling"
    sapling_bytes=approved_sapling.read_bytes()
    assert sapling_bytes[:8]==b"\x89PNG\r\n\x1a\n" and struct.unpack(">II",sapling_bytes[16:24])==(16,16)

    # Palette-only retouch: preserve the EXACT old leaf silhouette, 168
    # opaque pixels and 88 transparent pixels. PNGs remain crisp RGBA 16x16,
    # alpha either 0 or 255, never blended/antialiased.
    pixels=[pixel for row in check_rgba_pixel_art(src,(16,16)) for pixel in row]
    assert sum(px[3]==0 for px in pixels)==88, "Leaf silhouette must not change"
    visible=[px for px in pixels if px[3]==255]
    assert len(visible)==168
    silver_palette={
        (68,91,78), (104,127,110), (146,163,143), (198,209,194)
    }
    assert {px[:3] for px in visible}==silver_palette, (
        "Olive leaves must use the tested four olive-silver colors"
    )
    assert sum(px[0]>=190 for px in visible)>=20, (
        "Silvery highlights must remain visible over the crown"
    )
    for folder in ("models","blockstates","items"):
        for path in (A/folder).rglob("*.json"):
            if "sapling_olive" in path.read_text(encoding="utf-8"):
                raise ValueError("A Minecraft resource still references the deleted Rustic sapling: "+str(path))
    model=read(A/"models/block/olive_leaves.json")
    if model != {"parent":"minecraft:block/leaves",
                  "textures":{"all":"italiansdelight:block/olive_leaves"}}:
        raise ValueError("Olive leaves must use vanilla leaves geometry and approved art")
    # A single existing block ID supports all five visual growth stages.
    state=read(A/"blockstates/olive_leaves.json")
    expected_stages={
        0:"olive_leaves", 1:"olive_leaves_buds", 2:"olive_leaves_flowers",
        3:"olive_leaves_unripe", 4:"olive_leaves_fruiting"
    }
    assert state=={"variants":{
        f"stage={age}":{"model":f"italiansdelight:block/{name}"}
        for age,name in expected_stages.items()
    }}
    stage_pixel_changes={}
    for age,name in expected_stages.items():
        if age==0:continue
        assert read(A/f"models/block/{name}.json")=={
            "parent":"minecraft:block/leaves",
            "textures":{"all":f"italiansdelight:block/{name}"}
        }
        variant=[p for row in check_rgba_pixel_art(
            A/f"textures/block/{name}.png",(16,16)) for p in row]
        # Preserve the exact 168px silhouette, 88 transparent pixels, alpha
        # 0/255, and every original transparent pixel (no glow/AA).
        assert [p[3] for p in variant]==[p[3] for p in pixels],name
        assert all(p[3] in (0,255) for p in variant),name
        changed=[q[:3] for p,q in zip(pixels,variant) if p!=q]
        stage_pixel_changes[age]=len(changed)
        if age==1:
            assert 8<=len(changed)<=26, "Visible olive buds are missing"
            assert sum(r>225 and g>225 and b>205 for r,g,b in changed)>=7
        if age==2:
            assert 42<=len(changed)<=85, "Olive blossoms must cover the canopy"
            assert sum(r>=235 and g>=235 and b>=215 for r,g,b in changed)>=35, (
                "Olive flowers must be clearly white/ivory")
        if age==3:
            assert 26<=len(changed)<=65, "Olive drupes must be visible before maturity"
        if age==4:
            assert 50<=len(changed)<=90, "Ripe olives need many olive-green pixels"
            assert sum(g>r*1.20 and g>b*1.22 for r,g,b in changed)>=30
    assert stage_pixel_changes[4]>stage_pixel_changes[3]
    item=read(A/"items/olive_leaves.json")
    assert item["model"]["model"]=="italiansdelight:item/olive_leaves"
    assert read(A/"models/item/olive_leaves.json")["parent"]=="italiansdelight:block/olive_leaves"
    for type in ("block","item"):
        tag=read(D/"minecraft/tags"/type/"leaves.json")
        assert BLOCK in tag["values"]
    # No dedicated mineable:hoe needed: vanilla #minecraft:mineable/hoe
    # references #minecraft:leaves, which is extended above.
    logs=read(D/"minecraft/tags/block/logs_that_burn.json")
    assert all("italiansdelight:"+name in logs["values"]
               for name in ("olive_log","olive_wood","stripped_olive_log","stripped_olive_wood"))
    srcjava=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text()
    assert "new OliveLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES)" in srcjava
    assert "OLIVE_LEAVES" in srcjava
    assert "fire.add(OLIVE_LEAVES, 30, 60)" in srcjava
    harvest=Path("src/main/java/dev/italiansdelight/common/block/OliveLeavesBlock.java").read_text()
    assert "extends UntintedParticleLeavesBlock" in harvest
    assert 'IntegerProperty.create("stage", 0, 4)' in harvest
    assert "RIPE_STAGE = 4" in harvest
    assert "ADVANCE_CHANCE = 5" in harvest
    assert "super.randomTick(state, level, pos, random)" in harvest
    assert "super.isRandomlyTicking(state)" in harvest
    assert "hasAirFace(level, pos)" in harvest
    assert "for (Direction face : Direction.values())" in harvest
    assert "level.getBlockState(pos.relative(face))" in harvest
    assert "neighbor.isAir()" in harvest
    assert "hasAirFace(level, pos, direction, neighborState)" in harvest, (
        "A newly blocked face must immediately reset a no-longer-exposed leaf")
    assert "return updated.setValue(STAGE, 0)" in harvest
    assert "current.setValue(STAGE, stage + 1)" in harvest
    assert "server.setBlock(pos, current.setValue(STAGE, 0), 2)" in harvest
    assert "new ItemStack(ModItems.OLIVES, 1 + server.getRandom().nextInt(3))" in harvest
    assert "!state.getValue(PERSISTENT)" in harvest
    assert "state.getValue(DISTANCE) < 7" in harvest
    assert "!state.getValue(WATERLOGGED)" in harvest
    assert "level instanceof ServerLevel" in harvest
    assert "|| !hasAirFace(server, pos)" in harvest, "Harvest needs six-face air check"
    group=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text()
    assert group.count("output.accept(ModBlocks.OLIVE_LEAVES);")==1
    loot=read(D/"italiansdelight/loot_table/blocks/olive_leaves.json")
    drop=loot["pools"][0]["entries"][0]
    assert drop["name"]==BLOCK
    tool=drop["conditions"][0]
    assert tool["condition"]=="minecraft:any_of"
    assert any(t.get("predicate",{}).get("items")=="minecraft:shears" for t in tool["terms"])
    assert any(t.get("predicate",{}).get("predicates",{}).get("minecraft:enchantments")
               for t in tool["terms"])
    assert any(e.get("name")=="minecraft:stick" for pool in loot["pools"][1:] for e in pool["entries"])
    sapling_drop = [e for pool in loot["pools"] for e in pool["entries"]
                    if e.get("name")=="italiansdelight:olive_sapling"]
    assert len(sapling_drop)==1, "Leaf decay must now yield viable olive seedlings"
    assert sapling_drop[0]["conditions"][-1]["chances"]==[0.05,0.0625,0.083333336,0.1]
    assert not any(e.get("name","") in ("minecraft:apple","italiansdelight:olives")
                   for pool in loot["pools"] for e in pool["entries"])
    print("OK: five-stage olive cycle (leaf, pale buds, white flowers, unripe, harvestable green olives)")
    print("OK: any of six adjacent air faces required for ripening AND harvesting")
    print("OK: newly occluded foliage reverts to stage 0 while retaining vanilla leaf decay")
    print("OK: all 16x16 stage sprites have binary alpha and preserve original 168px silhouette")
    print("OK: base silver-green art, sapling, leaf-break loot, Silk Touch and shears unchanged")
    print("NOTE: manual in-game maturity, airflow, double-harvest, decay and client/server tests pending")

if __name__=="__main__":
    main()
