"""Regression checks for water-like world fluids without infinite sources (MC 26.2)."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
FLUIDS = ROOT / "src/main/java/dev/italiansdelight/common/fluid"
DATA = ROOT / "src/main/resources/data"
water = json.loads((DATA / "minecraft/tags/fluid/water.json").read_text())
assert water["replace"] is False
assert set(water["values"]) == {"#italiansdelight:olive_oil", "#italiansdelight:apple_juice"}
for name, java in [("olive_oil", "OliveOilFluid.java"), ("apple_juice", "AppleJuiceFluid.java")]:
    tag = json.loads((DATA / f"italiansdelight/tags/fluid/{name}.json").read_text())
    assert set(tag["values"]) == {f"italiansdelight:{name}", f"italiansdelight:flowing_{name}"}, name
    code = (FLUIDS / java).read_text()
    assert "canConvertToSource(ServerLevel world) { return false; }" in code, name
    assert "getSlopeFindDistance(LevelReader world) { return 4; }" in code, name
    assert "getDropOff(LevelReader world) { return 1; }" in code, name
    assert "getTickDelay(LevelReader world) { return 5; }" in code, name
# Storage-only fluids must not be assigned in-world water physics.
for name in ("sheep_milk", "whey", "rennet", "apple_cider_vinegar"):
    assert f"#italiansdelight:{name}" not in water["values"], name

print("OK: both world fluids have source+flowing water physics; canConvertToSource is always false")
