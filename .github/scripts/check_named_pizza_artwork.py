#!/usr/bin/env python3
"""Sanity checks for the *first* named whole-pizza item-model artwork batch.

This deliberately does not require the remaining 14 active named pizza icons:
those are deferred until the first four are tested by the mod's author.
No external Python libraries are needed in GitHub Actions.
"""
from __future__ import annotations

import json
import struct
import zlib
from pathlib import Path

ASSETS = Path("src/main/resources/assets/italiansdelight")
IMPLEMENTED = (
    "pizza_bianca",
    "pizza_rossa",
    "pizza_margherita",  # existing author texture, must not be overwritten
    "pizza_patate_speck",
    "pizza_custom",       # fallback for the other 14 flavors and mixed pizzas
)
NEW_TEXTURES = set(IMPLEMENTED) - {"pizza_margherita"}
PREFIX = b"\x89PNG\r\n\x1a\n"


def inspect_png(path: Path, require_binary_alpha: bool) -> None:
    content = path.read_bytes()
    if not content.startswith(PREFIX):
        raise ValueError(f"{path} is not a PNG")
    pos = len(PREFIX)
    chunks: list[bytes] = []
    seen_end = False
    while pos + 12 <= len(content):
        (size,) = struct.unpack_from(">I", content, pos)
        kind = content[pos + 4:pos + 8]
        payload = content[pos + 8:pos + 8 + size]
        checksum = struct.unpack_from(">I", content, pos + 8 + size)[0]
        if len(payload) != size or zlib.crc32(kind + payload) != checksum:
            raise ValueError(f"{path}: broken PNG chunk or CRC")
        pos += size + 12
        if kind == b"IHDR":
            width, height, depth, color, compression, filtering, interlace = struct.unpack(
                ">IIBBBBB", payload
            )
            if (width, height, depth, color, compression, filtering, interlace) != (
                16, 16, 8, 6, 0, 0, 0
            ):
                raise ValueError(f"{path}: expected 16x16 8-bit noninterlaced RGBA")
        elif kind == b"IDAT":
            chunks.append(payload)
        elif kind == b"IEND":
            seen_end = True
            break
    if not seen_end or pos != len(content):
        raise ValueError(f"{path}: truncated PNG")
    if not require_binary_alpha:
        return  # Existing approved artwork is deliberately not reprocessed.

    raw = zlib.decompress(b"".join(chunks))
    if len(raw) != 16 * (1 + 16 * 4):
        raise ValueError(f"{path}: invalid pixel length")
    visible = 0
    for y in range(16):
        if raw[y * 65] != 0:
            raise ValueError(f"{path}: newly generated images must use PNG filter 0")
        for x in range(16):
            alpha = raw[y * 65 + 1 + x * 4 + 3]
            if alpha not in (0, 255):
                raise ValueError(f"{path}: semitransparent pixel {x},{y}")
            visible += int(alpha == 255)
    if visible == 0:
        raise ValueError(f"{path}: transparent blank texture")


def main() -> None:
    for key in IMPLEMENTED:
        model_path = ASSETS / "models/item" / (key + ".json")
        item_path = ASSETS / "items" / (key + ".json")
        png_path = ASSETS / "textures/item" / (key + ".png")
        model = json.loads(model_path.read_text(encoding="utf-8"))
        item = json.loads(item_path.read_text(encoding="utf-8"))
        if model != {
            "parent": "minecraft:item/generated",
            "textures": {"layer0": "italiansdelight:item/" + key},
        }:
            raise ValueError(f"{model_path}: wrong layer0 or parent")
        if item != {
            "model": {
                "type": "minecraft:model",
                "model": "italiansdelight:item/" + key,
            },
        }:
            raise ValueError(f"{item_path}: wrong client item definition")
        inspect_png(png_path, key in NEW_TEXTURES)
        print(f"OK {key}: definition, model, 16x16 RGBA texture")

    default = json.loads(
        (ASSETS / "models/item/cooked_pizza.json").read_text(encoding="utf-8")
    )
    if default["textures"]["layer0"] != "italiansdelight:item/pizza_custom":
        raise ValueError("Default cooked pizza still misleadingly depicts Margherita")

    source = Path(
        "src/main/java/dev/italiansdelight/common/pizza/PizzaDisplayNames.java"
    ).read_text(encoding="utf-8")
    for key in ("bianca", "rossa", "margherita", "patate_speck"):
        if f'case "{key}" -> ModRegistries.id("pizza_{key}")' not in source:
            raise ValueError(f"No model-selection branch for {key}")
    if "stack.set(DataComponents.ITEM_MODEL, wholeItemModel(data));" not in source:
        raise ValueError("Baked pizza model is not assigned to its ItemStack")
    print("OK: 4 recognized cooked models, neutral fallback and data-driven selection")


if __name__ == "__main__":
    main()
