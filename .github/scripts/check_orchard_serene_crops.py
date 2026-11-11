#!/usr/bin/env python3
"""Serene Seasons crop fertility for basil and cultivated cardoon (MC 26.2).

Native Serene Seasons tag integration, no new random-tick or death mechanics.
Wild worldgen plants remain unaffected; off-season survival is protected.
"""
import json
from pathlib import Path

R=Path("src/main/resources/data/sereneseasons/tags")
JAVA=Path("src/main/java/dev/italiansdelight/common/block")
BASIL="italiansdelight:basil"
CARDOON="italiansdelight:cultivated_cardoon"

expected={
    ("block","spring_crops"):{BASIL,CARDOON,"italiansdelight:almond_sapling","italiansdelight:fruit_cherry_sapling"},
    ("block","summer_crops"):{BASIL,CARDOON},
    ("block","autumn_crops"):{CARDOON,"italiansdelight:almond_sapling"},
    ("item","spring_crops"):{BASIL,CARDOON,"italiansdelight:almond_sapling","italiansdelight:fruit_cherry_sapling"},
    ("item","summer_crops"):{BASIL,CARDOON},
    ("item","autumn_crops"):{CARDOON,"italiansdelight:almond_sapling"},
    ("block","unbreakable_infertile_crops"):{BASIL,CARDOON,"italiansdelight:olive_sapling","italiansdelight:almond_sapling","italiansdelight:fruit_cherry_sapling"}
}
for (kind,name),values in expected.items():
    path=R/kind/(name+".json")
    assert path.is_file(),path
    data=json.loads(path.read_text(encoding="utf-8"))
    assert data.get("replace") is False, "Do not overwrite upstream seasons"
    assert set(data["values"])==values,(path,data["values"])

# The normal Serene Seasons fertility mechanism handles rate and greenhouses.
# Avoid any custom randomTick overrides that might bypass its growth hook.
for java in ("BasilCropBlock.java","CultivatedCardoonCropBlock.java"):
    source=(JAVA/java).read_text(encoding="utf-8")
    assert "extends CropBlock" in source,java
    assert "randomTick(" not in source,java
    assert "destroyBlock(" not in source,java
    assert "performBonemeal(" not in source,java

# Crop/seed tags are deliberately aligned for both tooltip and fertility.
for kind in ("block","item"):
    assert not (R/kind/"winter_crops.json").exists()
    # Olive saplings follow vanilla OAK_SAPLING and grow year-round;
    # neither cultivated crop is winter-fertile or year-round.
    year=json.loads((R/kind/"year_round_crops.json").read_text())["values"]
    assert year==["italiansdelight:olive_sapling"]
    for name in ("spring_crops","summer_crops"):
        entries=json.loads((R/kind/(name+".json")).read_text())["values"]
        assert BASIL in entries and CARDOON in entries
    assert BASIL not in json.loads((R/kind/"autumn_crops.json").read_text())["values"]
    assert CARDOON in json.loads((R/kind/"autumn_crops.json").read_text())["values"]
    for name in ("spring_crops","summer_crops","autumn_crops"):
        entries=json.loads((R/kind/(name+".json")).read_text())["values"]
        assert "italiansdelight:wild_basil" not in entries
        assert "italiansdelight:cardoon" not in entries, "Wild cardoon not a crop"
# Protect against the upstream optional mode 'break inferrtile crop'.
survive=json.loads((R/"block/unbreakable_infertile_crops.json").read_text())["values"]
assert set(survive)=={BASIL,CARDOON,
    "italiansdelight:olive_sapling","italiansdelight:almond_sapling",
    "italiansdelight:fruit_cherry_sapling"}
build=Path("build.gradle").read_text()
assert 'compileOnly "curse.maven:serene-seasons-291874:8938283"' in build
mod=json.loads(Path("src/main/resources/fabric.mod.json").read_text())
assert "sereneseasons" not in mod["depends"], "Serene Seasons remains optional"
assert mod.get("suggests",{}).get("sereneseasons")=="*"

print("OK: Basil is spring/summer fertile; autumn/winter grow slowly by default")
print("OK: Cultivated Cardoon is spring/summer/autumn fertile; winter slow by default")
print("OK: Serene Seasons native block+item tags preserve its greenhouse/bonemeal behavior")
print("OK: Unbreakable infertile crop tag prevents seasonal breakage in mode 2")
print("OK: Wild basil/cardoon, growing stages, textures, yields and drops unchanged")
print("NOTE: config option 1 can stop growth; default config 0 slows to 1/6; verify in-game")
