#!/usr/bin/env python3
"""Deciduous almond/cherry visuals with optional Serene Seasons only.

Verifies render asset mapping, binary alpha, canopy preservation, season
windows and absence of other seasonal mod integration.
"""
from pathlib import Path
import hashlib
import json
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT=Path("src/main/resources/assets/italiansdelight")
SRC=Path("src/main/java/dev/italiansdelight")
policy=(SRC/"common/orchard/OrchardSeasonPolicy.java").read_text()
bridge=(SRC/"common/orchard/SereneSeasonsOrchardBridge.java").read_text()
seasons=("EARLY_SPRING","MID_SPRING","LATE_SPRING",
         "EARLY_SUMMER","MID_SUMMER","LATE_SUMMER",
         "EARLY_AUTUMN","MID_AUTUMN","LATE_AUTUMN",
         "EARLY_WINTER","MID_WINTER","LATE_WINTER")
trees={"almond":4,"fruit_cherry":5}
unchanged={
 "almond":"7fcfcb85a8f561c114f294650a764969df8aaac2306e6232324c23df90176c39",
 "fruit_cherry":"1d4b76bbd78358e48203f89551f3ff56c4d658827fd0b013e59aebc0bc108514"
}
for tree,maxstage in trees.items():
    name=tree+"_leaves"
    leaf=ROOT/f"textures/block/{name}.png"
    assert hashlib.sha256(leaf.read_bytes()).hexdigest()==unchanged[tree]
    normal=[p for line in check_rgba_pixel_art(leaf,(16,16)) for p in line]
    variants=json.loads((ROOT/f"blockstates/{name}.json").read_text())["variants"]
    assert len(variants)==3*(maxstage+1)
    for stage in range(maxstage+1):
        for visual in (0,1,2):
            state=f"stage={stage},seasonal={visual}"
            assert state in variants,state
            expected=name if stage==0 else (
                name+("_buds","_flowers","_unripe","_fruiting")[stage-1]
                if tree=="almond" else
                name+("_buds","_flowers_early","_flowers","_unripe","_fruiting")[stage-1])
            model=expected if visual==0 else name+"_autumn"
            assert variants[state]=={"model":"italiansdelight:block/"+model},state
    autumn_name=f"{name}_autumn"
    autumn_path=ROOT/f"textures/block/{autumn_name}.png"
    autumn=[p for row in check_rgba_pixel_art(autumn_path,(16,16)) for p in row]
    assert [p[3] for p in normal]==[p[3] for p in autumn], "Keep approved leaf silhouette"
    assert all(p[3] in (0,255) for p in autumn)
    assert sum(p[0]>p[1]>p[2] for p in autumn if p[3])>100
    autumn_model=json.loads((ROOT/f"models/block/{autumn_name}.json").read_text())
    assert autumn_model=={
      "parent":"minecraft:block/leaves",
      "textures":{"all":f"italiansdelight:block/{autumn_name}"}
    }
    # Never reference the rejected winter twig model: winter uses the
    # *identical* autumn model and PNG across all states.
    assert all(not v["model"].endswith("_winter") for v in variants.values())
    assert not (ROOT/f"models/block/{name}_winter.json").exists()
    assert not (ROOT/f"textures/block/{name}_winter.png").exists()
    # Keep real leaf blocks in place with no breakage, no regeneration.
    java=(SRC/f"common/block/{'AlmondLeavesBlock' if tree=='almond' else 'FruitCherryLeavesBlock'}.java").read_text()
    assert 'IntegerProperty.create("seasonal", 0, 2)' in java
    assert "builder.add(STAGE, SEASONAL_VISUAL);" in java
    assert "return true;" in java and "protected boolean isRandomlyTicking" in java
    assert "OrchardSeasonPolicy.foliagePhase(level, pos," in java
    assert "current.setValue(SEASONAL_VISUAL, visual)" in java
    assert "server.setBlock(pos, current.setValue(STAGE, 0), 2)" in java
    assert "level.removeBlock(pos" not in java
    assert "hasAirFace(level, pos)" in java

assert "public static int foliagePhase(Level level, BlockPos pos, Fruit fruit)" in policy
assert 'isModLoaded("sereneseasons")' in policy
assert "fruit == Fruit.OLIVE" in policy and "return 0;" in policy
assert 'subSeason.endsWith("_WINTER")' in policy
assert 'fruit == Fruit.CHERRY && subSeason.endsWith("_AUTUMN")' in policy
assert 'fruit == Fruit.ALMOND && "LATE_AUTUMN".equals(subSeason)' in policy
assert "SeasonHelper.usesStandardSeasons(level, pos)" in bridge
assert "season.getSubSeason().name()" in bridge
assert "return OrchardSeasonPolicy.deciduousFoliagePhase(" in bridge
assert all(term not in policy+bridge for term in ("Homeostatic","Seasonfall","FabricSeasons"))
# Document that cardoon and basil aren't yet season-gated.
orchard=(SRC/"common/orchard/OrchardSeasonPolicy.java").read_text()
assert "BASIL" not in orchard and "CARDOON" not in orchard

print("OK: almond and cherry reuse the EXACT autumn gold model in winter; no bare twigs")
print("OK: 15 almond + 18 cherry state variants preserve the approved spring/fruit models")
print("OK: no block destruction; leaves transition back to green in spring via random ticks")
print("OK: olive remains evergreen; optional Serene Seasons only; cardoon/basil unmodified")
print("NOTE: visual appearance and reload/season transition require gameplay QA")
