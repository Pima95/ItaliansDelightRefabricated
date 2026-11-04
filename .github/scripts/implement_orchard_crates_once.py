#!/usr/bin/env python3
"""One-time Orchard 1.1.0 crate implementation. Excludes all fluid textures."""
from pathlib import Path
from PIL import Image
import json, hashlib

AS=Path("src/main/resources/assets/italiansdelight")
DATA=Path("src/main/resources/data")
JAVA=Path("src/main/java/dev/italiansdelight/common")
CROPS=[
  ("olive","olives","Olive Crate","Cassa di Olive"),
  ("almond","almonds","Almond Crate","Cassa di Mandorle"),
  ("cherry","cherries","Cherry Crate","Cassa di Ciliegie"),
  ("basil","basil","Basil Crate","Cassa di Basilico"),
  ("cardoon","cultivated_cardoon","Cardoon Crate","Cassa di Cardi")]
def write(path,value):
    assert not path.exists(),f"Already exists: {path}"
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(value,encoding="utf-8")
def patch(path,a,b):
    s=path.read_text(encoding="utf-8")
    assert s.count(a)==1,(path,a[:70],s.count(a))
    path.write_text(s.replace(a,b),encoding="utf-8")
def jsonout(path,obj):
    write(path,json.dumps(obj,ensure_ascii=False,indent=2)+"\n")
