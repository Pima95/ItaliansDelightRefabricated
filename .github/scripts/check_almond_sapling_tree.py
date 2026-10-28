#!/usr/bin/env python3
"""Almond orchard stage 1: original leaves and plantable 1x1 tree (not wild)."""
from pathlib import Path
import json
import hashlib
from check_orchard_signs_and_cutting import check_rgba_pixel_art

R=Path("src/main/resources")
A=R/"assets/italiansdelight"
D=R/"data"
SRC=Path("src/main/java/dev/italiansdelight")
def j(path):
    assert path.exists(), path
    return json.loads(path.read_text(encoding="utf-8"))
def png(name):
    path=A/"textures/block"/f"{name}.png"
    data=path.read_bytes()
    assert data[:8]==b"\x89PNG\r\n\x1a\n",name
    import struct
    assert struct.unpack(">II",data[16:24])==(16,16),name
    pix=[p for row in check_rgba_pixel_art(path,(16,16)) for p in row]
    assert len(pix)==256 and all(p[3] in (0,255) for p in pix)
    return pix

def main():
    leaf=png("almond_leaves")
    sapling=png("almond_sapling")
    # Keep the approved six-color sapling untouched. Only leaf blocks
    # were desaturated a further 13% from the prior approved foliage,
    # keeping their 197 opaque positions and 59 fully transparent ones.
    sapling_colors={(40,75,40),(58,99,49),(78,121,57),
                    (103,148,71),(136,174,95),(166,192,119)}
    leaf_colors={(49,72,49),(68,95,62),(88,117,73),
                 (113,143,91),(144,170,117),(171,188,139)}
    leaf_visible=[p for p in leaf if p[3]==255]
    assert len(leaf_visible)==197 and {p[:3] for p in leaf_visible}==leaf_colors
    # Vanilla leaves are dense in the center, but their outer edges remain
    # partially cut out: not the see-through lace of the previous drawing.
    # Preserve six foliage tones, strict binary alpha and the vanilla cutout.
    assert sum(p[3]==0 for p in leaf)==59
    border=[leaf[y*16+x] for y in range(16) for x in range(16)
            if x in (0,15) or y in (0,15)]
    interior=[leaf[y*16+x] for y in range(1,15) for x in range(1,15)]
    assert len(border)==60
    assert 30 <= sum(p[3]==255 for p in border) <= 45, (
        "Vanilla-style leaf outlines should be semi-filled, not a solid border"
    )
    assert sum(p[3]==255 for p in interior)>=155, (
        "The center must be substantially fuller than the outer edges"
    )
    assert all(p[3] in (0,255) for p in leaf)
    assert 75<=sum(p[3]==255 for p in sapling)<=100
    stem={(87,58,48),(120,79,58),(156,110,78)}
    assert {p[:3] for p in sapling if p[3]==255}<=sapling_colors|stem
    assert sum(p[:3] in stem for p in sapling if p[3])>=30
    # The bent lower sapling MUST have a visually solid, shaded 3px base.
    for y in range(11,16):
        assert sum(sapling[y*16+x][:3] in stem and sapling[y*16+x][3]
                   for x in range(16))>=3, ("too thin sapling trunk",y)
    # Real saplings have a crooked S-shaped stem and secondary branches,
    # not a straight trunk with a symmetric pixel cloud above it.
    for x,y in ((8,15),(7,13),(6,11),(9,6)):
        assert sapling[y*16+x][:3] in stem, ("missing crooked wooden stem",x,y)
    assert len({x for y in range(6,16) for x in range(16)
                if sapling[y*16+x][:3] in stem})>=5
    assert {p[:3] for p in sapling if p[3]>0}==sapling_colors|stem
    assert hashlib.sha256((A/"textures/block/almond_leaves.png").read_bytes()).hexdigest()==(
        "7fcfcb85a8f561c114f294650a764969df8aaac2306e6232324c23df90176c39")
    assert hashlib.sha256((A/"textures/block/almond_sapling.png").read_bytes()).hexdigest()==(
        "f8c52ac0088cacf0584afaad9240bfcc96fa96e328e61791d31bc706d07a14a7")

    for name in ("almond_sapling","almond_leaves"):
        block=j(A/f"blockstates/{name}.json")
        assert block["variants"][""]["model"]==f"italiansdelight:block/{name}"
        assert j(A/f"items/{name}.json")["model"]["model"]==f"italiansdelight:item/{name}"
        assert j(D/f"italiansdelight/loot_table/blocks/{name}.json")["type"]=="minecraft:block"
        for kind in ("block","item"):
            cat="saplings" if name.endswith("sapling") else "leaves"
            assert "italiansdelight:"+name in j(D/f"minecraft/tags/{kind}/{cat}.json")["values"]
        for lang in ("it_it","en_us"):
            assert "block.italiansdelight."+name in j(A/f"lang/{lang}.json")
    assert j(A/"models/block/almond_leaves.json")=={
        "parent":"minecraft:block/leaves",
        "textures":{"all":"italiansdelight:block/almond_leaves"}}
    assert j(A/"models/block/almond_sapling.json")=={
        "parent":"minecraft:block/cross",
        "textures":{"cross":"italiansdelight:block/almond_sapling"}}
    assert j(A/"models/item/almond_sapling.json")["textures"]["layer0"]==(
        "italiansdelight:block/almond_sapling")
    assert j(A/"models/item/almond_leaves.json")["parent"]==(
        "italiansdelight:block/almond_leaves")
    assert j(A/"models/block/potted_almond_sapling.json")["parent"]==(
        "minecraft:block/flower_pot_cross")
    assert j(A/"blockstates/potted_almond_sapling.json")["variants"][""]["model"]==(
        "italiansdelight:block/potted_almond_sapling")
    assert "italiansdelight:potted_almond_sapling" in j(
        D/"minecraft/tags/block/flower_pots.json")["values"]
    loot=j(D/"italiansdelight/loot_table/blocks/almond_leaves.json")
    names=[e.get("name") for pool in loot["pools"] for e in pool["entries"]]
    assert "italiansdelight:almond_leaves" in names
    assert "italiansdelight:almond_sapling" in names
    assert "minecraft:stick" in names
    assert "italiansdelight:almonds" not in names
    assert j(D/"italiansdelight/loot_table/blocks/almond_sapling.json")["pools"][0]["entries"][0]["name"]==(
        "italiansdelight:almond_sapling")
    for lang in ("it_it","en_us"):
        assert "block.italiansdelight.potted_almond_sapling" in j(A/f"lang/{lang}.json")

    tree=j(D/"italiansdelight/worldgen/configured_feature/almond_tree.json")
    assert tree["type"]=="minecraft:tree"
    cfg=tree["config"]
    assert cfg["trunk_provider"]["state"]["Name"]=="italiansdelight:almond_log"
    assert cfg["foliage_provider"]["state"]["Name"]=="italiansdelight:almond_leaves"
    assert cfg["foliage_provider"]["state"]["Properties"]["persistent"]=="false"
    assert cfg["trunk_placer"]=={
        "type":"italiansdelight:almond_trunk_placer",
        "base_height":6, "height_rand_a":0, "height_rand_b":0}
    assert cfg["foliage_placer"]["type"]=="italiansdelight:almond_foliage_placer"
    assert cfg["foliage_placer"]["radius"]==2
    blocks=(SRC/"common/registry/ModBlocks.java").read_text()
    placers=(SRC/"common/worldgen/ModTreePlacers.java").read_text()
    assert "new SaplingBlock(ALMOND_TREE_GROWER" in blocks
    assert "Optional.of(ALMOND_TREE_FEATURE)" in blocks
    assert "new UntintedParticleLeavesBlock(0.0F, ParticleTypes.CHERRY_LEAVES" in blocks
    assert "fire.add(ALMOND_LEAVES, 30, 60);" in blocks
    for name in ("ALMOND_LEAVES","ALMOND_SAPLING"):
        assert (SRC/"common/item/group/ModItemGroups.java").read_text().count(
            f"output.accept(ModBlocks.{name});")==1
    for kind in ("almond_trunk_placer","almond_foliage_placer"):
        assert f'ModRegistries.id("{kind}")' in placers
    # The ALMOND trunk reuses Olive's proven lower bend + rising double fork.
    # The crown MUST NOT reuse the olive flattened dome: its vertical tuft
    # structure extends from dy=-1 through +5, with 3 upward shoots.
    trunk=(SRC/"common/worldgen/AlmondTrunkPlacer.java").read_text()
    foliage=(SRC/"common/worldgen/AlmondFoliagePlacer.java").read_text()
    assert "BlockPos lowSide = forkBase.relative(lean)" in trunk
    assert "BlockPos outerFork = raisedFork.relative(secondary)" in trunk
    assert "canPlacePlan(level, origin, plan.barkPositions())" in trunk
    assert "FORK_VARIANT_CHANCE = 5" in trunk
    assert "TALL_SINGLE_CHANCE = 4" in trunk
    assert "new double" not in foliage
    assert "anchor.above(5)" not in foliage
    assert "for (int dy = 2; dy <= 3; dy++)" in foliage
    assert "for (int dy = 1; dy <= 4; dy++)" not in foliage
    assert "for (int arm = 0; arm < 3; arm++)" in foliage
    assert "for (int dy = 2; dy <= 3; dy++)" in foliage
    assert "distanceFromWood" in foliage and "nextDistance > 6" in foliage
    # Match Olive's low starting level on every short/tall trunk while
    # keeping the prior maximum canopy level (+4) exactly unchanged.
    olive=(SRC/"common/worldgen/OliveFoliagePlacer.java").read_text()
    low_rule="int minDy = trunkHeightAboveGround >= 5 ? -2 : -1;"
    assert low_rule in olive and low_rule in foliage
    assert "for (int dy = minDy; dy <= 3; dy++)" in foliage
    assert "for (int dy = minDy; dy <= 4; dy++)" not in foliage
    assert "return 6;" in foliage
    assert "double[] radii = {1.95D, 1.70D, 2.35D, 2.45D, 2.00D, 1.45D};" in foliage
    assert "radii[dy + 2] * scale" in foliage
    assert "anchor.getY() + dy - groundY < 3" in foliage
    assert "dy <= 4" not in foliage
    assert "dy <= 5" not in foliage
    assert "anchor.above(5)" not in foliage
    assert "OLIVE_LEAVES" not in foliage and "OLIVE_TRUNK" not in trunk
    # Regression: cover upper bends even when the final trunk log has
    # shifted laterally. Each new leaf borders actual Almond wood, never
    # an assumed straight pole, and the collar stays below the old apex.
    assert "for (int dy = -3; dy <= -1; dy++)" in foliage
    assert "BlockPos wood = anchor.offset(dx, dy, dz)" in foliage
    assert "isAlmondWood(level.getBlockState(wood))" in foliage
    assert "for (Direction side : Direction.Plane.HORIZONTAL)" in foliage
    assert "BlockPos leaf = wood.relative(side)" in foliage
    assert "planned.add(leaf); // Face-adjacent to REAL logs." in foliage
    assert "y - groundY < 3" in foliage
    assert "for (int dy = minDy; dy <= 3; dy++)" in foliage
    assert "anchor.above(5)" not in foliage

    # The block stays a standard vanilla-tinted leaf model, but the
    # biome adapter is CLIENT ONLY and touches strictly Almond leaves.
    client=(SRC/"ItaliansDelightClient.java").read_text()
    assert "BlockColorRegistry.register(List.of(ALMOND_BIOME_FOLIAGE), ModBlocks.ALMOND_LEAVES);" in client
    assert "BlockTintSources.foliage().colorInWorld(state, level, pos)" in client
    assert "private static int softenAlmondTint(int biome)" in client
    assert "255 * 7" in client and "* 3" in client
    assert "ModBlocks.OLIVE_LEAVES" not in client
    assert "ModBlocks.ALMOND_SAPLING" not in client
    assert "ColorProviderRegistry" not in (SRC/"common/registry/ModBlocks.java").read_text()

    for class_name in ("AlmondTrunkPlacer","AlmondFoliagePlacer"):
        assert (SRC/f"common/worldgen/{class_name}.java").exists()
    assert "ALMOND_TREE" not in (SRC/"common/registry/ModWorldGeneration.java").read_text()
    assert not (D/"italiansdelight/worldgen/placed_feature/almond_tree_wild.json").exists()
    print("OK: Almond vanilla-density foliage: 197 opaque, 59 transparent, 37/60 opaque border, additional 13% reduced foliage saturation, preserved six-tone palette")
    print("OK: approved Almond sapling unchanged; crown begins at Olive height (-2 tall/-1 short), apex now +3")
    print("OK: Almond-only soft vanilla biome tint registered on the client; Olive untouched")
    print("OK: real upper-wood collars fill shifted elbows; crown apex lowered exactly one block (+3)")
    print("OK: No fruit drops or wild Almond worldgen until later phases")

if __name__=="__main__":
    main()
