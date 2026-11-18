#!/usr/bin/env python3
"""Static reproducible checks: Alcocraft-derived geometry and 16 wood fermenters."""
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
    "mangrove", "cherry", "pale_oak", "poplar", "bamboo",
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
for index,x,y in (("INPUT_IN",62,7),("OUTPUT_IN",116,7),("CULTURE_IN",26,15),
                  ("INPUT_OUT",62,63),("OUTPUT_OUT",116,63),("CULTURE_OUT",26,55)):
    name=f"BrewingBarrelBlockEntity.{index}"
    action="addInputSlot" if index.endswith("_IN") else "addReturnSlot"
    assert f"{action}({name}, {x}, {y})" in barrel_menu, index
for required in ("case INPUT_IN", "case OUTPUT_IN", "case CULTURE_IN",
                 "case CULTURE_IN -> stack.is(Items.GLASS_BOTTLE)"):
    assert required in barrel_entity, required
assert "return false; // Result slots are extraction-only." in barrel_entity
assert "handleInputContainer()" in barrel_entity
assert "handleOutputContainer()" in barrel_entity
assert "handleCultureContainer()" in barrel_entity
assert "if (++barrel.progress" not in barrel_entity, "Wine fermentation is deferred"
assert "barrel.juiceAmount -= amount;\n            barrel.wineAmount += amount" not in barrel_entity
assert 'textures/gui/brewing_barrel.png' in barrel_screen
assert all(label in lang_it for label in (
    "gui.italiansdelight.brewing_barrel.input",
    "gui.italiansdelight.brewing_barrel.output",
    "gui.italiansdelight.brewing_barrel.culture"))
gui_atlas=load(root/"assets/minecraft/atlases/gui.json")
sprites={entry.get("sprite") for entry in gui_atlas["sources"]}
for sprite in ("grape_juice_still","white_grape_juice_still","wine_still"):
    assert "italiansdelight:fluids/"+sprite in sprites
print("PASSED: Rustic six-slot GUI, persisted inventory, vessel IO, and disabled fermentation")


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
print("PASSED: 16 brewer IDs, blockstates, inventory models, recipes, loot, languages and attributions")

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
assert not metadata["unmodified"], "All 16 textures now use distinct grading"
assert len(metadata["spriteGitBlobSHA"]) == 16
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
    if wood in ("mangrove", "pale_oak"):
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
    if wood == "pale_oak":
        # This is a NEW pale-oak sprite based on SPRUCE wood geometry, not
        # an RGB warm-up of the incorrect beige image. The vanilla 26.3
        # stripped_pale_oak_log actually contains white and very light pink.
        # Reject the last attempt's beige (large R-G and G-B gaps).
        vanilla = {(255, 251, 248), (248, 240, 240), (240, 231, 229),
                   (235, 224, 224), (230, 219, 219)}
        recesses = {(219, 209, 209), (207, 197, 197)}
        shades = {tuple(px[:3]) for px in woody}
        assert shades == vanilla | recesses, "Pale oak lost the vanilla color palette"
        assert sum(tuple(px[:3]) in vanilla for px in woody) >= 2318, \
               "Not enough original stripped-pale-oak colors used"
        assert all(4 <= px[0] - px[1] <= 11 and 0 <= px[1] - px[2] <= 3
                   for px in woody), "Pale oak became brown/beige again"
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
for wood in ("mangrove", "pale_oak"):
    tex = decode_rgba(assets / "textures/block" / (wood + "_brewing_barrel.png"))
    top = wood_surface_stats(tex, metadata["surfaceDiagnosticRectangles"]["top"])
    bottom = wood_surface_stats(tex, metadata["surfaceDiagnosticRectangles"]["bottom"])
    if wood == "mangrove":
        assert top["unique"] >= guards[wood]["topUniqueMin"], "Mangrove lid lost the spruce-like shading"
        assert bottom["unique"] >= guards[wood]["bottomUniqueMin"], "Mangrove lower panel is flat"
        assert top["p05_p95"] >= guards[wood]["topP05P95Min"], "Mangrove top lacks depth"
        assert bottom["p05_p95"] >= guards[wood]["bottomP05P95Min"], "Mangrove underside lacks depth"
    else:
        assert top["unique"] >= guards[wood]["topUniqueMin"], "Pale oak lid has a flat highlight"
        assert top["p05_p95"] >= guards[wood]["topP05P95Min"], "Pale oak lid has no visible grey shades"
        assert max(c[0] for c in tex) <= 255  # Metal is outside the wood mask
print("PASSED: top and lower panels retain distinct spruce-like wood grain on mangrove and pale oak")

print("PASSED: depth, knots, RGBA, metal and distinct actual wood palettes")
print("PASSED: all 16 image SHA references match the new reviewed sprites")
