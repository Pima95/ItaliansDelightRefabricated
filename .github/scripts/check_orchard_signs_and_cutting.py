#!/usr/bin/env python3
"""Orchard 1.1.0: validate olive signage, cabinet and FD Cutting Board recipes.

This only validates files/registrations, not runtime behavior: the author must
still test sign text, inventory persistence, gameplay and JEI in Minecraft.
"""
from pathlib import Path
import json
import struct
import zlib

R = Path("src/main/resources")
A = R / "assets/italiansdelight"
D = R / "data/italiansdelight"
SRC = Path("src/main/java/dev/italiansdelight")


def doc(path):
    if not path.exists():
        raise ValueError(f"Missing resource: {path}")
    return json.loads(path.read_text(encoding="utf-8"))


def check_rgba_pixel_art(path, expected_size):
    buf = path.read_bytes()
    assert buf[:8] == b"\x89PNG\r\n\x1a\n", f"Not a PNG: {path}"
    position = 8
    compressed = bytearray()
    while position < len(buf):
        length = int.from_bytes(buf[position:position+4], "big")
        tag = buf[position+4:position+8]
        value = buf[position+8:position+8+length]
        if tag == b"IHDR":
            w, h, depth, color, comp, filt, interlace = struct.unpack(">IIBBBBB", value)
            assert (w, h, depth, color, interlace) == (*expected_size, 8, 6, 0), (path, w, h, depth, color, interlace)
        elif tag == b"IDAT":
            compressed.extend(value)
        elif tag == b"IEND":
            break
        position += 12 + length
    raw = zlib.decompress(compressed)
    bpp = 4
    previous = bytearray(w*bpp)
    decoded_rows = []
    offset = 0
    for y in range(h):
        filter_type = raw[offset]
        offset += 1
        row = bytearray(raw[offset:offset + w*bpp])
        offset += w*bpp
        for x in range(len(row)):
            left = row[x-bpp] if x >= bpp else 0
            up = previous[x]
            up_left = previous[x-bpp] if x >= bpp else 0
            if filter_type == 0: pass
            elif filter_type == 1: row[x] = (row[x] + left) & 255
            elif filter_type == 2: row[x] = (row[x] + up) & 255
            elif filter_type == 3: row[x] = (row[x] + ((left+up)//2)) & 255
            elif filter_type == 4:
                p=left+up-up_left
                pa=abs(p-left); pb=abs(p-up); pc=abs(p-up_left)
                pred=left if pa<=pb and pa<=pc else (up if pb<=pc else up_left)
                row[x]=(row[x]+pred)&255
            else: raise ValueError(f"Invalid PNG filter in {path}: {filter_type}")
        assert all(row[x] in (0,255) for x in range(3,len(row),4)), f"Alpha antialiasing in {path} row {y}"
        decoded_rows.append([tuple(row[x:x+4]) for x in range(0,len(row),4)])
        previous = row
    return decoded_rows


def main():
    # Match actual Minecraft 26.2 UV layout and screen asset dimensions.
    for name, size in (
        ("block/olive_sign.png", (32, 32)),
        ("block/olive_hanging_sign.png", (32, 32)),
        ("item/olive_hanging_sign.png", (16, 16)),
        ("item/olive_sign.png", (16, 16)),
        ("gui/signs/olive.png", (24, 26)),
        ("gui/hanging_signs/olive.png", (16, 16)),
    ):
        check_rgba_pixel_art(A / "textures" / name, size)
    # User clarified: RESTORE original sign lettering shape (pre-October
    # glyph patch), modify ONLY its two dark ink colors to those of the
    # hanging sign item. Its silhouette and all wood colors are untouched.
    # Freeze approved shape/color intent against future accidental changes.
    import hashlib
    candidate = (A/"textures/item/olive_sign.png").read_bytes()
    assert hashlib.sha1(b"blob " + str(len(candidate)).encode() + b"\0" + candidate).hexdigest() == "a04604000abc6d45202ae7dc4894b860d337cce8", "Olive sign glyph shape or other pixels changed unexpectedly"
    normal_item=check_rgba_pixel_art(A/"textures/item/olive_sign.png",(16,16))
    glyph_ink={(86,49,25),(126,68,34)}
    ink=[px[:3] for row in normal_item for px in row if px[3] and px[:3] in glyph_ink]
    assert len(ink)==25, "Original normal sign ink layout must be preserved with hanging-sign ink palette"
    assert {tuple(px[:3]) for row in normal_item for px in row if px[3]} >= glyph_ink

    # Vanilla 26.2 layout: normal sign pole uses UV x28..31 with a 2px
    # gap at y14..15. Match the original olive bark palette, not the
    # bright orange sign-board palette. User requested historic treatment.
    normal = check_rgba_pixel_art(A/"textures/block/olive_sign.png", (32,32))
    bark_palette = {
        (73,65,47), (84,77,57), (99,92,67), (112,102,71), (130,117,81)
    }
    for y in list(range(0,14)) + list(range(16,30)):
        for x in range(28,32):
            rgba = normal[y][x]
            assert rgba[3] == 255 and rgba[:3] in bark_palette, (
                "Normal sign post must use olive bark grain", (x,y,rgba)
            )

    # Vanilla 26.2 atlas: the hanging sign's entire wood board uses the
    # same five color tones as stripped_olive_log.png. Steel chains
    # retain their neutral dark grey texture and alpha layout.
    hanging = check_rgba_pixel_art(A/"textures/block/olive_hanging_sign.png", (32,32))
    stripped_palette = {
        (196,130,72), (214,139,79), (219,147,85),
        (224,157,94), (229,165,105)
    }
    chain_palette = {(37,44,61), (62,68,83), (73,80,101), (91,100,124)}
    for row in hanging:
        for rgba in row:
            if rgba[3] == 255:
                assert rgba[:3] in stripped_palette | chain_palette, (
                    "Hanging sign must match olive stripped-log tones or chain steel", rgba
                )
    for old in ("sign/olive.png","sign/hanging/olive.png"):
        assert (A/"textures/entity"/old).is_file()

    standing=doc(A/"blockstates/olive_sign.json")["variants"]
    hanging=doc(A/"blockstates/olive_hanging_sign.json")["variants"]
    assert len(standing)==16 and len(hanging)==32
    for n in range(16):
        for attached in ("false","true"):
            o=hanging[f"attached={attached},rotation={n}"]
            model=f"olive_hanging_sign{'_attached' if attached=='true' else ''}_rot_{n%4}"
            assert o["model"]==f"italiansdelight:block/{model}"
            assert o.get("y",0)==(n//4)*90
            shape=doc(A/f"models/block/{model}.json")
            assert shape["textures"]["all"]=="italiansdelight:block/olive_hanging_sign"
            expected_parent = (
                f"minecraft:block/template_attached_hanging_sign_rot_{n%4}"
                if attached == "true" else f"minecraft:block/template_hanging_sign_rot_{n%4}"
            )
            assert shape["parent"] == expected_parent, f"Wrong vanilla sign model: {model}"
        o=standing[f"rotation={n}"]
        assert o["model"]==f"italiansdelight:block/olive_sign_rot_{n%4}"
        assert o.get("y",0)==(n//4)*90
    for name in ("olive_wall_sign","olive_wall_hanging_sign"):
        assert len(doc(A/f"blockstates/{name}.json")["variants"])==4
    for name in ("olive_sign","olive_hanging_sign","olive_cabinet"):
        doc(A/f"items/{name}.json")
        doc(D/f"recipe/{name}.json")
    for name in ("olive_cabinet","olive_cabinet_open"):
        m=doc(A/f"models/block/{name}.json")
        assert set(m["textures"])=={"front","side","top"}
        for texture in m["textures"].values():
            assert (A/"textures"/(texture.replace("italiansdelight:", "")+".png")).is_file()
    assert len(doc(A/"blockstates/olive_cabinet.json")["variants"])==8
    for input_name,output_name in (("olive_log","stripped_olive_log"),("olive_wood","stripped_olive_wood")):
        data=doc(D/f"recipe/cutting/{input_name}.json")
        assert data["type"]=="farmersdelight:cutting"
        assert data["ingredients"]==[f"italiansdelight:{input_name}"]
        assert data["tool"]=={"fabric:type":"farmersdelight:item_ability","action":"axe_strip"}
        assert {x["item"]["id"] for x in data["result"]}=={f"italiansdelight:{output_name}","farmersdelight:tree_bark"}
        assert data["sound"]=="minecraft:item.axe.strip"
    signsrc=(SRC/"common/registry/ModSigns.java").read_text(encoding="utf-8")
    besrc=(SRC/"common/registry/ModBlockEntities.java").read_text(encoding="utf-8")
    assert "new SignItem(" in signsrc and "new HangingSignItem(" in signsrc
    assert "BlockEntityTypes.SIGN.addValidBlock" in besrc
    assert "BlockEntityTypes.HANGING_SIGN.addValidBlock" in besrc
    assert "ModBlockEntityTypes.CABINET.get()" in besrc
    assert "addValidBlock(ModBlocks.OLIVE_CABINET)" in besrc
    hanging_recipe=doc(D/"recipe/olive_hanging_sign.json")
    assert hanging_recipe["key"]=={"C":"minecraft:iron_chain","P":"italiansdelight:stripped_olive_log"}
    assert hanging_recipe["pattern"]==["C C","PPP","PPP"]
    assert hanging_recipe["result"]=={"id":"italiansdelight:olive_hanging_sign","count":6}
    cabinet_recipe=doc(D/"recipe/olive_cabinet.json")
    assert cabinet_recipe["pattern"]==["___","D D","___"]
    assert cabinet_recipe["key"]=={"_":"italiansdelight:olive_slab","D":"italiansdelight:olive_trapdoor"}
    assert cabinet_recipe["result"]["id"]=="italiansdelight:olive_cabinet"
    print("OK: Minecraft 26.2 32x32 sign UV maps, 24x26/16x16 GUI, 16x16 hanging item")
    print("OK: all 16 standing/32 hanging orientations, original normal-item lettering restored with hanging-item ink colors")
    print("OK: FD 27-slot olive cabinet uses original 4 PNGs and shared CabinetBlockEntity")
    print("OK: two olive stripping Cutting Board recipes return stripped wood and tree bark")


if __name__=="__main__":
    main()
