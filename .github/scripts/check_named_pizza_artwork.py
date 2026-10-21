#!/usr/bin/env python3
"""Validate the 18 exact named raw/cooked pizza models and approved originals.

No third-party dependencies. The PNG decoder handles standard row filters,
so both existing approved images and new 16x16 pixel art can be checked.
"""
from __future__ import annotations

import hashlib
import json
import re
import struct
import zlib
from pathlib import Path

ASSETS = Path("src/main/resources/assets/italiansdelight")
JAVA = Path("src/main/java/dev/italiansdelight/common/pizza/PizzaDisplayNames.java")
CREATIVE = Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java")
SAMPLES = Path("src/main/java/dev/italiansdelight/common/pizza/PizzaCreativeSamples.java")
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
# Exact crust pixels sampled from the 2 approved 16x16 Margherita images.
# Protect RGB *and* alpha, not merely silhouette. A previous batch preserved
# alpha but repainted the entire crust, resulting in noisy edges.
PROTECTED_CRUST = {
    "raw": {
        bytes.fromhex(hex_rgb + "ff")
        for hex_rgb in ("a6784d", "e0b777", "eccd95", "bb925f")
    },
    "cooked": {
        bytes.fromhex(hex_rgb + "ff")
        for hex_rgb in ("5c3018", "cd8338", "eaa64c", "914e23")
    },
}
EXPECTED_PROTECTED_PIXELS = 62

ORIGINAL_HASHES = {
    "raw_pizza": "38fdfd76ed7c30c287cafb5fee7461c6bdeb0399",
    "pizza_margherita": "141f894f5776bc71f980657d3b1ed457072c2b39",
}


def png_rgba(path: Path) -> bytes:
    png = path.read_bytes()
    if not png.startswith(PNG_SIGNATURE):
        raise ValueError(f"Not a PNG: {path}")
    offset, packed, seen_end, valid_header = 8, [], False, False
    while offset + 12 <= len(png):
        length = struct.unpack_from(">I", png, offset)[0]
        tag = png[offset + 4:offset + 8]
        chunk = png[offset + 8:offset + 8 + length]
        checksum = struct.unpack_from(">I", png, offset + 8 + length)[0]
        if len(chunk) != length or zlib.crc32(tag + chunk) != checksum:
            raise ValueError(f"Broken PNG chunk/CRC: {path}")
        offset += length + 12
        if tag == b"IHDR":
            fields = struct.unpack(">IIBBBBB", chunk)
            if fields != (16, 16, 8, 6, 0, 0, 0):
                raise ValueError(f"Expected 16x16 RGBA PNG: {path}")
            valid_header = True
        elif tag == b"IDAT":
            packed.append(chunk)
        elif tag == b"IEND":
            seen_end = True
            break
    if not valid_header or not seen_end or offset != len(png):
        raise ValueError(f"Incomplete PNG: {path}")

    raw = zlib.decompress(b"".join(packed))
    if len(raw) != 16 * 65:
        raise ValueError(f"Invalid decoded RGBA length: {path}")
    rgba, previous = bytearray(), bytearray(64)
    for y in range(16):
        filt = raw[y * 65]
        scan = bytearray(raw[y * 65 + 1:y * 65 + 65])
        for i in range(64):
            left = scan[i - 4] if i >= 4 else 0
            above = previous[i]
            upper_left = previous[i - 4] if i >= 4 else 0
            if filt == 1:
                predictor = left
            elif filt == 2:
                predictor = above
            elif filt == 3:
                predictor = (left + above) // 2
            elif filt == 4:
                p = left + above - upper_left
                distances = (abs(p - left), abs(p - above), abs(p - upper_left))
                predictor = (left, above, upper_left)[distances.index(min(distances))]
            elif filt == 0:
                predictor = 0
            else:
                raise ValueError(f"Unsupported PNG filter {filt}: {path}")
            scan[i] = (scan[i] + predictor) & 255
        rgba.extend(scan)
        previous = scan
    alphas = rgba[3::4]
    if not alphas or set(alphas) != {0, 255}:
        raise ValueError(f"Empty image or semi-transparent pixel: {path}")
    return bytes(rgba)


def original_sha(path: Path) -> str:
    binary = path.read_bytes()
    return hashlib.sha1(b"blob " + str(len(binary)).encode() + b"\0" + binary).hexdigest()


def assert_model(name: str, expected_texture: str) -> None:
    model_path = ASSETS / "models/item" / (name + ".json")
    definition_path = ASSETS / "items" / (name + ".json")
    model = json.loads(model_path.read_text(encoding="utf-8"))
    definition = json.loads(definition_path.read_text(encoding="utf-8"))
    if model != {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": "italiansdelight:item/" + expected_texture},
    }:
        raise ValueError(f"Unexpected model parent/layer: {model_path}")
    if definition != {
        "model": {"type": "minecraft:model", "model": "italiansdelight:item/" + name}
    }:
        raise ValueError(f"Unexpected item definition: {definition_path}")


