#!/usr/bin/env python3
"""Serene Seasons 26.2 native greenhouse and sapling parity with vanilla/FDR.

Upstream reference:
 - Serene Seasons SeasonalCropGrowthHandler.isGlassAboveBlock(): 1..16 above
   with sereneseasons:greenhouse_glass (c:glass_blocks / forge:glass).
 - Vanilla cherry sapling: spring, birch: spring+autumn, oak: year-round.
 - Farmer's Delight Refabricated 26.2: uses Serene Seasons crop block/item
   tags; wild bonemeal plants have no separate season growth override.
"""
from pathlib import Path
import json

R=Path("src/main/resources/data/sereneseasons/tags")
J=Path("src/main/java/dev/italiansdelight")
bridge=(J/"common/orchard/SereneSeasonsOrchardBridge.java").read_text()
policy=(J/"common/orchard/OrchardSeasonPolicy.java").read_text()
modblocks=(J/"common/registry/ModBlocks.java").read_text()
workflow=Path(".github/workflows/pizza-build.yml").read_text()

assert "import sereneseasons.init.ModTags;" in bridge
assert "ModTags.Blocks.GREENHOUSE_GLASS" in bridge
assert "private static boolean hasGreenhouseGlass(Level level, BlockPos pos)" in bridge
assert "for (int height = 1; height <= 16; ++height)" in bridge
assert "level.getBlockState(pos.above(height))" in bridge
assert bridge.count("hasGreenhouseGlass(level, pos)") == 2, (
    "Greenhouses must affect both fruit maturity and visual leaf state")
assert "if (hasGreenhouseGlass(level, pos)) return 0;" in bridge, (
    "No yellow foliage hiding greenhouse-grown fruit in autumn/winter")
assert "if (hasGreenhouseGlass(level, pos)) {" in bridge
assert "return fallbackMax;" in bridge, (
    "Greenhouse fruiting must bypass seasonal stage cap")
assert "SeasonHelper.usesStandardSeasons(level, pos)" in bridge
assert "return OrchardSeasonPolicy.stageCap(fruit, season.getSubSeason().name());" in bridge, (
    "Outside a greenhouse, retain the approved fruiting calendar")
assert 'isModLoaded("sereneseasons")' in policy, (
    "Optional integration must never load absent Serene Seasons")
assert "return vanillaMax;" in policy
assert "public static int maxStage(" in policy

# Serene Seasons applies fertility via its own 4-season tags. No added
# sapling growth speed/random tick class: keep vanilla SaplingBlock behavior.
for id in ("olive","almond","fruit_cherry"):
    item=f"italiansdelight:{id}_sapling"
    assert f'new SaplingBlock(' in modblocks
    assert f'ModRegistries.id("{id}_sapling")' in modblocks
    found={season for season in ("spring","summer","autumn","winter","year_round")
           if (R/"block"/f"{season}_crops.json").exists() and
           item in json.loads((R/"block"/f"{season}_crops.json").read_text())["values"]}
    expected={
        "olive":{"year_round"},       # vanilla oak_sapling
        "almond":{"spring","autumn"}, # vanilla birch_sapling
        "fruit_cherry":{"spring"}    # vanilla cherry_sapling
    }[id]
    assert found==expected,(id,found,expected)
    for season in ("spring","summer","autumn","year_round"):
        if (R/"item"/f"{season}_crops.json").exists():
            blocks=set(json.loads((R/"block"/f"{season}_crops.json").read_text())["values"])
            items=set(json.loads((R/"item"/f"{season}_crops.json").read_text())["values"])
            assert blocks==items,season
    protected=json.loads((R/"block/unbreakable_infertile_crops.json").read_text())["values"]
    assert item in protected, "No sapling can break due to out-of-season setting"

# Keep wild herbs aligned with FDR: no independent seasonal bonemeal gating.
for block in ("WildBasilBlock.java","CardoonBlock.java"):
    s=(J/"common/block"/block).read_text()
    assert "implements BonemealableBlock" in s
    assert "sereneseasons" not in s
    assert "OrchardSeasonPolicy" not in s
assert "check_orchard_greenhouse_saplings.py" in workflow

print("OK: glass in Serene Seasons native greenhouse tag 1..16 above ALL orchard leaves")
print("OK: greenhouse fruiting ignores out-of-season cap, while outdoors still enforces it")
print("OK: all greenhouse fruit keeps normal green foliage; outside foliage still seasonal")
print("OK: saplings use vanilla-style native 4-season tags, no custom growth timers")
print("OK: optional Serene Seasons, native Farmer's Delight wild plant parity, no seasonal crop death")
print("NOTE: real in-game test with/without greenhouse and bonemeal is still required")
