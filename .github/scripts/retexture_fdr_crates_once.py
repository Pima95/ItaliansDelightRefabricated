#!/usr/bin/env python3
"""One-time authorized retexture: original FDR crate wood, edited crop pixels only.

Upstream art: Farmer's Delight Refabricated, MIT, Copyright (c) 2020 vectorwing.
Pinned upstream branch snapshot fabric/latest/26.2 at commit below.
This script intentionally leaves all existing game mechanics untouched.
"""
from __future__ import annotations
from pathlib import Path
import hashlib
import io
import json
from urllib.request import Request, urlopen
from PIL import Image

BASE = Path("src/main/resources/assets/italiansdelight")
TOOLS = Path(".github/scripts")
DOC = Path("docs/orchards-1.1.0")
UPSTREAM_COMMIT = "699c451161249b1bb2d4244b7de6bb10890a44a8"
UPSTREAM_URL = (
    "https://raw.githubusercontent.com/"
    "MehVahdJukaar/FarmersDelightRefabricated/"
    + UPSTREAM_COMMIT +
    "/src/main/resources/assets/farmersdelight/textures/block/"
)
# SHA1 of actual upstream Git blobs checked against the GitHub connector.
UPSTREAM_BLOBS = {
    ("tomato", "side"): "0e7cb941dcc5a1a326ccad8ad805a642cd819637",
    ("tomato", "top"): "3f31af22583c8b4d5744afae64ca3f8712c44e43",
    ("potato", "side"): "c7357b137a8cbeffeb3b55038ee8a00d913e679d",
    ("potato", "top"): "0fd8707aaebe36893f70a86d2acee39a56607b97",
    ("cabbage", "side"): "8b61ffed91285b834502a460060307d8d0e15953",
    ("cabbage", "top"): "694f32175ac34fffd2bf64f2d0c65f9db8797033",
}

# FDR's actual wooden crate pixel palette, measured on tomato_crate_side/top.
# These RGB values are NEVER modified; only other (crop) colors get recolored.
WOOD = {
    (0x78, 0x58, 0x36), (0x80, 0x5e, 0x36), (0x8b, 0x67, 0x3c),
    (0x55, 0x3a, 0x1f), (0x40, 0x29, 0x13), (0x99, 0x71, 0x40),
    (0x66, 0x4d, 0x28),
}
DEST = {
    "olive": "tomato",      # round fruits, recolored as green / black olives
    "almond": "potato",     # uneven oval crops, recolored as almond shells
    "cherry": "tomato",    # round red fruits, cherry color accents
    "basil": "cabbage",    # leafy bunches, brighter basil greens
    "cardoon": "cabbage",  # leaves, green + purple cardoon inflorescences
}
# Five-level crop palettes, dark-to-light, with full 16x16 pixel-art shading.
PALETTES = {
    "almond": ["#34291c", "#60452e", "#8c623f", "#b58a5d", "#d4a976",
               "#e2c396", "#f1d7ad"],
    "basil": ["#182b1a", "#28492b", "#386738", "#4a8640", "#62a64c",
              "#84c469", "#b2d78b"],
    "cardoon": ["#233425", "#374b33", "#4c684a", "#69875d", "#87a478",
                "#a7bb94", "#d1d1a5"],
}
# Source tomato crop-specific color roles: original green leaves vs red fruit.
TOMATO_GREEN = {(0x20,0x38,0x21):0, (0x33,0x52,0x34):1}
TOMATO_RED = {
    (0x42,0x13,0x13):0, (0x63,0x1c,0x1c):1, (0x94,0x1e,0x33):2,
    (0xc0,0x34,0x1f):3, (0xff,0x80,0x73):4
}
OLIVE_FRUIT = ["#152319","#2b381e","#4e6129","#799040","#adbc67"]
CHERRY_FRUIT = ["#42121b","#762337","#af2940","#e0454a","#ff907d"]
OLIVE_LEAF = ["#1f3827","#406039"]
CHERRY_LEAF = ["#1e3526","#497b49"]

def rgb(v: str):
    return tuple(bytes.fromhex(v.lstrip("#")))

def gitsha(payload: bytes) -> str:
    return hashlib.sha1(b"blob "+str(len(payload)).encode()+b"\0"+payload).hexdigest()

def fetch_upstream(source: str, face: str) -> Image.Image:
    key = (source, face)
    url = UPSTREAM_URL + f"{source}_crate_{face}.png"
    request = Request(url, headers={"User-Agent":"ItaliansDelight-Authorized-Texture-Retexture"})
    with urlopen(request, timeout=25) as resp:
        payload = resp.read()
    actual = gitsha(payload)
    assert actual == UPSTREAM_BLOBS[key], f"Upstream FDR original changed: {key}: {actual}"
    im = Image.open(io.BytesIO(payload)).convert("RGBA")
    assert im.size == (16,16), (key, im.size)
    assert all(p[3] == 255 for p in im.getdata()), key
    return im

