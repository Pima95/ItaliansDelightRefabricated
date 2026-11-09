"""Verify Orchard olive foods and two new canonical pizzas without Pillow."""
from pathlib import Path
import json, struct, zlib, re
ROOT=Path("src/main/resources")
A=ROOT/"assets/italiansdelight"; R=ROOT/"data/italiansdelight/recipe"
SRC=Path("src/main/java/dev/italiansdelight")
j=lambda p:json.loads(p.read_text(encoding="utf-8"))
def png(path,w,h):
    source=path.read_bytes()
    assert source[:8]==b"\x89PNG\r\n\x1a\n",path
    assert struct.unpack(">II",source[16:24])==(w,h),path
    pos=8;idat=b"";seen=0;png_color=None
    while pos<len(source):
        size=int.from_bytes(source[pos:pos+4],"big")
        kind=source[pos+4:pos+8];content=source[pos+8:pos+8+size]
        assert zlib.crc32(kind+content)&0xffffffff==int.from_bytes(source[pos+8+size:pos+12+size],"big"),path
        pos+=size+12
        if kind==b"IHDR":
            assert struct.unpack(">IIBBBBB",content)==(w,h,8,6,0,0,0)
        elif kind==b"IDAT":idat+=content
        elif kind==b"IEND":seen+=1;break
    assert seen==1 and pos==len(source)
    raw=zlib.decompress(idat)
    assert len(raw)==h*(1+4*w)
    previous=bytearray(4*w);out=bytearray()
    for y in range(h):
        p=y*(1+4*w);line=bytearray(raw[p+1:p+1+4*w]);f=raw[p]
        for i in range(len(line)):
            a=line[i-4] if i>=4 else 0
            b=previous[i];c=previous[i-4] if i>=4 else 0
            if f==0:v=0
            elif f==1:v=a
            elif f==2:v=b
            elif f==3:v=(a+b)//2
            elif f==4:
                q=a+b-c;v=[a,b,c][[abs(q-a),abs(q-b),abs(q-c)].index(min(abs(q-a),abs(q-b),abs(q-c)))]
            else:raise ValueError((path,f))
            line[i]=(line[i]+v)&255
        out+=line;previous=line
    assert 255 in out[3::4] and set(out[3::4]) <= {0,255}
    return out
for name in ("olive_seed","olive_pitted","olive_pate","bruschetta","olive_bruschetta"):
    assert j(A/f"items/{name}.json")["model"]["model"]==f"italiansdelight:item/{name}"
    assert j(A/f"models/item/{name}.json")["textures"]["layer0"]==f"italiansdelight:item/{name}"
    if name=="olive_seed":
        # Original rustic olive-pit texture is an indexed palette PNG.
        # Preserve it rather than converting approved artwork to RGBA.
        raw=(A/f"textures/item/{name}.png").read_bytes()
        assert raw.startswith(bytes((137,80,78,71,13,10,26,10)))
        assert struct.unpack(">II",raw[16:24])==(16,16)
    else:
        png(A/f"textures/item/{name}.png",16,16)
for name in ("mediterranea","olive_scamorza_bianca"):
    for prefix in ("raw_pizza","cooked_pizza","pizza_slice"):
        id=f"{prefix}_{name}"
        assert j(A/f"items/{id}.json")["model"]["model"]==f"italiansdelight:item/{id}"
        assert j(A/f"models/item/{id}.json")["textures"]["layer0"]==f"italiansdelight:item/{id}"
        png(A/f"textures/item/{id}.png",16,16)
png(A/"textures/block/pizza_topping_sprites/olive_pitted.png",12,12)
cut=j(R/"cutting/olive_pitted.json")
assert cut["type"]=="farmersdelight:cutting"
assert cut["ingredients"]==["italiansdelight:olives"]
assert cut["tool"]=="#c:tools/knife"
assert cut["result"]==[{"item":{"id":"italiansdelight:olive_pitted"}},{"item":{"id":"italiansdelight:olive_seed"}}]
pat=j(R/"olive_pate.json")
assert pat["ingredients"].count("italiansdelight:olive_pitted")==3
assert set(pat["ingredients"])=={"italiansdelight:olive_pitted","italiansdelight:olive_oil_bottle","italiansdelight:salt","minecraft:bowl"}
assert j(R/"bruschetta.json")["ingredients"]==["minecraft:bread","italiansdelight:tomato_slice","italiansdelight:basil"]
assert j(R/"olive_bruschetta.json")["ingredients"]==["minecraft:bread","italiansdelight:olive_pate"]
items=(SRC/"common/registry/ModItems.java").read_text()
creative=(SRC/"common/item/group/ModItemGroups.java").read_text()
for id in ("OLIVE_SEED","OLIVE_PITTED","OLIVE_PATE","BRUSCHETTA","OLIVE_BRUSCHETTA"):
    assert f"public static final Item {id}" in items
    assert creative.count(f"output.accept(ModItems.{id});")==1