def main() -> None:
    src = JAVA.read_text(encoding="utf-8")
    # The stable internal translation key must resolve to the approved user-facing name.
    # Do not rename the key: naming logic and existing data must stay unchanged.
    if 'Component.translatable(PREFIX + "assorted")' not in src:
        raise ValueError("Mixed pizza name must still use the stable translation key")
    if '("margherita".equals(id) || (id == null && isMixed(whole)))' not in src:
        raise ValueError("Mixed pizzas must share Margherita raw/cooked artwork")
    if 'return isMixed(whole) ? null : PRESETS.get(quarter(whole, 0));' not in src:
        raise ValueError("Uniform unknown pizzas must still use fallback artwork")
    for locale, expected in (("it_it", "Pizza mista"), ("en_us", "Mixed Pizza")):
        names = json.loads((ASSETS / "lang" / (locale + ".json")).read_text(encoding="utf-8"))
        if names.get("pizza.italiansdelight.assorted") != expected:
            raise ValueError(f"{locale}: mixed pizza name must be {expected!r}")
    presets = re.findall(r'add\(presets,\s*"([^"]+)"', src)
    if len(presets) != 18 or len(set(presets)) != 18:
        raise ValueError(f"Expected 18 distinct active pizza presets, got {presets}")
    if "principessa" in presets:
        raise ValueError("Suspended Principessa must not be enabled")
    if 'return ModRegistries.id(base + "_" + (id == null ? "custom" : id))' not in src:
        raise ValueError("Raw/cooked saved-component model selection is missing")
    if "stack.set(DataComponents.ITEM_MODEL," not in src:
        raise ValueError("Whole pizza model is not stored in the ItemStack")

    for name, sha in ORIGINAL_HASHES.items():
        path = ASSETS / "textures/item" / (name + ".png")
        if original_sha(path) != sha:
            raise ValueError(f"APPROVED original texture changed: {path}")
    raw_ref = png_rgba(ASSETS / "textures/item/raw_pizza.png")
    cooked_ref = png_rgba(ASSETS / "textures/item/pizza_margherita.png")

    for type_, ref in (("raw", raw_ref), ("cooked", cooked_ref)):
        # Every recognized taste must have an independent item sprite.
        # A generic fallback must not accidentally look exactly like Bianca,
        # and no two named tastes may share the same pixel data.
        appearance_to_name = {"margherita": ref}
        # Audit the *exact RGBA colors* of all approved crust pixels, too.
        # These must be left byte-for-byte unchanged when recoloring toppings.
        border = {
            index: ref[index:index + 4]
            for index in range(0, len(ref), 4)
            if ref[index:index + 4] in PROTECTED_CRUST[type_]
        }
        if len(border) != EXPECTED_PROTECTED_PIXELS:
            raise ValueError(
                f"Reference {type_} crust palette changed; expected "
                f"{EXPECTED_PROTECTED_PIXELS} protected pixels, found {len(border)}"
            )

        # The generic item icons in Creative must remain the original artwork.
        base = "raw_pizza" if type_ == "raw" else "cooked_pizza"
        expected = "raw_pizza" if type_ == "raw" else "pizza_margherita"
        assert_model(base, expected)

        for preset in presets + ["custom"]:
            if preset == "margherita":
                # Vanilla base item model is exactly the author's Margherita.
                continue
            name = f"{type_}_pizza_{preset}"
            assert_model(name, name)
            pixels = png_rgba(ASSETS / "textures/item" / (name + ".png"))
            if pixels[3::4] != ref[3::4]:
                raise ValueError(f"Incorrect silhouette/proportions: {name}")
            changed_crust = [
                (index // 4 % 16, index // 64)
                for index, rgba in border.items()
                if pixels[index:index + 4] != rgba
            ]
            if changed_crust:
                raise ValueError(
                    f"{name} repaints {len(changed_crust)} original crust pixels: "
                    f"{changed_crust[:8]}; only edit the TOPPINGS"
                )
            if pixels == ref:
                raise ValueError(f"{name} must be a real variation, not a duplicate Margherita")
            duplicate_name = next(
                (previous for previous, rgba in appearance_to_name.items()
                 if rgba == pixels), None
            )
            if duplicate_name:
                raise ValueError(
                    f"{name} reuses the exact artwork of {duplicate_name}; "
                    "each pizza must be visually unique"
                )
            appearance_to_name[preset] = pixels

    creative = CREATIVE.read_text(encoding="utf-8")
    if "PizzaDisplayNames.allPresets().entrySet()" not in creative:
        raise ValueError("Named pizzas do not appear as 18 real Creative sample pairs")
    if creative.count("PizzaCreativeSamples.create(preset.getKey(),") != 2:
        raise ValueError("Creative requires one raw and one cooked sample per taste")
    samples = SAMPLES.read_text(encoding="utf-8")
    for marker in ("PizzaToppingApplication(", "PIZZA_CONTENTS", "nameWholeStack(stack, data)"):
        if marker not in samples:
            raise ValueError(f"Creative samples lack real persistent composition: {marker}")

    print("OK: 18 active named pizzas x 2 perspectives (raw/cooked),"
          " 36 new PNGs incl. 2 neutral fallback textures, all 16x16 RGBA")
    print("OK: original Margherita assets unchanged, silhouettes and 62 crust RGB pixels preserved in every variant")
    print("OK: distinct per-preset raw/cooked pixel art, no duplicate named/fallback sprites")
    print("OK: all models, item definitions and real Creative variants registered")
    print("OK: mixed-pizza inventory name translated as Pizza mista / Mixed Pizza")


if __name__ == "__main__":
    main()
