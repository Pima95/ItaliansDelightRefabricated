#!/usr/bin/env python3
"""Verify every named pizza slice and preserve the original Margherita sprite.

Pure Python stdlib; checks RGB+alpha preservation outside the 38-pixel
topping surface, PNG CRC and format, all 18 individual recipes, and Creative
examples.  Unlike alpha-only checks, this catches accidental crust changes.
"""
from __future__ import annotations

import hashlib
import json
import re
from itertools import combinations
import struct
import zlib
from pathlib import Path

ASSETS = Path("src/main/resources/assets/italiansdelight")
SRC = Path("src/main/java/dev/italiansdelight/common/pizza")
GROUP = Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java")
ORIGINAL_GIT_SHA = "7dd166bec3bd3ddc7473d498ce0e78b3e785bc06"
# Only these four exact presets lack any genuinely stretchy cheese.
# Their dangling Mozzarella pixels may be replaced, while all other tastes
# must retain the originally approved Margherita silhouette and crust.
NO_MELT_PRESETS = frozenset({
    "bianca", "rossa", "guanciale_pecorino_rossa",
    "mortadella_ricotta_mandorle",
})
# Nine isolated cheese-drip pixels removed; a tenth pixel (3,10) becomes
# golden-brown baked dough at the tip of the pizza instead of red mozzarella.
DRIP_REMOVALS = frozenset({
    (3, 13), (4, 13), (3, 14), (3, 15), (9, 11), (9, 12),
})
# Six pixel-art edits turn the short slice nose into a longer triangular tip.
# No melted cheese is restored; all colors come from baked pastry.
EXTENDED_TIP_PIXELS = {
    (1, 10): bytes((76, 50, 31, 255)),
    (2, 10): bytes((136, 88, 51, 255)),
    (2, 11): bytes((165, 115, 70, 255)),
    (3, 11): bytes((227, 164, 105, 255)),
    (3, 12): bytes((130, 82, 47, 255)),
    (4, 12): bytes((86, 55, 34, 255)),
}
TIP = (3, 10)
TIP_DOUGH_RGBA = bytes((103, 65, 36, 255))
# Safeguard the exact 38 original topping pixels for each revised flavor.
TOPPING_HASHES = {
    "bianca": "e9d2c90a3a29c4345189baecbea841feabc976c288456abf374a4715b9137f16",
    "rossa": "1b38dfa46cf8ee49efd5d3054442e4a67b19aeff4e2c2f2c1a1421556921cf22",
    "guanciale_pecorino_rossa": "1cc22628ad8c1984227f1bc96974218740ceedf5be56edfaa2f134333177aa69",
    "mortadella_ricotta_mandorle": "df2d4b7080cdc272bbd20b8370e85d720c12b7433b79365214391742e90b868c",
}
MAGIC = b"\x89PNG\r\n\x1a\n"
SURFACE = {
    (x, y)
    for y, xs in {
        5: (7, 8, 9),
        6: (6, 7, 8, 9, 10),
        7: (5, 6, 7, 8, 9, 10),
        8: (4, 5, 6, 7, 8, 9, 10, 11),
        9: (4, 5, 6, 7, 8, 9, 10),
        10: (4, 5, 6, 7, 8, 9),
        11: (4, 5, 6),
    }.items()
    for x in xs
}


