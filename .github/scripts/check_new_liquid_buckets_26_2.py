"""Regression: all configured liquid bucket variants; source world-fluid safety."""
import json, hashlib, struct
from pathlib import Path

root=Path(__file__).resolve().parents[2]
src=root/"src/main/java/dev/italiansdelight"
res=root/"src/main/resources"
items=(src/"common/registry/ModItems.java").read_text()
fluids=(src/"common/registry/ModFluids.java").read_text()
adapt=(src/"common/fluid/FabricFluidItemContainers.java").read_text()
bridge=(src/"common/block/BarrelFluidContainers.java").read_text()
creative=(src/"common/item/group/ModItemGroups.java").read_text()
info=(src/"integration/jei/ModFluidJeiInfo.java").read_text()
water=(res/"data/minecraft/tags/fluid/water.json").read_text()
tag=json.loads((res/"data/c/tags/item/buckets.json").read_text())
assert tag["replace"] is False
for id, sha256 in [
    ("golden_apple_juice_bucket","18076f389bed8b4879a4ef583b2b59bd7fe1892a9039f5941ec91f3199eddfd3"),
    ("honey_bucket","f955b4a51d1efb3ec149914ce7d93e1d70d8375f67f1a9bae17b3a82a6cae4bc")
]:
    symbol=id.upper()
    assert f"public static final Item {symbol}" in items
    assert f"ModItems.{symbol}" in creative
    assert f"new ItemStack(ModItems.{symbol})" in info
    assert f"italiansdelight:{id}" in tag["values"]
    assert f"if (stack.is(ModItems.{symbol}))" in bridge
    assert f"new ItemStack(ModItems.{symbol})" in bridge
    assert f"new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)" in items
    j=json.loads((res/f"assets/italiansdelight/items/{id}.json").read_text())
    assert j["model"]["model"]==f"italiansdelight:item/{id}"
    m=json.loads((res/f"assets/italiansdelight/models/item/{id}.json").read_text())
    assert m["textures"]["layer0"]==f"italiansdelight:item/{id}"
    png=(res/f"assets/italiansdelight/textures/item/{id}.png").read_bytes()
    assert png[:8]==b"\x89PNG\r\n\x1a\n"
    assert struct.unpack_from(">IIBBBBB",png,16)==(16,16,8,6,0,0,0)
    assert hashlib.sha256(png).hexdigest()==sha256
    for lang in ("it_it","en_us"):
        translations=json.loads((res/f"assets/italiansdelight/lang/{lang}.json").read_text())
        assert translations[f"item.italiansdelight.{id}"]
assert "new ContainedFluid(() -> ModItems.GOLDEN_APPLE_JUICE_BUCKET)" in fluids
assert "storageOnly(ModFluids.GOLDEN_APPLE_JUICE," in adapt
assert "ModItems.GOLDEN_APPLE_JUICE_BUCKET, ModItems.GOLDEN_APPLE_JUICE_BOTTLE" in adapt
assert 'new Portion("minecraft:honey", 1000, Items.BUCKET)' in bridge
assert 'new Portion("italiansdelight:golden_apple_juice", 1000, Items.BUCKET)' in bridge
assert 'if ("minecraft:honey".equals(id)) return new ItemStack(ModItems.HONEY_BUCKET)' in bridge
assert '"italiansdelight:golden_apple_juice"' not in water
assert '"minecraft:honey"' not in water
assert 'new BucketItem(ModFluids.GOLDEN_APPLE_JUICE' not in items
assert 'new BucketItem(ModFluids.' not in items.split("public static final ResourceKey<Item> GOLDEN_APPLE_JUICE_BUCKET_KEY")[1].split("public static final ResourceKey<Item> OLIVE_OIL_BUCKET_KEY")[0]
assert '"minecraft:honey"' in (src/"common/block/CrushingTubRecipes.java").read_text()
print("OK: both 1000mB bucket variants, vanilla metal assets, no world placement or migrated IDs")
