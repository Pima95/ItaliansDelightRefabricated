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
import struct
import zlib
from pathlib import Path

ASSETS = Path("src/main/resources/assets/italiansdelight")
SRC = Path("src/main/java/dev/italiansdelight/common/pizza")
GROUP = Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java")
ORIGINAL_GIT_SHA = "7dd166bec3bd3ddc7473d498ce0e78b3e785bc06"
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
    if len(named) != 18 or len(set(named)) != 18 or "principessa" in named:
        raise ValueError("Expected 18 canonical presets, without Principessa")
    expected = [id for id in named if id != "margherita"] + ["custom"]
    hashes = {original}
    for taste in expected:
        item = "pizza_slice_" + taste
        model(item, item)
        png_path = ASSETS / "textures/item" / (item + ".png")
        texture = rgba_png(png_path)
        for y in range(16):
            for x in range(16):
                pos = (y * 16 + x) * 4
                if (x, y) not in SURFACE:
                    if texture[pos:pos + 4] != original[pos:pos + 4]:
                        raise ValueError(
                            f"{item}: approved crust pixel changed at {x},{y}")
        if texture in hashes:
            raise ValueError(f"Duplicate named slice artwork: {item}")
        hashes.add(texture)
    if len(hashes) != 19:
        raise ValueError("Expected 18 named variants and a neutral fallback")

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

    print(f"OK: {len(expected)} unique 16x16 RGBA slices plus original Margherita")
    print("OK: 218 pixels (crust/background) immutable in every variant")
    print("OK: each quarter selects its own preset; examples are edible stacks")


if __name__ == "__main__":
    main()