def jsonpatch(path,fn):
    obj=json.loads(path.read_text(encoding="utf-8"))
    fn(obj)
    path.write_text(json.dumps(obj,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")
# FDR 26.2 crate parity: ordinary oak-plank-derived, full wooden cube.
declarations="    // Reversible nine-crop Farmer's Delight-style storage crates.\n"
for name,_,_,_ in CROPS:
    sym=name.upper()
    declarations+=f'''    public static final ResourceKey<Block> {sym}_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("{name}_crate"));
    public static final Block {sym}_CRATE = register(
        {sym}_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD).setId({sym}_CRATE_KEY))
    );

'''
patch(JAVA/"registry/ModBlocks.java","    // -------------------- Basil --------------------",declarations+"    // -------------------- Basil --------------------")
patch(JAVA/"registry/ModBlocks.java",
      "        fire.add(FRUIT_CHERRY_CABINET, 5, 20);",
      "        fire.add(FRUIT_CHERRY_CABINET, 5, 20);\n"+"\n".join(f"        fire.add({name.upper()}_CRATE, 5, 20);" for name,_,_,_ in CROPS))
patch(JAVA/"item/group/ModItemGroups.java",
      "                output.accept(ModItems.CHERRIES);\n",
      "                output.accept(ModItems.CHERRIES);\n                // Reversible orchard/field crop storage blocks.\n"+
      "\n".join(f"                output.accept(ModBlocks.{name.upper()}_CRATE);" for name,_,_,_ in CROPS)+"\n")
patch(JAVA/"item/group/ModItemGroups.java",
      "                // Fruits are Creative-only until harvesting, drops and food\n                // mechanics are designed together for all orchard trees.",
      "                // Orchard fruits still lack natural harvesting and food mechanics;\n                // crates only pack and unpack the existing inventory items.")
patch(JAVA/"registry/ModItems.java",
      "        // Orchard fruits: Creative-only placeholder ingredients for a later\n        // shared harvesting system. No food components, recipes, drops,\n        // or special interactions are registered at this stage.",
      "        // Orchard fruits: Creative-only acquisition until harvesting is added.\n        // Only reversible storage crafting is provided; no foods or tree drops.")
for locale in ("it_it","en_us"):
    def add_lang(obj,loc=locale):
        for n,_,en,it in CROPS:
            k=f"block.italiansdelight.{n}_crate"
            assert k not in obj
            obj[k]=en if loc=="en_us" else it
    jsonpatch(AS/f"lang/{locale}.json",add_lang)
def add_axe(obj):
    obj["values"].extend(f"italiansdelight:{n}_crate" for n,_,_,_ in CROPS)
jsonpatch(DATA/"minecraft/tags/block/mineable/axe.json",add_axe)
for name,ingredient,_,_ in CROPS:
    n=f"{name}_crate";b=f"italiansdelight:{n}";i=f"italiansdelight:{ingredient}"
    jsonout(AS/f"blockstates/{n}.json",{"variants":{"":{"model":f"italiansdelight:block/{n}"}}})
    jsonout(AS/f"models/block/{n}.json",{"parent":"minecraft:block/cube_bottom_top",
        "textures":{"bottom":"farmersdelight:block/crate_bottom",
        "side":f"italiansdelight:block/{n}_side","top":f"italiansdelight:block/{n}_top"}})
    jsonout(AS/f"models/item/{n}.json",{"parent":f"italiansdelight:block/{n}"})
    jsonout(AS/f"items/{n}.json",{"model":{"type":"minecraft:model","model":f"italiansdelight:block/{n}"}})
    jsonout(DATA/f"italiansdelight/recipe/{n}.json",{"type":"minecraft:crafting_shaped",
        "category":"building","pattern":["###"]*3,"key":{"#":i},"result":{"id":b}})
    jsonout(DATA/f"italiansdelight/recipe/{ingredient}_from_crate.json",{"type":"minecraft:crafting_shapeless",
        "category":"misc","ingredients":[b],"result":{"id":i,"count":9}})
    jsonout(DATA/f"italiansdelight/loot_table/blocks/{n}.json",{"type":"minecraft:block",
        "pools":[{"rolls":1,"conditions":[{"condition":"minecraft:survives_explosion"}],
                  "entries":[{"type":"minecraft:item","name":b}]}]})
    for kind in ("item","block"):
        jsonout(DATA/f"c/tags/{kind}/storage_blocks/{name}.json",{"replace":False,"values":[b]})
for kind in ("block","item"):
    jsonout(DATA/f"c/tags/{kind}/storage_blocks.json",
        {"replace":False,"values":[f"italiansdelight:{n}_crate" for n,_,_,_ in CROPS]})
# Original 16px pixel art: top wooden rim, open crop display, and three side slats.
# The bottom uses the actual FDR block texture by model reference.
palettes={
  "olive":["273522","485a2d","778b40","a0ae5c","2b4030","667a3a"],
  "almond":["4c3924","906235","b58a4c","d6b074","625039","c5a36a"],
  "cherry":["591c28","982337","c52b3a","ea5151","28543c","f88479"],
  "basil":["1f4728","2e7132","5ba144","9ac667","356339","77b452"],
  "cardoon":["334a3b","567b58","739b73","a4b895","715686","b194bb"]}
def rgba(h):return tuple(bytes.fromhex(h))+(255,)
wood={k:rgba(v) for k,v in {"edge":"77522d","light":"9d713f",
    "mid":"6f4d27","shadow":"4c3218","dark":"372213","base":"2b2015"}.items()}
def render(name,top):
    c=list(map(rgba,palettes[name]))
    im=Image.new("RGBA",(16,16),wood["base"]); p=im.load()
    def put(x,y,color):
        if 0<=x<16 and 0<=y<16:p[x,y]=color
    for y in range(2,14):
        for x in range(2,14):put(x,y,c[(x//3+y//4)%3])
    if name in ("olive","cherry"):
        for n,(x,y) in enumerate([(4,3),(9,3),(12,6),(5,7),(9,9),(4,11),(12,12),(8,5),(11,10)]):
            if name=="olive":
                for dx,dy,i in [(0,0,0),(1,0,1),(0,1,1),(1,1,2),(0,2,1),(1,2,0)]:put(x+dx,y+dy,c[i])
                put(x,y,c[3])
                if n%3==0:put(x-1,y,c[4])
            else:
                for dx,dy,i in [(0,0,0),(1,0,2),(0,1,2),(1,1,3),(2,1,2),(1,2,1),(2,2,0)]:put(x+dx,y+dy,c[i])
                put(x,y,c[5]);put(x+1,y-1,c[4])
    elif name=="almond":
        for x,y in [(3,3),(7,3),(11,3),(5,8),(9,8),(3,11),(12,11)]:
            for dx,dy,i in [(0,0,0),(1,0,2),(2,0,0),(0,1,1),(1,1,3),(2,1,2),(0,2,1),(1,3,0)]:put(x+dx,y+dy,c[i])
            put(x+1,y+1,c[4])
    elif name=="basil":
        for x,y in [(3,3),(9,3),(6,6),(11,8),(3,10),(8,11)]:
            for dx,dy,i in [(1,0,4),(0,1,1),(1,1,2),(2,1,2),(0,2,2),(1,2,3),(2,2,1),(1,3,0)]:put(x+dx,y+dy,c[i])
            put(x,y+1,c[5])
    else:
        for x,y in [(4,3),(10,4),(6,8),(11,10),(3,11)]:
            for dx,dy,i in [(0,0,0),(1,0,4),(0,1,1),(1,1,5),(2,1,4),(0,2,2),(1,3,0)]:put(x+dx,y+dy,c[i])
    if top:
        for y in range(16):
            for x in range(16):
                if x in (0,15) or y in (0,15):
                    put(x,y,wood["light"] if (x*13+y*17)%5==0 else wood["edge"])
                elif x in (1,14) or y in (1,14):
                    put(x,y,wood["shadow"] if x==14 or y==14 else wood["mid"])
        for x,y in [(2,1),(6,0),(12,0),(0,9),(14,14),(8,15)]:
            put(x,y,wood["light"] if y<=1 else wood["dark"])
    else:
        for y in range(16):
            for x in range(16):
                if y<=1:put(x,y,wood["light"] if (x//3)%2 else wood["edge"])
                elif y==2:put(x,y,wood["dark"] if 2<=x<=13 else wood["mid"])
                elif x in (0,1,14,15):put(x,y,wood["mid"] if x<14 else wood["shadow"])
        for y in (5,10,13):
            for x in range(2,14):put(x,y,wood["shadow"] if y==13 else wood["mid"])
            for x in range(3,13):put(x,y,wood["edge"] if (x+y)%4==0 else wood["light"])
        for x in range(2,14):put(x,15,wood["dark"] if x%5==0 else wood["mid"])
        for x,y in [(2,3),(13,4),(2,8),(13,9),(2,12),(13,12)]:put(x,y,wood["dark"])
    assert all(p[x,y][3]==255 for y in range(16) for x in range(16))
    return im
for name,_,_,_ in CROPS:
    for face in ("side","top"):
        render(name,face=="top").save(AS/f"textures/block/{name}_crate_{face}.png")
# Preserve approved cherry stem/leaf palette and geometry, reduce fruit size.
path=AS/"textures/item/cherries.png";orig=path.read_bytes()
gitblob=b"blob "+str(len(orig)).encode()+b"\0"+orig
assert hashlib.sha1(gitblob).hexdigest()=="3a6b5c68bd0998ae59a7e3054c780cb2bf37518b"
im=Image.open(path).convert("RGBA");assert im.size==(16,16)
for y in range(7,16):
    for x in range(16):im.putpixel((x,y),(0,0,0,0))
shades={"dark":(113,34,43,255),"shade":(152,31,50,255),"red":(197,43,58,255),
        "light":(231,68,69,255),"spark":(247,126,107,255),"stem":(68,58,38,255)}
for x,y in ((6,7),(10,7)):im.putpixel((x,y),shades["stem"])
for left in (3,9):
    for y in range(8,14):
        xs=range(left+1,left+5) if y in (8,12) else range(left+2,left+4) if y==13 else range(left,left+6)
        for x in xs:
            dx=x-left; shade="red"
            if y in (8,13) or dx in (0,5):shade="dark"
            elif y>=12 or dx>=4:shade="shade"
            if y==9 and dx in (2,3):shade="light"
            if y==10 and dx==1:shade="light"
            if y==9 and dx==2:shade="spark"
            im.putpixel((x,y),shades[shade])
assert sum(im.getpixel((x,y))[3]==255 for y in range(16) for x in range(16))==76
im.save(path)
# Update legacy visual test: almond SHA unchanged, cherry intentionally smaller.
test=Path(".github/scripts/check_orchard_fruits_creative.py")
s=test.read_text();a=s.index("expected = {");b=s.index("\n# Creative-only:",a)
s=s[:a]+'''# Almond remains approved; cherry fruit was deliberately resized.
expected_almonds = "bdc426ebd75d2adc704999bc62b0bfbf2ccc17a290f877acb09ebc989f97ca8c"
for name in ("almonds","cherries"):
    path = ASSETS / f"textures/item/{name}.png"
    if name == "almonds":
        assert hashlib.sha256(path.read_bytes()).hexdigest() == expected_almonds
    pixels = check_rgba_pixel_art(path, (16, 16))
    assert all(px[3] in (0,255) for row in pixels for px in row)
    opaque = [px for row in pixels for px in row if px[3]]
    assert (65 <= len(opaque) <= 95) if name == "cherries" else (85 <= len(opaque) <= 130)
    assert len({px[:3] for px in opaque}) >= 5
'''+s[b:]
s=s[:s.index("# Creative-only:")]+'''# Orchard fruits still have no harvesting or edibility; crate recipes are intentional.
assert all(f'registerFoodItem("{n}"' not in items for n in ids)
assert tab.index("output.accept(ModItems.OLIVES);") < tab.index("output.accept(ModItems.ALMONDS);") < tab.index("output.accept(ModItems.CHERRIES);")
allowed={f"{n}_crate.json" for n in ("olive","almond","cherry")}
allowed|={f"{n}_from_crate.json" for n in ids}
for folder in (DATA / "italiansdelight/recipe", DATA / "italiansdelight/loot_table"):
    if folder.is_dir():
        for path in folder.rglob("*.json"):
            content=path.read_text(encoding="utf-8")
            if path.parent==DATA / "italiansdelight/recipe" and path.name in allowed:
                continue
            assert not any(f"italiansdelight:{n}" in content for n in ids), (
                "Premature fruit crafting/harvesting",str(path))
print("OK: original olive/almond art retained, cherry sprite smaller and binary-alpha")
print("OK: no new fruit harvesting or food effects, only reversible crate recipes")
'''
test.write_text(s,encoding="utf-8")
# Add new static regression checks to the existing GitHub Java 25 build.
patch(Path(".github/workflows/pizza-build.yml"),
      "      - name: Validate Creative-only orchard fruit items and original artwork\n",
      "      - name: Validate five reversible Orchard crates and their artwork\n"
      "        run: python3 .github/scripts/check_orchard_crates.py\n"
      "      - name: Validate Creative-only orchard fruit items and original artwork\n")
write(Path(".github/scripts/check_orchard_crates.py"),'''#!/usr/bin/env python3
"""Static validation of all FDR-style Orchard crates and 9-to-1 recipes."""
import json
from pathlib import Path
from check_orchard_signs_and_cutting import check_rgba_pixel_art
A=Path("src/main/resources/assets/italiansdelight")
D=Path("src/main/resources/data")
J=Path("src/main/java/dev/italiansdelight/common")
crops={"olive":"olives","almond":"almonds","cherry":"cherries",
       "basil":"basil","cardoon":"cultivated_cardoon"}
blocks=(J/"registry/ModBlocks.java").read_text()
creative=(J/"item/group/ModItemGroups.java").read_text()
axe=json.loads((D/"minecraft/tags/block/mineable/axe.json").read_text())
for crop,item in crops.items():
    name=f"{crop}_crate";rid=f"italiansdelight:{name}"
    sym=f"{crop.upper()}_CRATE"
    assert blocks.count(f"public static final Block {sym} = register(")==1
    assert creative.count(f"output.accept(ModBlocks.{sym});")==1
    assert rid in axe["values"]
    assert json.loads((A/f"blockstates/{name}.json").read_text())=={
        "variants":{"":{"model":f"italiansdelight:block/{name}"}}}
    assert json.loads((A/f"items/{name}.json").read_text())=={
        "model":{"type":"minecraft:model","model":f"italiansdelight:block/{name}"}}
    assert json.loads((A/f"models/item/{name}.json").read_text())=={
        "parent":f"italiansdelight:block/{name}"}
    assert json.loads((A/f"models/block/{name}.json").read_text())=={
        "parent":"minecraft:block/cube_bottom_top",
        "textures":{"bottom":"farmersdelight:block/crate_bottom",
          "side":f"italiansdelight:block/{name}_side","top":f"italiansdelight:block/{name}_top"}}
    for face in ("side","top"):
        pix=check_rgba_pixel_art(A/f"textures/block/{name}_{face}.png",(16,16))
        assert all(p[3]==255 for row in pix for p in row)
    pack=json.loads((D/f"italiansdelight/recipe/{name}.json").read_text())
    unpack=json.loads((D/f"italiansdelight/recipe/{item}_from_crate.json").read_text())
    assert pack=={"type":"minecraft:crafting_shaped","category":"building",
      "pattern":["###"]*3,"key":{"#":f"italiansdelight:{item}"},"result":{"id":rid}}
    assert unpack=={"type":"minecraft:crafting_shapeless","category":"misc",
      "ingredients":[rid],"result":{"id":f"italiansdelight:{item}","count":9}}
    loot=json.loads((D/f"italiansdelight/loot_table/blocks/{name}.json").read_text())
    assert loot["pools"][0]["entries"]==[{"type":"minecraft:item","name":rid}]
    assert {"condition":"minecraft:survives_explosion"} in loot["pools"][0]["conditions"]
    for lang in ("it_it","en_us"):
        assert json.loads((A/f"lang/{lang}.json").read_text())[f"block.italiansdelight.{name}"]
    for kind in ("item","block"):
        specific=json.loads((D/f"c/tags/{kind}/storage_blocks/{crop}.json").read_text())
        general=json.loads((D/f"c/tags/{kind}/storage_blocks.json").read_text())
        assert specific["values"]==[rid] and rid in general["values"]
pix=check_rgba_pixel_art(A/"textures/item/cherries.png",(16,16))
assert all(p[3] in (0,255) for row in pix for p in row)
assert sum(p[3]==255 for row in pix for p in row)==76
print("OK: five registered crates, ten opaque textures, block/item assets, drops, tags")
print("OK: 9-in and 9-out recipes for olives, almonds, cherries, basil and cultivated cardoon")
print("OK: smaller 76-pixel cherry sprite retained with alpha 0/255")
''')
write(Path("docs/orchards-1.1.0/CRATES_2026-11-04.md"),'''# Orchard 1.1.0 — cinque crate e ciliegie ridotte

Cinque blocchi piazzabili: olive_crate, almond_crate, cherry_crate, basil_crate, cardoon_crate.
Craft 3x3 di nove ingredienti in una cassa; una cassa produce nove ingredienti.
Il cardoon usa cultivated_cardoon raccolto, non la pianta selvatica cardoon.
Grafica pixel art 16x16 originale, lati a doghe e top a cornice, senza alpha intermedi;
fondo del crate referenziato al modello Farmer's Delight Refabricated
(farmersdelight:block/crate_bottom), non duplicato nel repository.
Registrazione Block + BlockItem, item model 26.2, blockstate, loot, Creative,
traduzioni IT/EN, crafting, tag storage, mineable/axe, suoni e proprietà del legno.
Icona cherries ridotta solo nei due frutti con piccioli e foglia precedenti.
Nessuna variazione a fluidi, altri sprite, raccolta alberi o proprietà alimentari.
Riferimento studiato: MehVahdJukaar/FarmersDelightRefabricated, fabric/latest/26.2.
Controlli: check_orchard_crates.py, check_orchard_fruits_creative.py,
check_creative_tab.py, check_all_crafting_26_2.py, build Java 25.
Da verificare in Minecraft 26.2: rendering, JEI, craft, rottura e drop.
''')
print("Created 5 crates + 10 textures, updated cherries and static tests.")
