#!/usr/bin/env python3
"""Orchards: medium-fast random-tick fruiting and natural-only bonemeal (Fabric 26.2).

No 70% foliage exclusion is implemented yet (separately approved later).
For default randomTickSpeed=3, 20 TPS, a single given leaf is selected
on average once every 4096/(3*20) = ~68.27 real seconds.
"""
from pathlib import Path
import math

BASE=Path("src/main/java/dev/italiansdelight/common/block")
WF=Path(".github/workflows/pizza-build.yml").read_text()
FRUITS=[
    ("OliveLeavesBlock.java","OLIVE",4,5,1,18,27),
    ("AlmondLeavesBlock.java","ALMOND",4,11,2,18,32),
    ("FruitCherryLeavesBlock.java","CHERRY",5,5,1,23,34)
]
mean_tick_seconds=4096/(3*20)
for name,fruit,steps,roll,success,quick,balanced in FRUITS:
    s=(BASE/name).read_text()
    probability=success/roll
    time_minutes=steps/probability*mean_tick_seconds/60
    assert quick<time_minutes<balanced,(name,time_minutes)
    assert f"RIPE_STAGE = {steps}" in s
    assert f"ADVANCE_CHANCE = {roll}" in s
    if success==1:
        assert "ADVANCE_SUCCESSES" not in s
        assert "random.nextInt(ADVANCE_CHANCE) == 0" in s
    else:
        assert f"ADVANCE_SUCCESSES = {success}" in s
        assert "random.nextInt(ADVANCE_CHANCE) < ADVANCE_SUCCESSES" in s

    assert "implements BonemealableBlock" in s, name
    assert "import net.minecraft.world.level.block.BonemealableBlock;" in s
    assert "public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state)" in s
    assert "public boolean isBonemealSuccess(Level level, RandomSource random," in s
    assert "public void performBonemeal(ServerLevel level, RandomSource random," in s
    assert "return true;" in s[s.index("public boolean isBonemealSuccess("):s.index("public void performBonemeal(")]
    assert "return !state.getValue(PERSISTENT)" in s
    assert "state.getValue(DISTANCE) < 7" in s
    assert "!state.getValue(WATERLOGGED)" in s
    validator=s[s.index("public boolean isValidBonemealTarget("):s.index("public boolean isBonemealSuccess(")]
    assert "!canDevelop(state)" in validator, "No hand-placed, detached or flooded leaves"
    assert "!hasAirFace(level, pos)" in validator, "No fertilizing enclosed inner canopy"
    assert "state.getValue(STAGE) >= RIPE_STAGE" in validator, "No wasted bonemeal on ripe fruit"
    assert "level instanceof Level world" in validator
    assert "OrchardSeasonPolicy.maxStage(world, pos," in validator
    assert f"OrchardSeasonPolicy.Fruit.{fruit}" in validator
    assert "state.getValue(STAGE) < OrchardSeasonPolicy.maxStage" in validator
    performer=s[s.index("public void performBonemeal("):s.index("protected InteractionResult useWithoutItem(")]
    assert "BlockState current = level.getBlockState(pos);" in performer
    assert "if (!isValidBonemealTarget(level, pos, current))" in performer
    assert "current.setValue(STAGE, current.getValue(STAGE) + 1)" in performer
    assert "Block.popResource" not in performer, "Bonemeal should not auto-harvest"
    assert "random.nextInt(" not in performer, "Single stage increment is predictable"
    assert "stage < seasonalMax && random.nextInt(ADVANCE_CHANCE)" in s
    assert "if (stage > seasonalMax)" in s
    assert "super.randomTick(state, level, pos, random)" in s
    assert "server.setBlock(pos, current.setValue(STAGE, 0), 2)" in s, "Harvest regrowth unchanged"
    assert "new ItemStack(ModItems." in s
    # No additional eligibility gates: 70% rotating fruiting leaf selection
    # is explicitly deferred.
    assert "FRUIT_ELIGIBILITY" not in s and "ELIGIBLE_PERCENT" not in s
    print(f"OK: {fruit} expected {time_minutes:.1f} min/leaf; bonemeal 1 growth phase on natural foliage only")

# Keep existing vanilla crop behavior and saplings unchanged.
for name in ("BasilCropBlock.java","CultivatedCardoonCropBlock.java"):
    s=(BASE/name).read_text()
    assert "extends CropBlock" in s and "implements BonemealableBlock" not in s
    assert "randomTick(" not in s
    assert "ADVANCE_CHANCE" not in s
assert "check_orchard_growth_bonemeal.py" in WF
print("OK: basil/cardoon not rebalanced; season/greenhouse caps still apply")
print("OK: no rotating 70% eligibility introduced; retained for future decision")
print("NOTE: in-game bonemeal and simulated growth timing must be tested")
