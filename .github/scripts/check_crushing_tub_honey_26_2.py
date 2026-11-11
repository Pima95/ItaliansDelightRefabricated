"""Crushing Tub honeycomb -> honey, 250 mB, bottle and bucket, JEI parity.

These static checks complement—not replace—survival/client testing.
"""
from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[2]
JAVA = ROOT / "src/main/java/dev/italiansdelight"
REC = (JAVA / "common/block/CrushingTubRecipes.java").read_text()
BE = (JAVA / "common/block/entity/CrushingTubBlockEntity.java").read_text()
TUB = (JAVA / "common/block/CrushingTubBlock.java").read_text()
CONTAINERS = (JAVA / "common/block/BarrelFluidContainers.java").read_text()
JEI = (JAVA / "integration/jei/CrushingTubJeiRecipe.java").read_text()
CATEGORY = (JAVA / "integration/jei/CrushingTubRecipeCategory.java").read_text()
SURFACE = (JAVA / "client/render/LiquidSurfaceVisuals.java").read_text()

assert 'if (stack.is(Items.HONEYCOMB))' in REC
assert 'new Recipe("minecraft:honey", STANDARD_AMOUNT, ItemStack.EMPTY)' in REC
assert 'STANDARD_AMOUNT = 250' in REC
assert '"minecraft:honey", 250, Items.GLASS_BOTTLE' in CONTAINERS
assert 'if ("minecraft:honey".equals(id)) return new ItemStack(Items.HONEY_BOTTLE)' in CONTAINERS
assert 'if ("minecraft:honey".equals(id)) return new ItemStack(ModItems.HONEY_BUCKET)' in CONTAINERS
assert 'new Portion("minecraft:honey", 1000, Items.BUCKET)' in CONTAINERS
assert 'recipe.amount() > CAPACITY - amount' in BE
assert '(amount > 0 && !fluidId.equals(recipe.fluidId()))' in BE
assert 'CrushingTubRecipes.find(held) != null' in TUB
assert 'new ItemStack(Items.HONEYCOMB)' in JEI
assert 'Fluids.EMPTY, 250, ItemStack.EMPTY, new ItemStack(Items.HONEY_BOTTLE)' in JEI
assert 'builder.addOutputSlot(93, 26).add(recipe.bottlePreview())' in CATEGORY
assert 'builder.addOutputSlot(93, 26)' in CATEGORY
assert 'builder.addOutputSlot(126, 26)' in CATEGORY
assert 'gui.italiansdelight.jei.crushing_tub.honey_extraction' in CATEGORY
assert '"minecraft:honey", new Surface(' in SURFACE
assert '"block/honey_block_top"' in SURFACE
for code, text in (
    ('it_it', 'Il favo produce 250 mB di miele nel tino.'),
    ('en_us', 'Honeycomb produces 250 mB of honey in the tub.'),
):
    data = json.loads((ROOT / f"src/main/resources/assets/italiansdelight/lang/{code}.json").read_text())
    assert data['gui.italiansdelight.jei.crushing_tub.honey_extraction'].startswith(text)
print('OK: Crushing Tub honeycomb recipe, existing honey-bottle adapter, JEI and surface validated')
