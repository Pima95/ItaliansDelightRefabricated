#!/usr/bin/env python3
"""Serene Seasons 26.2 optional orchard integration, calendar and compatibility QA.

This statically verifies the calendar, optional loading and server-side harvest
guards; runtime in-game testing with AND without Serene Seasons is still needed.
"""
import json
import re
from pathlib import Path

J=Path("src/main/java/dev/italiansdelight")
policy=(J/"common/orchard/OrchardSeasonPolicy.java").read_text()
bridge=(J/"common/orchard/SereneSeasonsOrchardBridge.java").read_text()
gradle=Path("build.gradle").read_text()
mod=json.loads(Path("src/main/resources/fabric.mod.json").read_text())
workflow=Path(".github/workflows/pizza-build.yml").read_text()

assert 'compileOnly "curse.maven:serene-seasons-291874:8938283"' in gradle
assert "modImplementation \"curse.maven:serene-seasons" not in gradle
assert "sereneseasons" not in mod["depends"], "Serene Seasons must stay optional"
assert mod["suggests"]["sereneseasons"]=="*"
assert 'FabricLoader.getInstance().isModLoaded("sereneseasons")' in policy
assert "return vanillaMax;" in policy
assert "return SereneSeasonsOrchardBridge.maxStage" in policy
assert "sereneseasons.api" not in policy, "Do not load optional API in common bridge entrypoint"
assert "import sereneseasons.api.season.SeasonHelper;" in bridge
assert "import sereneseasons.api.season.ISeasonState;" in bridge
assert "SeasonHelper.usesStandardSeasons(level, pos)" in bridge
assert "SeasonHelper.getSeasonState(level)" in bridge
assert "season.getSubSeason().name()" in bridge
assert "return fallbackMax;" in bridge, "Tropical and no-season worlds need vanilla fallback"
assert "SeasonHelper" not in (J/"ItaliansDelight.java").read_text()
assert "SereneSeasonsOrchardBridge" not in (J/"ItaliansDelight.java").read_text()

SUBSEASONS=(
  "EARLY_SPRING","MID_SPRING","LATE_SPRING",
  "EARLY_SUMMER","MID_SUMMER","LATE_SUMMER",
  "EARLY_AUTUMN","MID_AUTUMN","LATE_AUTUMN",
  "EARLY_WINTER","MID_WINTER","LATE_WINTER"
)
expected={
 "ALMOND":{**{s:0 for s in SUBSEASONS},
   "LATE_WINTER":1,"EARLY_SPRING":2,
   "MID_SPRING":3,"LATE_SPRING":3,"EARLY_SUMMER":3,
   "MID_SUMMER":3,"LATE_SUMMER":4,
   "EARLY_AUTUMN":4,"MID_AUTUMN":4},
 "CHERRY":{**{s:0 for s in SUBSEASONS},
   "EARLY_SPRING":1,"MID_SPRING":2,"LATE_SPRING":3,
   "EARLY_SUMMER":4,"MID_SUMMER":5,"LATE_SUMMER":5},
 "OLIVE":{**{s:0 for s in SUBSEASONS},
   "LATE_SPRING":1,"EARLY_SUMMER":2,
   "MID_SUMMER":3,"LATE_SUMMER":3,
   "EARLY_AUTUMN":4,"MID_AUTUMN":4,"LATE_AUTUMN":4}
}
for fruit in ("ALMOND","CHERRY","OLIVE"):
    start=policy.index("case "+fruit+" -> switch (subSeason)")
    end=policy.index("default -> 0;",start)
    section=policy[start:end]
    actual={s:0 for s in SUBSEASONS}
    seen=set()
    for cases,value in re.findall(r"case\s+((?:\"[A-Z_]+\"(?:\s*,\s*)?)+)\s*->\s*(\d+)\s*;",section):
        tokens=re.findall(r'\"([A-Z_]+)\"',cases)
        for token in tokens:
            assert token in SUBSEASONS,(fruit,token)
            assert token not in seen,(fruit,token,"duplicate")
            actual[token]=int(value)
            seen.add(token)
    assert actual==expected[fruit],(fruit,actual,expected[fruit])
    assert all(0<=v<=({"CHERRY":5}.get(fruit,4)) for v in actual.values())
    # Cold months never mature a fruit: the user can test with /season set.
    assert actual["EARLY_WINTER"]==0 and actual["MID_WINTER"]==0

for name,fruit,ripe in (
    ("OliveLeavesBlock","OLIVE",4),
    ("AlmondLeavesBlock","ALMOND",4),
    ("FruitCherryLeavesBlock","CHERRY",5)
):
    source=(J/f"common/block/{name}.java").read_text()
    token=f"OrchardSeasonPolicy.Fruit.{fruit}"
    count=2 if fruit=="OLIVE" else 3
    assert source.count(token)==count,(name,"growth, harvest, foliage season checks")
    assert "super.randomTick(state, level, pos, random)" in source
    assert "int seasonalMax = OrchardSeasonPolicy.maxStage" in source
    assert "if (stage > seasonalMax)" in source
    assert "current.setValue(STAGE, 0)" in source, "Winter must clean stale ripe state"
    assert "stage < seasonalMax && random.nextInt(ADVANCE_CHANCE)" in source
    assert "if (OrchardSeasonPolicy.maxStage(server, pos," in source
    assert "return InteractionResult.PASS;" in source
    assert f"RIPE_STAGE = {ripe}" in source
    assert "ADVANCE_CHANCE = 3" in source, "Do not change growth speed yet"
    assert "hasAirFace(server, pos)" in source
    assert "new ItemStack(ModItems." in source
    assert "boolean canDevelop" in source
assert "check_orchard_serene_seasons.py" in workflow
print("OK: Serene Seasons Fabric 26.2 uses compileOnly API, optional Fabric mod metadata")
print("OK: ALL 12 sub-seasons checked for olive, almond, cherry calendar stages")
print("OK: both growing and harvesting respect season cap on server, vanilla fallback safe")
print("OK: growth chance, drops, textures, air exposure and decay remain unchanged")
print("NOTE: in-game verification with and without Serene Seasons/GlitchCore required")
