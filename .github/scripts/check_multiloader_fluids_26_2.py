from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / "src/main/java/dev/italiansdelight"
RES = ROOT / "src/main/resources"
catalog = (JAVA / "common/fluid/FluidCatalog.java").read_text()
model = (JAVA / "common/fluid/ContainedFluid.java").read_text()
reg = (JAVA / "common/registry/ModFluids.java").read_text()
tank = (JAVA / "common/fluid/CompatibleFluidTank.java").read_text()
adapters = (JAVA / "common/fluid/FabricFluidItemContainers.java").read_text()
init = (JAVA / "ItaliansDelight.java").read_text()
names = ["sheep_milk", "whey", "rennet", "apple_cider_vinegar"]
for n in names:
    assert f'new Definition("italiansdelight:{n}", false,' in catalog
    assert f'ModRegistries.id("{n}")' in reg
    assert f'"italiansdelight:{n}"' in catalog
    assert (RES/f"data/italiansdelight/tags/fluid/{n}.json").exists()
    assert f"ModFluids.{n.upper()}" in adapters
assert 'new Definition("italiansdelight:olive_oil", true,' in catalog
assert 'new Definition("italiansdelight:apple_juice", true,' in catalog
assert 'return Blocks.AIR.defaultBlockState();' in model
assert 'FluidVariant.CODEC' in tank
assert 'String id = input.getStringOr("FluidId", fallback);' in tank
assert "FluidCatalog.canonicalId(" not in tank
# No alias mapping, registry entry or fluid tag for vanilla cow milk.
assert 'italiansdelight:cow_milk' not in catalog
assert '"minecraft:milk".equals(id)' not in catalog
assert 'if ("italiansdelight:cow_milk".equals(id)) id = "minecraft:milk";' in tank
assert not (RES/"data/italiansdelight/tags/fluid/cow_milk.json").exists()
assert "ModFluids.COW_MILK" not in adapters
client=(JAVA/"ItaliansDelightClient.java").read_text()
atlas=(RES/"assets/minecraft/atlases/blocks.json").read_text()
for name, path in {
    "SHEEP_MILK": "sheep_milk_still",
    "WHEY": "whey_still",
    "RENNET": "rennet_still",
    "APPLE_CIDER_VINEGAR": "booze/cider_still"
}.items():
    assert "registerContainedFluid(ModFluids." + name in client
    assert "block/fluids/" + path in client
    assert "italiansdelight:block/fluids/" + path in atlas
assert 'FluidRenderingRegistry.register(fluid, new FluidModel.Unbaked(' in client
assert "FluidStorage.combinedItemApiProvider(Items.BUCKET)" in adapters
assert "FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE)" in adapters
assert "new FullItemFluidStorage(" in adapters
assert "new EmptyItemFluidStorage(" in adapters
assert "FabricFluidItemContainers.register();" in init
assert "FluidTransferBridge.register();" in init
print("OK: six registered fluid families; four non-world liquids; legacy milk migration and item transfer adapters")

# Fabric 26.2 FluidVariantAttributeHandler derives "block.<namespace>.<id>"
# for non-placeable fluids (createLegacyBlock() -> Blocks.AIR). JEI shows the
# raw key if only "fluid.<namespace>.<id>" is translated.
import json
for lang, expected in {
    "it_it": {
        "sheep_milk": "Latte di pecora",
        "whey": "Siero di latte",
        "rennet": "Caglio",
        "apple_cider_vinegar": "Aceto di mele",
    },
    "en_us": {
        "sheep_milk": "Sheep Milk",
        "whey": "Whey",
        "rennet": "Rennet",
        "apple_cider_vinegar": "Apple Cider Vinegar",
    },
}.items():
    translations=json.loads((RES / f"assets/italiansdelight/lang/{lang}.json").read_text())
    for fluid_id, display in expected.items():
        assert translations[f"block.italiansdelight.{fluid_id}"] == display
        assert translations[f"fluid.italiansdelight.{fluid_id}"] == display
print("OK: four storage-only liquid JEI display names translated in Italian and English")
