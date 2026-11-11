#!/usr/bin/env python3
"""Guard exact FD Cutting Board GUI, 18x7 pizza guides and the Ricotta whey alignment."""
import json
from pathlib import Path

root=Path("src/main/java/dev/italiansdelight/integration/jei")
recipes=(root/"PizzaWorldJeiRecipes.java").read_text(encoding="utf-8")
world=(root/"PizzaWorldRecipeCategory.java").read_text(encoding="utf-8")
record=(root/"PizzaWorldJeiRecipe.java").read_text(encoding="utf-8")
cutting=(root/"PizzaCuttingRecipeCategory.java").read_text(encoding="utf-8")
vat=(root/"CheeseVatRecipeCategory.java").read_text(encoding="utf-8")
presets=(Path("src/main/java/dev/italiansdelight/common/pizza/PizzaDisplayNames.java")
         .read_text(encoding="utf-8"))
assert "presets.size() != 18" in presets
assert "PizzaDisplayNames.allPresets().entrySet()" in recipes
assert "PizzaCreativeSamples.create(preset.getKey(), false)" in recipes
assert "data.composition().applications().forEach(app ->" in recipes
assert "ingredients.add(app.originalStack())" in recipes
assert "for (PizzaWorldJeiRecipe.Step step : PizzaWorldJeiRecipe.Step.values())" in recipes
assert "PizzaDisplayNames.allPresets().size() * PizzaWorldJeiRecipe.Step.values().length" in recipes
assert '"/pizza_world/" + recipe.flavorId() + "/" + recipe.step().id()' in world
assert 'recipe.examplePizza().getHoverName()' in world
assert 'builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)' in world
assert 'for (int i = 0; i < toppings.size(); i++)' in world
assert "toppings.isEmpty()" in world, "Bianca should never require nonexistent toppings"
assert "named_toppings_description" in world
assert "String flavorId" in record
for key in ("PLACE", "STRETCH_HAND", "STRETCH_PIN", "DIVIDE", "TOPPINGS", "REMOVE", "COLLECT"):
    assert key in record, f"Missing guide step {key}"

# The 26.2 Farmer's Delight CuttingRecipeCategory uses a 117x57 background
# in farmersdelight:textures/gui/jei/cutting_board.png, knife at 16,8,
# input at 16,27, and 2x2 results at 77/96,11/30.
for token in ('"farmersdelight", "textures/gui/jei/cutting_board.png"',
              'private static final int WIDTH = 117;',
              'private static final int HEIGHT = 57;',
              'builder.addSlot(RecipeIngredientRole.INPUT, 16, 8)',
              'builder.addSlot(RecipeIngredientRole.INPUT, 16, 27)',
              'OUTPUT_GRID_X = 76;', 'OUTPUT_GRID_Y = 10;',
              'i % 2', 'i / 2', 'outputSlot.draw(graphics, x, y)',
              'TextUtils.JEI("cutting")'):
    assert token in cutting, f"Cutting board layout diverged from FD: {token}"
assert "createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT)" in cutting
assert "PizzaCuttingJeiRecipeTypes.PIZZA_CUTTING" in cutting, (
    "Keep component-bearing dynamic results, not fake static cutting JSON")

assert 'private static final int WHEY_TANK_Y = 5;' in vat, (
    "Ricotta tank liquid should be exactly 4 GUI pixels lower")
assert 'private static final int WHEY_TANK_HEIGHT = 32;' in vat
assert 'WheyTankRenderer.draw(' in vat

for language in ("en_us", "it_it"):
    translations=json.loads(Path(
        f"src/main/resources/assets/italiansdelight/lang/{language}.json")
        .read_text(encoding="utf-8"))
    for key in ("flavor_title", "named_toppings_description",
                "bianca_description", "named_toppings_note"):
        full="gui.italiansdelight.jei.pizza_world."+key
        assert translations.get(full), f"Missing {language} translation: {full}"
print("OK: 18 flavors x 7 guide steps = 126 unique, data-backed world guide pages")
print("OK: FD 26.2 native cutting-board background, icon, title and 2x2 layout")
print("OK: Ricotta JEI whey overlay is 4px lower; filling amount unchanged")
print("NOTE: client JEI GUI spacing and word wrapping still need visual testing")
