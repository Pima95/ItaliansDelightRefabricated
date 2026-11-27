"""Source-snapshot parity guard. Requires the pinned Fabric commit in git history."""
from pathlib import Path
import subprocess
import json

BASE = "dd0d7cef35a6d74a3db9448c07172ea57767f64a"
ROOT = "src/main/resources/"
ASSETS = ROOT + "assets/italiansdelight/"
DATA = ROOT + "data/italiansdelight/"
WOODS = {"pale_oak", "poplar"}
REMOVED = {f"{wood}_{kind}" for wood in WOODS for kind in
           ("barrel", "brewing_barrel", "crushing_tub", "crop_stake", "cheese_aging_rack")}
REMOVED |= {"olive_shelf", "almond_shelf", "fruit_cherry_shelf"}
LEGAL = ["LICENSE", "NOTICE", "CREDIT.md", "THIRD_PARTY_NOTICES.md", "docs/FDR_CRATES_LICENSE.md"]

def tree(ref):
    raw = subprocess.check_output(["git", "ls-tree", "-r", "-z", ref])
    result = {}
    for entry in raw.split(b"\0"):
        if not entry:
            continue
        meta, name = entry.split(b"\t", 1)
        mode, kind, sha = meta.decode().split()
        if kind == "blob":
            result[name.decode()] = sha
    return result

old, new = tree(BASE), tree("HEAD")

def compare(prefix, expected_removed, expected_added=(), unchanged=True):
    before = {p for p in old if p.startswith(prefix)}
    after = {p for p in new if p.startswith(prefix)}
    assert before - after == set(expected_removed), (prefix, "removed", sorted(before-after))
    assert after - before == set(expected_added), (prefix, "added", sorted(after-before))
    if unchanged:
        changed = [p for p in before & after if old[p] != new[p]]
        assert not changed, (prefix, "changed", changed)
    print(f"{prefix}: {len(before)} -> {len(after)}; parity verified")

compare(DATA+"recipe/", [
    DATA+"recipe/"+("crafting/" if name.endswith("_cheese_aging_rack") else "")+name+".json"
    for name in REMOVED])
compare(DATA+"loot_table/", [DATA+"loot_table/blocks/"+name+".json" for name in REMOVED])
compare(ASSETS+"items/", [ASSETS+"items/"+name+".json" for name in REMOVED])
compare(ASSETS+"blockstates/", [ASSETS+"blockstates/"+name+".json" for name in REMOVED],
        [ASSETS+"blockstates/"+name+".json" for name in ("apple_juice_fluid","olive_oil_fluid")],
        unchanged=False)
texture_removed = [f"block/{wood}_shelf.png" for wood in ("olive","almond","fruit_cherry")]
for wood in WOODS:
    texture_removed += [f"block/barrel_{wood}.png", f"block/crushing_tub_{wood}.png", f"block/{wood}_brewing_barrel.png"]
compare(ASSETS+"textures/", [ASSETS+"textures/"+p for p in texture_removed],
        [ASSETS+"textures/entity/signs/"+p+wood+".png" for p in ("","hanging/") for wood in ("olive","fruit_cherry")])
for path in LEGAL:
    assert old[path] == new[path], f"Original legal notice changed: {path}"
for path in new:
    if path.startswith(ASSETS+"items/"):
        assert path.replace("/items/", "/models/item/") in new, "Missing legacy model: "+path
print("IDR_SOURCE_PARITY_PASS")
