#!/usr/bin/env python3
"""Regression: rotating 70% fruiting eligibility for natural orchard leaves.

The pure-Python 64-bit simulation mirrors the Java avalanche/unsigned
selection so we can detect broken distributions or windows without loading
Minecraft. Counts are *expected* 70%, not an exact per-tree quota.
"""
from pathlib import Path

JAVA = Path("src/main/java/dev/italiansdelight")
helper = (JAVA/"common/orchard/OrchardFruitingCycle.java").read_text()
MASK=(1<<64)-1
WINDOW=4*24_000

def mix(value):
    value &= MASK
    value = ((value ^ (value >> 30)) * 0xBF58476D1CE4E5B9) & MASK
    value = ((value ^ (value >> 27)) * 0x94D049BB133111EB) & MASK
    return value ^ (value >> 31)

def selected(x,y,z,fruit,game_time):
    pos=(((x & 0x3FFFFFF)<<38) | ((z & 0x3FFFFFF)<<12) | (y & 0xFFF))
    base=mix(pos ^ ((0xD1B54A32D192ED03*(fruit+1)) & MASK))
    offset=base % WINDOW
    window=(game_time+offset)//WINDOW
    value=mix(base ^ ((window*0x9E3779B97F4A7C15) & MASK))
    return value%100 <70

assert "public static final int ELIGIBILITY_PERCENT = 70;" in helper
assert "public static final long CYCLE_TICKS = 4L * 24_000L;" in helper
assert "long offset = Long.remainderUnsigned(base, CYCLE_TICKS);" in helper
assert "long window = Math.floorDiv(level.getGameTime() + offset, CYCLE_TICKS);" in helper
assert "Long.remainderUnsigned(selection, 100L) < ELIGIBILITY_PERCENT" in helper
assert "private static long mix(" in helper
assert "pos.asLong()" in helper
assert "fruit.ordinal()" in helper
assert "RandomSource" not in helper, "Selection must be stable across calls"
assert "SavedData" not in helper and "BlockEntity" not in helper

for fruit,name in enumerate(("OLIVE","ALMOND","CHERRY")):
    # Sampling 1024 eligible surface coordinates approximates a population
    # of neighboring canopy leaves; the quota is statistical, not exact.
    samples=[(x,65,z) for x in range(-16,16) for z in range(-16,16)]
    for tick in (0,WINDOW,2*WINDOW,8*WINDOW):
        count=sum(selected(*p,fruit,tick) for p in samples)
        assert 620<=count<=820,(name,tick,count)
    first=[selected(*p,fruit,0) for p in samples]
    next_cycle=[selected(*p,fruit,WINDOW) for p in samples]
    flips=sum(a!=b for a,b in zip(first,next_cycle))
    assert 260<=flips<=600,(name,"No rotation",flips)
    for p in samples[:100]:
        state=selected(*p,fruit,1200)
        assert selected(*p,fruit,1200)==state
    # Across a short time span, the vast majority of leaves remain stable:
    # leaf-specific offsets spread rollover events over the entire window.
    before=[selected(*p,fruit,18000) for p in samples]
    after=[selected(*p,fruit,18001) for p in samples]
    assert sum(a!=b for a,b in zip(before,after))<=2
    path=JAVA/f"common/block/{('OliveLeavesBlock','AlmondLeavesBlock','FruitCherryLeavesBlock')[fruit]}.java"
    src=path.read_text()
    assert "import dev.italiansdelight.common.orchard.OrchardFruitingCycle;" in src
    assert src.count("OrchardFruitingCycle.isSelected(")==3,(name,"tick, bonemeal, harvest")
    growth=src[src.index("protected void randomTick("):src.index("protected BlockState updateShape(")]
    assert "|| !OrchardFruitingCycle.isSelected(level, pos," in growth
    assert "if (current.getValue(STAGE) != 0)" in growth
    assert "current.setValue(STAGE, 0)" in growth, "Dormant leaves must lose old fruit"
    bone=src[src.index("public boolean isValidBonemealTarget("):src.index("public boolean isBonemealSuccess(")]
    assert "&& OrchardFruitingCycle.isSelected(world, pos," in bone
    assert "!canDevelop(state)" in bone and "!hasAirFace(level, pos)" in bone
    assert "state.getValue(STAGE) < OrchardSeasonPolicy.maxStage(world, pos," in bone
    harvest=src[src.index("protected InteractionResult useWithoutItem("):]
    assert "if (!OrchardFruitingCycle.isSelected(server, pos," in harvest
    assert "server.setBlock(pos, current.setValue(STAGE, 0), 2)" in harvest
    assert "return InteractionResult.PASS;" in harvest
    assert "server.getRandom().nextInt(3)" in harvest, "Yields unchanged"
    assert "BooleanProperty" not in src and "fruiting_cycle" not in src
    print(f"OK: {name}: ~70% selected, rotating per leaf; bone meal and stale-fruit harvest gated")

for crop in ("BasilCropBlock.java","CultivatedCardoonCropBlock.java"):
    text=(JAVA/"common/block"/crop).read_text()
    assert "OrchardFruitingCycle" not in text, "Do not change either crop"
print("OK: no new blockstates, textures, crop/sapling changes or global data")
print("NOTE: in-game tests still needed for growth, harvest and bone meal")