def recolor(original: Image.Image, crop: str) -> tuple[Image.Image,int]:
    dst = original.copy()
    colors = sorted({
        original.getpixel((x,y))[:3]
        for y in range(16) for x in range(16)
        if original.getpixel((x,y))[:3] not in WOOD
    }, key=lambda c:(sum(c),c))
    assert len(colors) >= 4, (crop,colors)
    palette = [rgb(s) for s in PALETTES[crop]] if crop in PALETTES else None
    lum_map = {color: index for index, color in enumerate(colors)}
    n = len(colors)
    affected = 0
    for y in range(16):
        for x in range(16):
            old = original.getpixel((x,y))
            srgb = old[:3]
            if srgb in WOOD:
                assert dst.getpixel((x,y)) == old
                continue
            if crop in ("olive", "cherry"):
                if srgb in TOMATO_GREEN:
                    new = (OLIVE_LEAF if crop=="olive" else CHERRY_LEAF)[TOMATO_GREEN[srgb]]
                    new = rgb(new)
                else:
                    assert srgb in TOMATO_RED, f"Unknown tomato crop color: {srgb}"
                    new = rgb((OLIVE_FRUIT if crop=="olive" else CHERRY_FRUIT)[TOMATO_RED[srgb]])
            else:
                # Preserve the exact contours and shadows of the original crops,
                # mapping the source's luminance tiers to palette tiers.
                idx = round(lum_map[srgb] * (len(palette)-1) / max(1,n-1))
                new = palette[idx]
                if crop=="cardoon" and lum_map[srgb] >= n-2 and (x*7+y*3)%11 in (2,3,4):
                    new = rgb("#a78ab4" if idx >= len(palette)-1 else "#715a85")
            dst.putpixel((x,y), new + (255,))
            affected += 1
    assert 20 <= affected <= 180, (crop,affected)
    # All wood pixels and all alpha values are bit-perfect copies of FDR.
    for y in range(16):
        for x in range(16):
            a = original.getpixel((x,y))
            b = dst.getpixel((x,y))
            assert a[3] == b[3] == 255
            if a[:3] in WOOD:
                assert a == b, (crop,x,y,a,b)
            else:
                assert a != b, (crop,x,y,a,b)
    return dst, affected

source_images = {(s,f):fetch_upstream(s,f) for s,f in UPSTREAM_BLOBS}
palette_log = {}
for crop, original_kind in DEST.items():
    palette_log[crop] = {}
    for face in ("side","top"):
        image, n = recolor(source_images[(original_kind, face)], crop)
        target = BASE / f"textures/block/{crop}_crate_{face}.png"
        assert target.is_file(), target
        image.save(target)
        check = Image.open(target).convert("RGBA")
        assert list(image.getdata()) == list(check.getdata()), target
        palette_log[crop][face] = {"source":original_kind,"recolored_pixels":n,
                                   "untouched_wood_pixels":256-n}
        print(f"{crop}_crate_{face}: {n} crop pixels recolored, {256-n} FDR original wood pixels exact")

# Offset ONLY the right berry down by 1, leaving the already approved
# size, 16x16 palette, left berry and green foliage exactly unchanged.
cherry_path = BASE / "textures/item/cherries.png"
old_cherry_bytes = cherry_path.read_bytes()
assert gitsha(old_cherry_bytes) == "4dd38ed35bd25de85a22f73388faff49bf67e5c4", \
    "Cherry item changed since the user approved its smaller size"
original_cherry = Image.open(cherry_path).convert("RGBA")
assert original_cherry.size == (16,16)
sprite = original_cherry.copy()
# Move only right berry body (x>=9, y=8..13) down one; top and lower row
# remain inside sprite bounds. Extend existing stem at x10,y8 to reconnect.
for y in range(8,15):
    for x in range(9,15):
        sprite.putpixel((x,y),(0,0,0,0))
for y in range(8,14):
    for x in range(9,15):
        sprite.putpixel((x,y+1),original_cherry.getpixel((x,y)))
sprite.putpixel((10,8),original_cherry.getpixel((10,7)))
assert sprite.getpixel((10,8))[3] == 255
assert all(sprite.getpixel((x,y))[3] in (0,255) for y in range(16) for x in range(16))
assert all(sprite.getpixel((x,y)) == original_cherry.getpixel((x,y))
           for y in range(16) for x in range(9))