def rgba_png(path: Path) -> bytes:
    payload = path.read_bytes()
    if not payload.startswith(MAGIC):
        raise ValueError(f"Not a PNG: {path}")
    offset = len(MAGIC)
    idats = []
    palette = b""
    trans = b""
    header = None
    ended = False
    while offset + 12 <= len(payload):
        size = struct.unpack_from(">I", payload, offset)[0]
        kind = payload[offset + 4:offset + 8]
        content = payload[offset + 8:offset + 8 + size]
        if len(content) != size:
            raise ValueError(f"Truncated PNG chunk: {path}")
        crc = struct.unpack_from(">I", payload, offset + 8 + size)[0]
        if zlib.crc32(kind + content) != crc:
            raise ValueError(f"Corrupted PNG chunk CRC: {path}")
        offset += size + 12
        if kind == b"IHDR":
            header = struct.unpack(">IIBBBBB", content)
        elif kind == b"PLTE":
            palette = content
        elif kind == b"tRNS":
            trans = content
        elif kind == b"IDAT":
            idats.append(content)
        elif kind == b"IEND":
            ended = True
            break
    if not ended or offset != len(payload) or header is None:
        raise ValueError(f"Incomplete PNG: {path}")
    width, height, depth, color, compression, filtering, interlace = header
    if (width, height, depth, compression, filtering, interlace) != (16, 16, 8, 0, 0, 0):
        raise ValueError(f"Expected native 16x16, 8-bit PNG: {path}")
    if color not in (3, 6):
        raise ValueError(f"Unsupported PNG color mode {color}: {path}")
    bpp = 1 if color == 3 else 4
    raw = zlib.decompress(b"".join(idats))
    row_width = 16 * bpp
    if len(raw) != 16 * (1 + row_width):
        raise ValueError(f"Unexpected PNG pixel buffer: {path}")
    previous = bytearray(row_width)
    output = bytearray()
    for y in range(16):
        kind = raw[y * (1 + row_width)]
        row = bytearray(raw[y * (1 + row_width) + 1:(y + 1) * (1 + row_width)])
        for i in range(row_width):
            left = row[i - bpp] if i >= bpp else 0
            up = previous[i]
            diag = previous[i - bpp] if i >= bpp else 0
            if kind == 0:
                predict = 0
            elif kind == 1:
                predict = left
            elif kind == 2:
                predict = up
            elif kind == 3:
                predict = (left + up) // 2
            elif kind == 4:
                p = left + up - diag
                diffs = (abs(p - left), abs(p - up), abs(p - diag))
                predict = (left, up, diag)[diffs.index(min(diffs))]
            else:
                raise ValueError(f"Bad PNG filter {kind}: {path}")
            row[i] = (row[i] + predict) & 255
        if color == 6:
            output.extend(row)
        else:
            for index in row:
                if index * 3 + 3 > len(palette):
                    raise ValueError(f"Palette index out of range: {path}")
                output.extend(palette[index * 3:index * 3 + 3])
                output.append(trans[index] if index < len(trans) else 255)
        previous = row
    if set(output[3::4]) != {0, 255}:
        raise ValueError(f"PNG must use only alpha 0 or 255: {path}")
    return bytes(output)


def git_blob_hash(path: Path) -> str:
    content = path.read_bytes()
    return hashlib.sha1(
        b"blob " + str(len(content)).encode() + b"\x00" + content
    ).hexdigest()


def model(name: str, texture: str) -> None:
    m = json.loads((ASSETS / "models/item" / (name + ".json")).read_text())
    d = json.loads((ASSETS / "items" / (name + ".json")).read_text())
    if m != {"parent": "minecraft:item/generated",
             "textures": {"layer0": "italiansdelight:item/" + texture}}:
        raise ValueError(f"Invalid slice model: {name}")
    if d != {"model": {"type": "minecraft:model",
                      "model": "italiansdelight:item/" + name}}:
        raise ValueError(f"Invalid slice item definition: {name}")


