#!/usr/bin/env python3
"""Static reproducible checks: Alcocraft-derived geometry and 14 wood fermenters."""
from pathlib import Path
import hashlib
import json
import struct

root = Path("src/main/resources")
assets = root / "assets/italiansdelight"
data = root / "data/italiansdelight"
java = Path("src/main/java/dev/italiansdelight")
woods = (
    "oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
    "mangrove", "cherry", "bamboo",
    "crimson", "warped", "olive", "almond", "fruit_cherry"
)
custom = {"olive", "almond", "fruit_cherry"}

def load(path):
    return json.loads(path.read_text(encoding="utf-8"))

model = load(assets / "models/block/brewing_barrel.json")
assert model["texture_size"] == [64, 64]
assert model["textures"]["0"] == "italiansdelight:block/brewing_barrel"
assert len(model["elements"]) == 22, "Must preserve imported keg geometry"
assert any(e.get("name") == "top_plug" for e in model["elements"])
assert any(e.get("name") == "front_plug" for e in model["elements"])
assert model["credit"] == "Made with Blockbench"

lang_en = load(assets / "lang/en_us.json")
lang_it = load(assets / "lang/it_it.json")
registration = (java / "common/registry/ModBlocks.java").read_text(encoding="utf-8")
entities = (java / "common/registry/ModBlockEntities.java").read_text(encoding="utf-8")
creative = (java / "common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
block_code = (java / "common/block/BrewingBarrelBlock.java").read_text(encoding="utf-8")
assert "EnumProperty<Direction> FACING" in block_code
assert 'getHorizontalDirection().getOpposite()' in block_code
assert "builder.add(FACING)" in block_code
assert 'register("brewing_barrel", BrewingBarrelBlockEntity::new' in entities
assert "getTicker(" in block_code
assert "preserveContents(stack" in block_code, "Keep item contents on breaking"
# Rustic 1.12.2 GUI layout, but native Fabric 26.3 menu and preserved tank IO.
barrel_entity = (java/"common/block/entity/BrewingBarrelBlockEntity.java").read_text(encoding="utf-8")
barrel_menu = (java/"common/block/entity/container/BrewingBarrelMenu.java").read_text(encoding="utf-8")
barrel_screen = (java/"client/gui/BrewingBarrelScreen.java").read_text(encoding="utf-8")
menu_registry = (java/"common/registry/ModMenuTypes.java").read_text(encoding="utf-8")
client_init = (java/"ItaliansDelightClient.java").read_text(encoding="utf-8")
assert 'public static final MenuType<BrewingBarrelMenu> BREWING_BARREL' in menu_registry
assert 'MenuScreens.register(\n            ModMenuTypes.BREWING_BARREL' in client_init
assert "player.openMenu(barrel)" in block_code
assert "implements Container, MenuProvider" in barrel_entity
assert "ContainerHelper.saveAllItems(output, items)" in barrel_entity
assert "ContainerHelper.loadAllItems(input, items)" in barrel_entity
assert "if (isEmpty() && juiceAmount == 0" in barrel_entity
assert "cultureAmount == 0 && progress == 0" in barrel_entity
for index,x,y in (("INPUT_IN",62,7),("OUTPUT_IN",116,7),("CULTURE_IN",26,15),
                  ("INPUT_OUT",62,63),("OUTPUT_OUT",116,63),("CULTURE_OUT",26,55)):
    name=f"BrewingBarrelBlockEntity.{index}"
    action="addInputSlot" if index.endswith("_IN") else "addReturnSlot"
    assert f"{action}({name}, {x}, {y} + MACHINE_Y_OFFSET)" in barrel_menu, index
assert "public static final int MACHINE_Y_OFFSET = 20" in barrel_menu
assert "public static final int INVENTORY_Y_OFFSET = 32" in barrel_menu
assert "addStandardInventorySlots(inventory, 8, 84 + INVENTORY_Y_OFFSET)" in barrel_menu
assert "GUI_HEIGHT = 198" in barrel_screen
assert "this.inventoryLabelY = 104" in barrel_screen
assert "this.titleLabelY = 6" in barrel_screen
assert "font.split(title, GUI_WIDTH - 16)" in barrel_screen
assert "drawExtendedBackground(graphics)" in barrel_screen
# Fluids are 16x512 animated strips (32 native 16px frames). They must be
# repeated at the actual sprite size, never stretched to the 32px tall tanks.
assert "private static final int FLUID_FRAME_SIZE = 16;" in barrel_screen
assert "row += FLUID_FRAME_SIZE" in barrel_screen
assert "absoluteX, absoluteY + row, FLUID_FRAME_SIZE, FLUID_FRAME_SIZE" in barrel_screen
assert "graphics.enableScissor(absoluteX, surfaceY" in barrel_screen
assert "graphics.fill(absoluteX, surfaceY" in barrel_screen
assert "absoluteX, absoluteY, 16, height" not in barrel_screen
# New full-resolution vessel silhouettes live INSIDE the existing 256x256
# brewing_barrel.png, not a second texture atlas. The redesigned bucket has
# the curved inner rim (rows 6-7) from the original Rustic depth indicator.
assert "VESSEL_U = 176" in barrel_screen
assert "BUCKET_V = 54" in barrel_screen
assert "BOTTLE_V = 70" in barrel_screen
assert "iconX, iconY, VESSEL_U, bucket ? BUCKET_V : BOTTLE_V," in barrel_screen
assert "16, 16, 256, 256" in barrel_screen
assert "int iconX = leftPos + slot.x;" in barrel_screen
assert "int iconY = topPos + slot.y;" in barrel_screen
assert "VESSEL_ICONS" not in barrel_screen
assert "BOTTLE_ICON_SIZE" not in barrel_screen
assert not (assets/"textures/gui/brewing_vessel_icons.png").exists()
atlas=assets/"textures/gui/brewing_barrel.png"
atlas_bytes=atlas.read_bytes()
assert atlas_bytes[:8] == bytes.fromhex("89504e470d0a1a0a")
assert struct.unpack(">II", atlas_bytes[16:24]) == (256,256)
assert atlas_bytes[24:26] == bytes((8,6)), "GUI must be RGBA8"
assert hashlib.sha256(atlas_bytes).hexdigest() == "533ddfce1aff9f4b49a59ac108c83d45d028d705577452adf4e3245888198e17", "Unexpected modification to approved GUI art"
# Opaque but GUI-only backing is clipped to the actual filled liquid height;
# the original animated textures remain unchanged for the world and JEI.
assert "if (backingColor != 0)" in barrel_screen
assert "graphics.fill(absoluteX, surfaceY," in barrel_screen
assert "0xFF87395F" in barrel_screen and "0xFF7B304D" in barrel_screen
assert "case BrewingBarrelBlockEntity.JUICE_CHERRY -> CHERRY_JUICE;" in barrel_screen
assert "case BrewingBarrelBlockEntity.JUICE_HONEY -> MEAD;" in barrel_screen
assert "case BrewingBarrelBlockEntity.OUTPUT_WHITE_WINE -> WHITE_WINE;" in barrel_screen
assert "case BrewingBarrelBlockEntity.OUTPUT_CHERRY_WINE -> CHERRY_WINE;" in barrel_screen
assert "case BrewingBarrelBlockEntity.OUTPUT_MEAD -> MEAD;" in barrel_screen
assert "case BrewingBarrelBlockEntity.OUTPUT_CIDER -> CIDER;" in barrel_screen
assert "case BrewingBarrelBlockEntity.OUTPUT_WINE_VINEGAR -> WINE_VINEGAR;" in barrel_screen
assert "menu.hasVinegarOutput() ? -1 : menu.getOutputQuality()" in barrel_screen
assert "switch (menu.getCultureWineKind())" in barrel_screen
assert "case 1 -> WHITE_WINE;" in barrel_screen
assert "case 2 -> CHERRY_WINE;" in barrel_screen
assert 'input.getIntOr("CultureWineKind", 0)' in barrel_entity
assert 'output.putInt("CultureWineKind", cultureWineKind);' in barrel_entity
assert "emptyVesselIcon(graphics, BrewingBarrelBlockEntity.INPUT_IN, true)" in barrel_screen
assert "emptyVesselIcon(graphics, BrewingBarrelBlockEntity.OUTPUT_IN, false)" in barrel_screen
assert "emptyVesselIcon(graphics, BrewingBarrelBlockEntity.CULTURE_IN, false)" in barrel_screen
assert "leftPos + 62, topPos + 7 + MACHINE_SHIFT, 176, 54" not in barrel_screen
for relative in (
    "block/fluids/grape_juice_still.png",
    "block/fluids/white_grape_juice_still.png",
    "block/fluids/booze/wine_still.png",
):
    path=assets/"textures"/relative
    raw=path.read_bytes()
    assert raw[:8]==bytes.fromhex("89504e470d0a1a0a")
    assert struct.unpack(">II", raw[16:24])==(16,512), relative
    animated=load(path.with_suffix(path.suffix+".mcmeta"))
    assert animated["animation"]["frametime"]>0, relative

assert "Component.translatable(getBlockState().getBlock().getDescriptionId())" in barrel_entity
# Every registered wood still has a translated display key and its own identity.
assert "getBlockState().getBlock().getDescriptionId()" in barrel_entity
jei_plugin=(java/"integration/jei/ItaliansDelightJeiPlugin.java").read_text(encoding="utf-8")
jei_category=(java/"integration/jei/BrewingBarrelJeiRecipeCategory.java").read_text(encoding="utf-8")
jei_guide=(java/"integration/jei/BrewingBarrelJeiGuide.java").read_text(encoding="utf-8")
jei_types=(java/"integration/jei/BrewingBarrelJeiRecipeTypes.java").read_text(encoding="utf-8")
assert 'new BrewingBarrelJeiRecipeCategory(' in jei_plugin
assert 'BrewingBarrelJeiRecipeTypes.BARREL_GUI_GUIDE' in jei_plugin
assert 'BrewingBarrelScreen.class' in jei_plugin
assert "implements IRecipeCategory<BrewingBarrelJeiGuide>" in jei_category
assert 'textures/gui/jei/brewing_barrel.png' in jei_category
assert "private static final int WIDTH = 130;" in jei_category
assert "private static final int HEIGHT = 80;" in jei_category
assert "background.draw(graphics);" in jei_category
assert "arrow.draw(graphics, 63, 32)" in jei_category
assert "bubbles.draw(graphics, 117, 26)" in jei_category
assert "cultureArrow.draw(graphics, 23, 35)" in jei_category
assert "builder.addInputSlot(40, 24)" in jei_category
assert "builder.addOutputSlot(94, 24)" in jei_category
assert "builder.addText(" not in jei_category
assert "createRecipeExtras" not in jei_category
assert (assets/"textures/gui/jei/brewing_barrel.png").exists()
assert "builder.addOutputSlot(" in jei_category
assert "recipe.input()" in jei_category
assert "recipe.output()" in jei_category
# Mead now consumes the registered honey Fluid, not a visible honey-bottle
# item. Check the actual recipe mapping and the fluid renderer instead.
assert "ModFluids.HONEY" in jei_guide
assert ".setFluidRenderer(MAX_BATCH, false, 16, 32)" in jei_category
assert "new ItemStack(Items.HONEY_BOTTLE)" not in jei_category
assert "recipe.requiresCulture()" in jei_category
assert "ModFluids.WHITE_GRAPE_JUICE" in jei_guide
assert "ModFluids.CHERRY_JUICE" in jei_guide
assert "ModFluids.MEAD" in jei_guide
assert "ModFluids.CIDER" in jei_guide
assert "ModFluids.RED_WINE_VINEGAR" in jei_guide
assert 'new BrewingBarrelJeiGuide("red_wine")' in jei_plugin
assert 'new BrewingBarrelJeiGuide("apple_cider_vinegar")' in jei_plugin
assert 'new BrewingBarrelJeiGuide("white")' not in jei_plugin
for key in ("white_wine","cherry_wine","mead","cider","red_wine_vinegar","cider_vinegar"):
    assert f'new BrewingBarrelJeiGuide("{key}")' in jei_plugin, key
assert 'brewing_barrel_gui_guide' in jei_types
assert "record BrewingBarrelJeiGuide" in jei_guide
for wood in woods:
    name=("BREWING_BARREL" if wood=="oak" else wood.upper()+"_BREWING_BARREL")
    assert f"ModBlocks.{name}" in jei_plugin, name
for locale in (lang_en,lang_it):
    for key in ("gui.italiansdelight.jei.brewing_barrel.title",
                "gui.italiansdelight.jei.brewing_barrel.slots",
                "gui.italiansdelight.jei.brewing_barrel.details"):
        assert locale[key], key

for required in ("case INPUT_IN", "case OUTPUT_IN", "case CULTURE_IN",
                 "case CULTURE_IN -> stack.is(Items.GLASS_BOTTLE)"):
    assert required in barrel_entity, required
assert "default -> false; // Result slots are extraction-only." in barrel_entity
assert "handleInputContainer()" in barrel_entity
assert "handleOutputContainer()" in barrel_entity
assert "handleCultureContainer()" in barrel_entity
# Rustic 1.12.2 behavior: 8L input/output, 1L reusable culture, 12,000-tick
# cycles and 5..75 / cultureQuality+(-1..+4) quality generation.
for required in (
    "public static final int CAPACITY = 8000;",
    "public static final int BREW_TIME = 12000;",
    "public static final int CULTURE_CAPACITY = 1000;",
    "public static final int QUALITY_CHANGE_MIN = -1;",
    "public static final int QUALITY_CHANGE_MAX = 4;",
    "if (++barrel.progress >= BREW_TIME)",
    "barrel.finishBrewing(level);",
    "int amount = Math.min(juiceAmount, CAPACITY - wineAmount);",
    "if (wineAmount == 0) {",
    "? brewedQuality(cultureAmount, cultureQuality, level.getRandom()) : 0;",
    "return 5 + random.nextInt(71);",
    "QUALITY_CHANGE_MIN",
    "barrel.progress = 0;",
    "juiceAmount -= amount;",
    "wineAmount += amount;",
    "output.putInt(\"CultureAmount\", cultureAmount);",
    "input.getIntOr(\"CultureAmount\",",
    "cultureAmount > 0 && cultureQuality < 0",
    "ItemStack.isSameItemSameComponents(slot, result)"
):
    assert required in barrel_entity, f"Missing Rustic brewing rule: {required}"
assert "case 2 -> cultureAmount;" in barrel_entity
assert "case 5 -> wineQuality;" in barrel_entity
assert "case 6 -> cultureQuality;" in barrel_entity
assert "case 8 -> cultureWineKind;" in barrel_entity
assert "case 8 -> cultureWineKind = Math.clamp(value, 0, 2);" in barrel_entity
assert "@Override public int getCount() { return 9; }" in barrel_entity
assert "new SimpleContainerData(9)" in barrel_menu
assert "public int getCultureWineKind() { return data.get(8); }" in barrel_menu
assert "checkContainerDataCount(data, 9)" in barrel_menu
assert "case 7 -> outputKind;" in barrel_entity
assert 'output.putInt("OutputKind", outputKind);' in barrel_entity
assert 'input.getIntOr("OutputKind", OUTPUT_WINE)' in barrel_entity
assert "JUICE_APPLE = 2" in barrel_entity
assert "OUTPUT_VINEGAR = 1" in barrel_entity
for path in (
    "case JUICE_WHITE -> OUTPUT_WHITE_WINE;",
    "case JUICE_CHERRY -> OUTPUT_CHERRY_WINE;",
    "case JUICE_HONEY -> OUTPUT_MEAD;",
    "case JUICE_RED_WINE -> OUTPUT_WINE_VINEGAR;",
    "case JUICE_CIDER -> OUTPUT_VINEGAR;",
    "case JUICE_APPLE -> hasCulture ? OUTPUT_CIDER : OUTPUT_VINEGAR;"):
    assert path in barrel_entity, path
assert "wineAmount == 0 || outputKind == expected" in barrel_entity
assert "ItemStack filled = outputContainer(outputKind, bucket, wineQuality);" in barrel_entity
assert "if (hasQuality(kind))" in barrel_entity
assert "ModItems.APPLE_CIDER_VINEGAR_BUCKET" in barrel_entity
assert "ModItems.APPLE_CIDER_VINEGAR" in barrel_entity
assert "ModItems.APPLE_JUICE_BOTTLE" in barrel_entity
assert "ModItems.APPLE_JUICE_BUCKET" in barrel_entity
assert "menu.hasVinegarOutput()" in barrel_screen
assert 'OutputKind' in barrel_entity
assert "isVinegar()" in jei_category
assert "BrewingBarrelBlockEntity.BREW_TIME" in barrel_menu
assert "progress > 0" in barrel_screen
assert "Math.min(24, progress * 24 / duration + 1)" in barrel_screen
assert "Math.min(10, progress * 10 / duration + 1)" in barrel_screen
assert "5600) % 28" in barrel_screen
assert "tooltip.italiansdelight.wine_quality" in barrel_screen
assert "ModItems.WINE_BUCKET" in barrel_entity, "A 1L bucket must be usable as a culture"
assert "if (cultureAmount == 0) cultureQuality = -1;" in barrel_entity
assert 'gui.italiansdelight.jei.brewing_barrel.disabled' not in lang_it
assert 'gui.italiansdelight.jei.brewing_barrel.disabled' not in lang_en
assert "fermentation is not implemented yet" not in jei_category.lower()
assert "isVinegar()" in jei_category
assert lang_it["gui.italiansdelight.brewing_barrel.input"] == "Serbatoio d'ingresso"
assert lang_it["gui.italiansdelight.brewing_barrel.output"] == "Serbatoio d'uscita"
assert lang_en["gui.italiansdelight.brewing_barrel.input"] == "Input tank"

assert 'textures/gui/brewing_barrel.png' in barrel_screen
assert all(label in lang_it for label in (
    "gui.italiansdelight.brewing_barrel.input",
    "gui.italiansdelight.brewing_barrel.output",
    "gui.italiansdelight.brewing_barrel.culture"))
gui_atlas=load(root/"assets/minecraft/atlases/gui.json")
sprites={entry.get("sprite") for entry in gui_atlas["sources"]}
for sprite in ("grape_juice_still","white_grape_juice_still","wine_still",
               "apple_juice_still","apple_cider_vinegar_still",
               "white_wine_still","cherry_juice_still","cherry_wine_still",
               "mead_still","cider_still","red_wine_vinegar_still"):
    assert "italiansdelight:fluids/"+sprite in sprites
# Added liquids use authentic 16x16 Rustic frame animations, not flattened art.
for id_ in ("white_wine","cherry_wine","mead","cherry_juice","red_wine_vinegar"):
    source=assets/"textures/block/fluids/booze"/(id_+"_still.png")
    assert source.is_file(), id_
    assert struct.unpack(">II", source.read_bytes()[16:24])==(16,512), id_
    assert load(source.with_suffix(source.suffix+".mcmeta"))["animation"]["frametime"]>0, id_
# Fermented products have direct kitchen uses. Regular red wine retains one
# grid-crafting variant: the redundant grid Amatriciana was intentionally
# removed in favor of the existing Cooking Pot recipe.
fermented=root/"data/italiansdelight/recipe/fermented"
recipe_files=list(fermented.glob("*.json"))
assert len(recipe_files)>=15
assert not (fermented/"red_wine_amatriciana.json").exists()
products=("white_wine_bottle","cherry_wine_bottle","mead_bottle",
          "cider_bottle","red_wine_vinegar_bottle","cherry_juice_bottle",
          "wine_bottle","apple_cider_vinegar")
for prod in products:
    uses=[p for p in recipe_files if "italiansdelight:"+prod in load(p)["ingredients"]]
    expected_uses=1 if prod=="wine_bottle" else 2
    assert len(uses)>=expected_uses, f"Missing kitchen uses: {prod}"
print("PASSED: fermented kitchen uses, no crafting-table Amatriciana, native Rustic sprites")


original = (Path(".github/scripts/fixtures/alcocraft_spruce_keg.png").read_bytes())
assert hashlib.sha1(f"blob {len(original)}\0".encode("ascii") + original).hexdigest() == \
       "15bc4dcee27b3cb34a82d9bbf9ac50a43bdbee26", "Original Alcocraft fixture changed"
for wood in woods:
    id_ = "brewing_barrel" if wood == "oak" else wood + "_brewing_barrel"
    name = "italiansdelight:" + id_
    blockstate = load(assets / "blockstates" / (id_ + ".json"))["variants"]
    assert set(blockstate) == {f"facing={d}" for d in ("north", "east", "south", "west")}
    for facing, rotation in (("north", 0), ("east", 90),
                             ("south", 180), ("west", 270)):
        entry = blockstate[f"facing={facing}"]
        assert entry["model"] == f"italiansdelight:block/{id_}"
        assert entry.get("y", 0) == rotation
    if wood != "oak":
        expected_ns = "italiansdelight" if wood in custom else "minecraft"
        sub = load(assets / "models/block" / (id_ + ".json"))
        assert sub["parent"] == "italiansdelight:block/brewing_barrel"
        assert sub["textures"]["0"] == f"italiansdelight:block/{id_}"
        assert sub["textures"]["particle"] == f"{expected_ns}:block/{wood}_planks"
        assert load(assets / "models/item" / (id_ + ".json"))["parent"] == f"italiansdelight:block/{id_}"
        assert load(assets / "items" / (id_ + ".json"))["model"]["model"] == f"italiansdelight:item/{id_}"
        recipe = load(data / "recipe" / (id_ + ".json"))
        assert recipe["result"]["id"] == name and recipe["result"]["count"] == 1
        assert recipe["key"]["P"] == f"{expected_ns}:{wood}_planks"
        assert recipe["key"]["S"] == f"{expected_ns}:{wood}_slab"
        loot = load(data / "loot_table/blocks" / (id_ + ".json"))
        assert loot["pools"][0]["entries"][0]["name"] == name
        assert f'{wood.upper()}_BREWING_BARREL' in registration
        assert f'ModBlocks.{wood.upper()}_BREWING_BARREL' in entities
        assert f'ModBlocks.{wood.upper()}_BREWING_BARREL' in creative
    assert lang_en[f"block.italiansdelight.{id_}"]
    assert lang_it[f"block.italiansdelight.{id_}"]
    png = (assets / "textures/block" / (id_ + ".png")).read_bytes()
    assert png[:8] == bytes.fromhex("89504e470d0a1a0a")
    assert struct.unpack(">II", png[16:24]) == (64, 64)
    assert png[24:26] == bytes((8, 6)), "PNG must be 64x64 RGBA8"
    assert png != original, f"{wood}: texture still flat/unmodified original"
assert "block.italiansdelight.brewing_barrel" in lang_it
assert 'BREWING_BARREL = register(' in registration
for notice in ("CREDIT.md", "THIRD_PARTY_NOTICES.md", "NOTICE", "LICENSE"):
    assert Path(notice).is_file(), f"Missing legal notice {notice}"
credits = Path("CREDIT.md").read_text(encoding="utf-8")
assert all(name in credits for name in ("hadrus91", "Alcocraft", "Pima95"))
third_party = Path("THIRD_PARTY_NOTICES.md").read_text(encoding="utf-8")
assert "Alcocraft: Beer & Stuff" in third_party
assert "MIT" in third_party
assert 'from("CREDIT.md")' in Path("build.gradle").read_text(encoding="utf-8")
print("PASSED: Alcocraft keg geometry and 64x64 wood palettes are packaged")
print("PASSED: 14 brewer IDs, blockstates, inventory models, recipes, loot, languages and attributions")

assert "Morogorian" not in credits
assert "Morogorian" not in third_party

# Depth regression checks, comparing every recolor with original Alcocraft
# pixels instead of enforcing the previously flat single-axis ramp.
import zlib
import math

def decode_rgba(path):
    data = path.read_bytes()
    assert data[:8] == bytes.fromhex("89504e470d0a1a0a")
    pos = 8
    compressed = bytearray()
    width = height = None
    while pos < len(data):
        n = int.from_bytes(data[pos:pos + 4], "big")
        kind = data[pos + 4:pos + 8]
        chunk = data[pos + 8:pos + 8 + n]
        pos += n + 12
        if kind == b"IHDR":
            width = int.from_bytes(chunk[0:4], "big")
            height = int.from_bytes(chunk[4:8], "big")
            assert chunk[8:10] == bytes((8, 6))
        elif kind == b"IDAT":
            compressed.extend(chunk)
        elif kind == b"IEND":
            break
    assert (width, height) == (64, 64)
    raw = zlib.decompress(compressed)
    assert len(raw) == 64 * 257
    result = []
    above = [0] * 256
    pos = 0
    for _ in range(64):
        filtering = raw[pos]
        row = list(raw[pos + 1:pos + 257])
        pos += 257
        for x in range(256):
            left = row[x - 4] if x >= 4 else 0
            up = above[x]
            upper_left = above[x - 4] if x >= 4 else 0
            if filtering == 0:
                prediction = 0
            elif filtering == 1:
                prediction = left
            elif filtering == 2:
                prediction = up
            elif filtering == 3:
                prediction = (left + up) // 2
            elif filtering == 4:
                p = left + up - upper_left
                errors = (abs(p - left), abs(p - up), abs(p - upper_left))
                prediction = (left, up, upper_left)[errors.index(min(errors))]
            else:
                raise AssertionError(f"Unsupported PNG filter {filtering}")
            row[x] = (row[x] + prediction) & 255
        result.extend(tuple(row[i:i + 4]) for i in range(0, 256, 4))
        above = row
    return result


def luminance(rgb):
    return rgb[0] * .2126 + rgb[1] * .7152 + rgb[2] * .0722

metadata = load(Path(".github/scripts/brewing_barrel_wood_palettes.json"))
assert not metadata["unmodified"], "All 14 textures now use distinct grading"
assert len(metadata["spriteGitBlobSHA"]) == 14
base_sprite = decode_rgba(Path(".github/scripts/fixtures/alcocraft_spruce_keg.png"))
assert len(base_sprite) == 4096
for wood in woods:
    id_ = "brewing_barrel" if wood == "oak" else wood + "_brewing_barrel"
    png_path = assets / "textures/block" / (id_ + ".png")
    data = png_path.read_bytes()
    sha = hashlib.sha1(f"blob {len(data)}\0".encode("ascii") + data).hexdigest()
    assert sha == metadata["spriteGitBlobSHA"][wood], \
           f"Unreviewed texture modification for {wood}"
    recolored = decode_rgba(png_path)
    assert len(recolored) == 4096
    wood_luminances = []
    metal_pixels = 0
    for source, result in zip(base_sprite, recolored):
        r, g, b, a = source
        assert a == result[3], f"Pixel transparency changed in {wood}"
        is_wood = a > 0 and r > g + 7 and g > b + 7
        if not is_wood:
            assert source == result, f"Metal/transparent details altered in {wood}"
            if a:
                metal_pixels += 1
        else:
            wood_luminances.append(luminance(result[:3]))
    assert len(wood_luminances) == 2492 and metal_pixels == 196
    wood_luminances.sort()
    lo = wood_luminances[int(len(wood_luminances) * .05)]
    hi = wood_luminances[int(len(wood_luminances) * .95)]
    contrast = hi - lo
    if wood != "mangrove":
        assert contrast >= metadata["expectedWoodContrastP05P95"][wood] - 3, \
               f"{wood} has lost its grain/wood depth: {contrast:.2f}"
    if wood in metadata["moderatedContrastP05P95Upper"]:
        assert contrast <= metadata["moderatedContrastP05P95Upper"][wood], \
               f"{wood}: contrast too harsh ({contrast:.2f})"
    if wood == "mangrove":
        woody = [px for src, px in zip(base_sprite, recolored)
                 if src[3] and src[0] > src[1] + 7 and src[1] > src[2] + 7]
        assert len(woody) == 2492
    if wood == "mangrove":
        # Sampled from Minecraft Java 26.3 mangrove_planks: the seven-color
        # source never exceeds RGB (139,77,58); any brighter barrel pixel
        # looks artificially orange/red against the vanilla wood.
        assert all(px[0] <= 139 and px[1] <= 77 and px[2] <= 58
                   for px in woody), "Mangrove barrel brighter than actual vanilla planks"
        assert all(px[0] > px[1] and px[1] >= px[2] - 5
                   for px in woody), "Mangrove palette lost red-brown undertones"
    if wood == "bamboo":
        # Bamboo preserves real vanilla stalk bands/joints, not only yellow RGB.
        assert len({tuple(px[:3]) for orig, px in zip(base_sprite, recolored)
                    if orig[3] and orig[0] > orig[1] + 7
                    and orig[1] > orig[2] + 7}) > 150, \
               "Bamboo texture lost stalk and knot variations"
# Specifically protect the top lid and bottom surface from becoming flat again.
# Minecraft's barrel model samples these rectangles of the 64x64 atlas.
def wood_surface_stats(pixels, bounds):
    x0, y0, x1, y1 = bounds
    rgb = []
    for yy in range(y0, y1):
        for xx in range(x0, x1):
            i = yy * 64 + xx
            src = base_sprite[i]
            if src[3] and src[0] > src[1] + 7 and src[1] > src[2] + 7:
                rgb.append(pixels[i][:3])
    values = sorted(luminance(pixel) for pixel in rgb)
    assert values, "No wood pixels in barrel surface region"
    return {
        "unique": len(set(rgb)),
        "p05_p95": values[int(.95 * len(values))] - values[int(.05 * len(values))]
    }

guards = metadata["regionShadeGuard"]
for wood in ("mangrove",):
    tex = decode_rgba(assets / "textures/block" / (wood + "_brewing_barrel.png"))
    top = wood_surface_stats(tex, metadata["surfaceDiagnosticRectangles"]["top"])
    bottom = wood_surface_stats(tex, metadata["surfaceDiagnosticRectangles"]["bottom"])
    if wood == "mangrove":
        assert top["unique"] >= guards[wood]["topUniqueMin"], "Mangrove lid lost the spruce-like shading"
        assert bottom["unique"] >= guards[wood]["bottomUniqueMin"], "Mangrove lower panel is flat"
        assert top["p05_p95"] >= guards[wood]["topP05P95Min"], "Mangrove top lacks depth"
        assert bottom["p05_p95"] >= guards[wood]["bottomP05P95Min"], "Mangrove underside lacks depth"
print("PASSED: top and lower panels retain distinct spruce-like wood grain on mangrove")

print("PASSED: depth, knots, RGBA, metal and distinct actual wood palettes")
print("PASSED: all 14 image SHA references match the new reviewed sprites")


# Regression guard: all newly registered fermentation fluids MUST have a
# client FluidModel and a stitched blocks-atlas sprite. A GUI sprite alone
# does not prevent magenta/black missing-liquid tiles in JEI.
client_code=(java/"ItaliansDelightClient.java").read_text(encoding="utf-8")
blocks_atlas=load(root/"assets/minecraft/atlases/blocks.json")
gui_atlas=load(root/"assets/minecraft/atlases/gui.json")
block_resources={x["resource"] for x in blocks_atlas["sources"]}
gui_sprites={x.get("sprite") for x in gui_atlas["sources"]}
new_fluids=("cherry_juice", "white_wine", "cherry_wine",
            "mead", "cider", "red_wine_vinegar")
for drink in new_fluids:
    assert f"registerContainedFluid(ModFluids.{drink.upper()}," in client_code, drink
    still=f"italiansdelight:block/fluids/booze/{drink}_still"
    assert still in block_resources, (drink,"blocks atlas")
    assert f"italiansdelight:fluids/{drink}_still" in gui_sprites, (drink,"gui atlas")
    png=assets/"textures/block/fluids/booze"/(drink+"_still.png")
    assert png.is_file(), drink
    assert struct.unpack(">II", png.read_bytes()[16:24])==(16,512), drink

# New 16x16 item art must have real linked sprites, not the placeholders that
# accidentally reused grape juice and wine buckets or vanilla potion tints.
changed_art=("white_wine_bucket", "white_wine_bottle",
             "cherry_juice_bucket", "cherry_wine_bucket", "cherry_wine_bottle")
for name in changed_art:
    tex=assets/"textures/item"/(name+".png")
    assert struct.unpack(">II", tex.read_bytes()[16:24])==(16,16), name
    model=load(assets/"models/item"/(name+".json"))
    assert model["textures"]["layer0"]=="italiansdelight:item/"+name, name
    item=load(assets/"items"/(name+".json"))
    assert item["model"]["model"]=="italiansdelight:item/"+name, name
# Cherry juice intentionally uses the standard Minecraft potion bottle
# instead of its obsolete custom cherry-juice sprite.
juice_bottle_model=load(assets/"models/item/cherry_juice_bottle.json")
assert juice_bottle_model["parent"]=="minecraft:item/potion"
assert load(assets/"items/cherry_juice_bottle.json")["model"]["model"]=="italiansdelight:item/cherry_juice_bottle"
assert (assets/"textures/item/white_wine_bucket.png").read_bytes() != (
    assets/"textures/item/white_grape_juice_bucket.png").read_bytes()
for lang in (lang_en,lang_it):
    for name in new_fluids:
        for suffix in ("bottle","bucket"):
            label=lang["item.italiansdelight."+name+"_"+suffix]
            assert label and "(" not in label and ")" not in label, (name,suffix,label)
print("PASSED: six JEI fluid renderers, stitched atlases, paired item textures and clean locales")