assert all(sprite.getpixel((x,y)) == original_cherry.getpixel((x,y))
           for y in range(8) for x in range(16))
before = sum(p[3]==255 for p in original_cherry.getdata())
after = sum(p[3]==255 for p in sprite.getdata())
assert before == 76 and after == 77, (before,after)
sprite.save(cherry_path)
print("Cherry item: right berry lowered by 1px, stem extended, left unchanged")

# Regression test now pins new 1px offset + precise FDR wooden slat palette.
check_path = TOOLS / "check_orchard_crates.py"
check_src = check_path.read_text(encoding="utf-8")
needle = 'assert sum(p[3]==255 for row in pix for p in row)==76'
assert check_src.count(needle)==1, "Unknown existing cherry QA fixture"
check_src=check_src.replace(needle,'''assert sum(p[3]==255 for row in pix for p in row)==77
assert pix[8][4][3]==255 and pix[8][10][3]==255
assert pix[8][11][3]==0 and pix[14][11][3]==255
# One berry must begin lower, but the upper foliage remains unchanged.
assert pix[9][11][:3]!=pix[8][11][:3]''')
needle = 'print("OK: smaller 76-pixel cherry sprite retained with alpha 0/255")'
assert needle in check_src
check_src=check_src.replace(needle,'print("OK: cherry berry lowered 1px (77 opaque pixels) while size remains unchanged")')
needle = '    for face in ("side","top"):\n        pix=check_rgba_pixel_art(A/f"textures/block/{name}_{face}.png",(16,16))\n        assert all(p[3]==255 for row in pix for p in row)'
assert needle in check_src
check_src=check_src.replace(needle,'''    for face in ("side","top"):
        pix=check_rgba_pixel_art(A/f"textures/block/{name}_{face}.png",(16,16))
        assert all(p[3]==255 for row in pix for p in row)
        wood={(0x78,0x58,0x36),(0x80,0x5e,0x36),(0x8b,0x67,0x3c),
              (0x55,0x3a,0x1f),(0x40,0x29,0x13),(0x99,0x71,0x40),
              (0x66,0x4d,0x28)}
        assert 80 <= sum(p[:3] in wood for row in pix for p in row) <= 236
        assert len({p[:3] for row in pix for p in row}) >= 10
'''.rstrip())
check_path.write_text(check_src, encoding="utf-8")

# Retain proper MIT attribution for reworked third-party art.
credits = DOC / "FDR_CRATES_LICENSE.md"
assert not credits.exists()
credits.write_text('''# Original Farmer's Delight Refabricated crate textures — MIT

This mod includes modified crop-crate *side* and *top* art derived from
Farmer's Delight Refabricated, with original wooden crate pixels left
unchanged. Upstream files come from

https://github.com/MehVahdJukaar/FarmersDelightRefabricated/tree/fabric/latest/26.2/src/main/resources/assets/farmersdelight/textures/block

Pinned upstream revision: 699c451161249b1bb2d4244b7de6bb10890a44a8.
Original source crates: tomato, potato, cabbage.
Source license: MIT License, Copyright (c) 2020 vectorwing.

MIT License

Copyright (c) 2020 vectorwing

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
''',encoding="utf-8")

doc = DOC / "CRATES_2026-11-04.md"
doc.write_text(doc.read_text(encoding="utf-8") + '''

## Revisione texture su feedback dell'autore — 4 novembre 2026

- Gli ultimi dieci PNG dei crate sono derivati dalle texture **originali**
  di Farmer's Delight Refabricated (tomato / potato / cabbage crates).
- Sono stati ricolorati **esclusivamente** i pixel dei raccolti: i pixel della
  cornice, delle doghe e delle ombreggiature del legno restano esattamente uguali
  ai corrispondenti PNG FDR, coordinate per coordinate.
- Olive e ciliegie: base tomato crate; mandorle: base potato crate; basilico
  e cardoon: base cabbage crate. Anche i contorni dei vegetali sono conservati
  rispetto alle rispettive basi originali.
- Sprite item ciliegie: dimensione precedente invariata; **ciliegia di destra
  abbassata di un pixel** e picciolo allungato per mantenerne la continuità.
- Nessuna texture fluido o altra arte approvata è stata modificata.
- Attribuzione e licenza MIT della grafica originale in
  [FDR_CRATES_LICENSE.md](FDR_CRATES_LICENSE.md).
- Verifica cromatica pixel per pixel e alpha completamente opaca per i crate.
- Palette/maschere di riferimento:
''' + json.dumps(palette_log,ensure_ascii=False,indent=2) + '\n',encoding="utf-8")
print("OK: FDR original wood preserved, recolored crops only, attribution and tests updated")
