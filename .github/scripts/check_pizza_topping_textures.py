#!/usr/bin/env python3
"""Validate pixel-perfect Italian's Delight pizza topping art (stdlib only).

Every topping surface PNG must be exactly 12x12 RGBA and every pixel
must have alpha 0 or 255. Fails CI for missing, malformed or softened art.
Also verifies the PNG stream and that expected native/legacy textures exist.
"""
from __future__ import annotations

import struct
import zlib
from pathlib import Path

ROOT = Path("src/main/resources/assets/italiansdelight/textures/block/pizza_toppings")
EXPECTED = {
    "tomato_sauce", "cream_bowl", "ricotta",
    "mozzarella_slice", "bocconcino", "grated_parmigiano_reggiano",
    "grated_pecorino_romano", "gorgonzola_wedge", "provolone_slice",
    "scamorza_slice", "smoked_scamorza_slice", "prosciutto_crudo_slice",
    "salame_slice", "mortadella_slice", "pancetta_slice", "guanciale_slice",
    "bresaola_slice", "coppa_slice", "speck_slice", "tomato_slice",
    "basil", "bacon", "chicken_cuts", "cod_slice", "salmon_slice",
    "fried_egg", "minced_beef", "cabbage_leaf", "pumpkin_slice",
    "carrot_slice", "potato_slice", "legacy_mozzarella", "unknown",
}
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"


def inspect(path: Path) -> tuple[int, int]:
    data = path.read_bytes()
    if not data.startswith(PNG_SIGNATURE):
        raise ValueError(f"{path}: invalid PNG signature")
    cursor = 8
    payload = bytearray()
    seen_ihdr = False
    seen_end = False
    while cursor + 12 <= len(data):
        size = struct.unpack_from(">I", data, cursor)[0]
        cursor += 4
        kind = data[cursor:cursor + 4]
        cursor += 4
        body = data[cursor:cursor + size]
        cursor += size
        crc = struct.unpack_from(">I", data, cursor)[0]
        cursor += 4
        if len(body) != size or zlib.crc32(kind + body) != crc:
            raise ValueError(f"{path}: invalid chunk {kind!r}")
        if kind == b"IHDR":
            width, height, depth, color, compression, filtering, interlace = struct.unpack(
                ">IIBBBBB", body
            )
            if (width, height, depth, color, compression, filtering, interlace) != (
                12, 12, 8, 6, 0, 0, 0
            ):
                raise ValueError(f"{path}: expected 12x12 8-bit RGBA noninterlaced")
            seen_ihdr = True
        elif kind == b"IDAT":
            payload.extend(body)
        elif kind == b"IEND":
            seen_end = True
            break
    if not (seen_ihdr and seen_end and cursor == len(data)):
        raise ValueError(f"{path}: truncated or trailing PNG data")
    decoded = zlib.decompress(payload)
    if len(decoded) != 12 * (1 + 12 * 4):
        raise ValueError(f"{path}: invalid pixel data length")
    visible = 0
    for row in range(12):
        offset = row * 49
        if decoded[offset] != 0:
            raise ValueError(f"{path}: only unfiltered pixel rows supported")
        for pixel in range(12):
            alpha = decoded[offset + 1 + pixel * 4 + 3]
            if alpha not in (0, 255):
                raise ValueError(f"{path}: non-binary alpha at {pixel},{row}")
            visible += (alpha == 255)
    if visible == 0:
        raise ValueError(f"{path}: invisible/empty texture")
    return visible, 144 - visible


def main() -> None:
    found = {p.stem for p in ROOT.glob("*.png")}
    if found != EXPECTED:
        raise ValueError(f"Missing textures: {sorted(EXPECTED - found)}; "
                         f"unexpected textures: {sorted(found - EXPECTED)}")
    for name in sorted(EXPECTED):
        filled, empty = inspect(ROOT / (name + ".png"))
        print(f"OK {name}.png: 12x12 RGBA, opaque {filled}, transparent {empty}")
    print(f"Validated all {len(EXPECTED)} unique pizza topping textures")


if __name__ == "__main__":
    main()
