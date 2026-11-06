#!/usr/bin/env python3
"""Regression: 8 fixed Almond nut sites; two-layer biome tint in Almond and Cherry."""
from pathlib import Path
import hashlib
import json
from check_orchard_signs_and_cutting import check_rgba_pixel_art

A=Path("src/main/resources/assets/italiansdelight")
JAVA=Path("src/main/java/dev/italiansdelight/ItaliansDelightClient.java")
STAGES={
    "almond":("buds","flowers","unripe","fruiting"),
    "fruit_cherry":("buds","flowers_early","flowers","unripe","fruiting")
}
PINS={
    "almond":"7fcfcb85a8f561c114f294650a764969df8aaac2306e6232324c23df90176c39",
    "fruit_cherry":"1d4b76bbd78358e48203f89551f3ff56c4d658827fd0b013e59aebc0bc108514"
}
FACES={"up","down","north","south","east","west"}
def rgba(path):
    return [p for row in check_rgba_pixel_art(path,(16,16)) for p in row]
for tree,stages in STAGES.items():
    key=f"{tree}_leaves"
    base_path=A/f"textures/block/{key}.png"
    assert hashlib.sha256(base_path.read_bytes()).hexdigest()==PINS[tree],key
    base=rgba(base_path)
    assert all(p[3] in (0,255) for p in base)
    for stage in stages:
        stage_name=f"{key}_{stage}"
        combined=rgba(A/f"textures/block/{stage_name}.png")
        overlay=rgba(A/f"textures/block/{stage_name}_overlay.png")
        assert [p[3] for p in combined]==[p[3] for p in base],stage_name
        assert all(p[3] in (0,255) for p in overlay),stage_name
        for i,(leaf,p,top) in enumerate(zip(base,combined,overlay)):
            if p==leaf:
                assert top[3]==0,(stage_name,i,"overlay obscures leaf")
            else:
                assert leaf[3]==p[3]==255 and top==p,(stage_name,i)
        model=json.loads((A/f"models/block/{stage_name}.json").read_text())
        assert model["parent"]=="minecraft:block/leaves"
        assert model["textures"]=={
            "all":f"italiansdelight:block/{key}",
            "overlay":f"italiansdelight:block/{stage_name}_overlay",
            "particle":f"italiansdelight:block/{key}"
        },stage_name
        assert len(model["elements"])==2
        bottom,top=model["elements"]
        assert bottom["from"]==[0,0,0] and bottom["to"]==[16,16,16]
        assert all(v<0 for v in top["from"])
        assert all(v>16 for v in top["to"])
        for layer,texture,tinted in ((bottom,"#all",True),(top,"#overlay",False)):
            assert set(layer["faces"])==FACES
            for face,quad in layer["faces"].items():
                assert quad["texture"]==texture and quad["cullface"]==face
                assert quad["uv"]==[0,0,16,16]
                if tinted:
                    assert quad.get("tintindex")==0,(stage_name,face)
                else:
                    assert "tintindex" not in quad,(stage_name,face)

# Exactly eight asymmetric Almond clusters. 5px hull and 1px stem each.
# The SAME coordinates host buds, pink/white flowers, green hull and dry shells.
ALMONDS=(
    ([(2,2),(3,2),(2,3),(3,3),(3,4)],(3,1)),
    ([(9,1),(10,1),(9,2),(10,2),(9,3)],(9,0)),
    ([(11,4),(12,5),(13,5),(12,6),(13,6)],(12,3)),
    ([(5,6),(6,6),(5,7),(6,7),(5,8)],(5,5)),
    ([(1,9),(2,9),(1,10),(2,10),(2,11)],(1,8)),
    ([(10,8),(11,8),(10,9),(11,9),(11,10)],(10,7)),
    ([(5,11),(6,11),(5,12),(6,12),(4,13)],(5,10)),
    ([(9,12),(10,12),(9,13),(10,13),(9,14)],(9,11))
)
base=rgba(A/"textures/block/almond_leaves.png")
fruit_cells={p for body,stem in ALMONDS for p in body}
stems={stem for body,stem in ALMONDS}
assert len(ALMONDS)==8 and len(fruit_cells)==40 and len(stems)==8
assert not fruit_cells & stems
assert len({min(y for x,y in body) for body,stem in ALMONDS})>=6
for step,stage in enumerate(STAGES["almond"],start=1):
    pixels=rgba(A/f"textures/block/almond_leaves_{stage}.png")
    changed={(i%16,i//16) for i,(a,b) in enumerate(zip(base,pixels)) if a!=b}
    expected=stems|({body[0] for body,stem in ALMONDS} if step==1 else fruit_cells)
    assert changed==expected,(stage,"shifted pixels",changed^expected)
    assert all(pixels[y*16+x]==(76,123,65,255) for x,y in stems)
    if step in (1,2):
        assert all(pixels[body[0][1]*16+body[0][0]][0]>=200
                   for body,stem in ALMONDS)
green=rgba(A/"textures/block/almond_leaves_unripe.png")
dry=rgba(A/"textures/block/almond_leaves_fruiting.png")
assert all(green[y*16+x]!=dry[y*16+x] for x,y in fruit_cells)
assert all(green[y*16+x]==dry[y*16+x] for x,y in stems)

client=JAVA.read_text(encoding="utf-8")
assert "BlockColorRegistry.register(List.of(ALMOND_BIOME_FOLIAGE), ModBlocks.ALMOND_LEAVES)" in client
assert "BlockColorRegistry.register(List.of(FRUIT_CHERRY_BIOME_FOLIAGE), ModBlocks.FRUIT_CHERRY_LEAVES)" in client
assert client.count("BlockTintSources.foliage().colorInWorld(state, level, pos)")==2
assert "state.getValue(FruitCherryLeavesBlock.STAGE)" not in client
assert "255 * 7" in client and "255 * 5" in client
print("OK: Almond 8 scattered nut sites match every bud, pink flower, green and dry fruit stage")
print("OK: identical five-pixel almond hull and stem geometry in green and dry stages")
print("OK: 9 block models use tinted base foliage and untinted cutout blossom/fruit overlays")
print("OK: all overlays exactly match original modified pixels; binary alpha, base textures pinned")
print("OK: almond (30%) and fruit cherry (50%) biome tint applies at EVERY growth stage")
print("NOTE: shader/cutout appearance and colors in multiple biomes still need in-game review")
