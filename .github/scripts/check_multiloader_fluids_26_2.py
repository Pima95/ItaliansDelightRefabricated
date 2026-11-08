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
names = ["cow_milk", "sheep_milk", "whey", "rennet", "apple_cider_vinegar"]
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
assert 'FluidCatalog.canonicalId(input.getStringOr("FluidId", fallback))' in tank
assert '"minecraft:milk".equals(id)' in catalog
assert "FluidStorage.combinedItemApiProvider(Items.BUCKET)" in adapters
assert "FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE)" in adapters
assert "new FullItemFluidStorage(" in adapters
assert "new EmptyItemFluidStorage(" in adapters
assert "FabricFluidItemContainers.register();" in init
assert "FluidTransferBridge.register();" in init
print("OK: seven registered fluid families; five non-world liquids; legacy milk migration and item transfer adapters")