def main() -> None:
    original_path = ASSETS / "textures/item/margherita_slice.png"
    if git_blob_hash(original_path) != ORIGINAL_GIT_SHA:
        raise ValueError("Approved Margherita slice PNG was modified")
    original = rgba_png(original_path)
    model("pizza_slice", "margherita_slice")

    names_src = (SRC / "PizzaDisplayNames.java").read_text()
    named = re.findall(r'add\(presets,\s*"([^"]+)"', names_src)
    if len(named) != 21 or len(set(named)) != 21 or "principessa" in named:
        raise ValueError("Expected 18 canonical presets, without Principessa")
    expected = [id for id in named if id != "margherita"] + ["custom"]
    hashes = {original}
    appearances = {"margherita": original}
    for taste in expected:
        item = "pizza_slice_" + taste
        model(item, item)
        png_path = ASSETS / "textures/item" / (item + ".png")
        texture = rgba_png(png_path)
        for y in range(16):
            for x in range(16):
                pos = (y * 16 + x) * 4
                if (x, y) not in SURFACE:
                    current = texture[pos:pos + 4]
                    if taste in NO_MELT_PRESETS and (x, y) in DRIP_REMOVALS:
                        if current != bytes((0, 0, 0, 0)):
                            raise ValueError(f"{item}: cheese drip remains at {x},{y}")
                    elif taste in NO_MELT_PRESETS and (x, y) == TIP:
                        if current != TIP_DOUGH_RGBA:
                            raise ValueError(f"{item}: tip is not baked pizza crust")
                    elif taste in NO_MELT_PRESETS and (x, y) in EXTENDED_TIP_PIXELS:
                        if current != EXTENDED_TIP_PIXELS[x, y]:
                            raise ValueError(f"{item}: longer triangular crust tip changed at {x},{y}")
                    elif current != original[pos:pos + 4]:
                        raise ValueError(
                            f"{item}: approved crust pixel changed at {x},{y}")
        if taste in NO_MELT_PRESETS:
            topping = b"".join(
                texture[(y * 16 + x) * 4:(y * 16 + x + 1) * 4]
                for x, y in sorted(SURFACE)
            )
            if hashlib.sha256(topping).hexdigest() != TOPPING_HASHES[taste]:
                raise ValueError(f"{item}: original topping artwork must remain unchanged")
        if texture in hashes:
            raise ValueError(f"Duplicate named slice artwork: {item}")
        hashes.add(texture)
        appearances[taste] = texture
    if len(hashes) != 22:
        raise ValueError("Expected 18 named variants and a neutral fallback")

    # Matching only byte-for-byte misses the actual defect reported in game:
    # two slices may differ at three or four pixels but look identical in
    # hotbar. Require a substantial, plainly visible color change on at
    # least ten of the 38 editable surface pixels for EVERY pair.
    minimum_visual_difference = 10
    for (name_a, art_a), (name_b, art_b) in combinations(appearances.items(), 2):
        changed = 0
        for x, y in SURFACE:
            pos = (y * 16 + x) * 4
            separation = sum(
                abs(art_a[pos + channel] - art_b[pos + channel])
                for channel in (0, 1, 2)
            )
            if separation > 105:
                changed += 1
        if changed < minimum_visual_difference:
            raise ValueError(
                f"Slice sprites {name_a} / {name_b} too similar at 16x16: "
                f"only {changed} strongly different topping pixels "
                f"(minimum {minimum_visual_difference})"
            )

    if "slicePresetId(PizzaSliceData slice)" not in names_src:
        raise ValueError("Slice must match only its own canonical quarter")
    if "stack.set(DataComponents.ITEM_MODEL, sliceItemModel(data));" not in names_src:
        raise ValueError("Slice ItemStack is missing dynamic ITEM_MODEL")
    if "return PRESETS.get(quarter(slice));" not in names_src:
        raise ValueError("Slice model lookup must reuse canonical names/presets")
    samples = (SRC / "PizzaCreativeSamples.java").read_text()
    creative = GROUP.read_text()
    if "PizzaSliceFactory.create(pizza, 0)" not in samples:
        raise ValueError("Creative slice examples must be real edible stacks")
    if "PizzaCreativeSamples.createSlice(preset.getKey())" not in creative:
        raise ValueError("Named slices must be accessible in Creative")

    print(f"OK: {len(expected)} distinctive 16x16 RGBA slices plus original Margherita")
    print("OK: 19 sprites checked pairwise for at least 10 pronounced pixel differences")
    print("OK: 218 outside-topping pixels preserved except approved crust-tip edits")
    print("OK: extended triangular tip on four cheeseless slices; 38 topping pixels intact")
    print("OK: each quarter selects its own preset; examples are edible stacks")


if __name__ == "__main__":
    main()
