"""Check the port's explicit content exclusions; inventory remaining Fabric source."""
from pathlib import Path
import json
import re
root = Path("src/main")
bad = re.compile(r"pale_?oak|poplar", re.I)
errors = []
fabric = []
for p in sorted(root.rglob("*")):
    if not p.is_file():
        continue
    if bad.search(p.as_posix()):
        errors.append(str(p))
    if p.suffix in (".java", ".json", ".mcmeta", ".toml"):
        content = p.read_text(encoding="utf-8")
        if bad.search(content):
            errors.append(str(p) + ": obsolete wood reference")
        if p.suffix == ".json":
            try:
                data = json.loads(content)
                if "/worldgen/" in p.as_posix():
                    def check_offsets(node):
                        if isinstance(node, dict):
                            if node.get("type") == "minecraft:random_offset":
                                for axis in ("xz_spread", "y_spread"):
                                    provider = node.get(axis)
                                    if isinstance(provider, dict) and provider.get("type") == "minecraft:trapezoid":
                                        errors.append(f"{p}: {axis} uses a float/newer int provider unsupported in 1.21.1")
                            for value in node.values():
                                check_offsets(value)
                        elif isinstance(node, list):
                            for value in node:
                                check_offsets(value)
                    check_offsets(data)
            except ValueError as exc:
                errors.append(f"{p}: {exc}")
        if p.suffix == ".java" and "net.fabricmc." in content:
            fabric.append(str(p))
print("Remaining Java files with Fabric API imports:", len(fabric))
for p in fabric:
    print("PORT PENDING:", p)
assets = root / "resources/assets/italiansdelight"
modern_items = sorted((assets / "items").glob("*.json"))
print("Modern item definitions pending review:", len(modern_items))
for item in modern_items:
    if not (assets / "models/item" / item.name).is_file():
        errors.append(str(item) + ": missing legacy inventory model")
    model = json.loads(item.read_text(encoding="utf-8")).get("model", {})
    if model.get("type") != "minecraft:model" or set(model) != {"type", "model"}:
        print("DYNAMIC MODEL PORT PENDING:", item.name)
print("Remaining static inventory model coverage:", len(modern_items))
for e in errors:
    print("ERROR:", e)
assert not errors, "Static content audit failed"
print("Static content audit passed; not a compile or runtime certification.")