assert "new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16)" in items
assert "new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)" in items
cat=(SRC/"common/pizza/PizzaToppingCatalog.java").read_text()
assert '"olive_pitted", VEGETABLE, SOLID, "italiansdelight", "olive_pitted"' in cat
assert "types.size() != 32 || nativeItems.size() != 36" in cat
for id in ("olives","olive_seed","olive_pate"):
    assert f'Identifier.parse("italiansdelight:{id}")' in cat
names=(SRC/"common/pizza/PizzaDisplayNames.java").read_text()
assert re.search(r'add\(presets,\s*"mediterranea",', names)
assert re.search(r'add\(presets,\s*"olive_scamorza_bianca",', names)
assert "presets.size() != 20" in names
for lang in ("it_it","en_us"):
    l=j(A/f"lang/{lang}.json")
    for id in ("olive_seed","olive_pitted","olive_pate","bruschetta","olive_bruschetta"):
        assert f"item.italiansdelight.{id}" in l
    for id in ("mediterranea","olive_scamorza_bianca"):
        assert f"pizza.italiansdelight.taste.{id}" in l
# The new pâté sprite is based on this mod's already-approved cream_bowl.
# Its real pottery silhouette must remain, while the food itself differs.
bowl=png(A/"textures/item/cream_bowl.png",16,16)
pate_sprite=png(A/"textures/item/olive_pate.png",16,16)
same_opaque=sum(
    bowl[n:n+4] == pate_sprite[n:n+4] and bowl[n+3]==255
    for n in range(0,len(bowl),4)
)
assert same_opaque >= 35, "Olive pâté must reuse the existing ceramic bowl silhouette"
# Open bruschettas share a single lower bread; they must not acquire
# an upper slice of bread covering their tomato or olive filling.
tomato=png(A/"textures/item/bruschetta.png",16,16)
olive=png(A/"textures/item/olive_bruschetta.png",16,16)
same_bottom=sum(
    tomato[(y*16+x)*4:(y*16+x)*4+4] == olive[(y*16+x)*4:(y*16+x)*4+4]
    for y in range(11,16) for x in range(16)
)
assert same_bottom>=78, "Both bruschettas should share the same lower bread slice"
assert all(tomato[(y*16+x)*4+3]==0 and olive[(y*16+x)*4+3]==0
           for y in range(0,6) for x in range(16)), "No top bread layer"
# Stomp accepts exactly one whole olive, produces the existing 250 mB
# olive oil, and spawns one pit only after a successful input consumption.
crushing=(SRC/"common/block/CrushingTubRecipes.java").read_text()
jei=(SRC/"integration/jei/CrushingTubJeiRecipe.java").read_text()
tub=(SRC/"common/block/entity/CrushingTubBlockEntity.java").read_text()
block=(SRC/"common/block/CrushingTubBlock.java").read_text()
assert 'if (stack.is(ModItems.OLIVES))' in crushing
assert 'return new Recipe("italiansdelight:olive_oil", STANDARD_AMOUNT,\n                new ItemStack(ModItems.OLIVE_SEED));' in crushing
assert "public static final int STANDARD_AMOUNT = 250;" in crushing
assert 'ModFluids.OLIVE_OIL_STILL, 250, new ItemStack(ModItems.OLIVE_SEED), ItemStack.EMPTY)' in jei
assert 'return recipe.byproduct().copy();' in tub
assert 'if (!byproduct.isEmpty()) popResource(level, pos.above(), byproduct);' in block
print("OK: olive processing in Crushing Tub returns one olive pit per accepted stomp, reflected in JEI")
print("OK: pâté uses original cream-bowl pottery, bruschettas use common original lower bread")
print("OK: olive pitting and pit, pate, two open bruschettas, stable recipes")
print("OK: two new 20-preset pizza combinations, 32 native toppings, texture hard-alpha")
