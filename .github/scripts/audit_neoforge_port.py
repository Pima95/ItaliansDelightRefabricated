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
                json.loads(content)
            except ValueError as exc:
                errors.append(f"{p}: {exc}")
        if p.suffix == ".java" and re.search(r"net\\.fabricmc\\.", content):
            fabric.append(str(p))
print("Remaining Java files with Fabric API imports:", len(fabric))
for p in fabric:
    print("PORT PENDING:", p)
print("Modern item definitions pending review:", len(list(root.glob("resources/assets/italiansdelight/items/*.json"))))
for e in errors:
    print("ERROR:", e)
assert not errors, "Static content audit failed"
print("Static content audit passed; not a compile or runtime certification.")
