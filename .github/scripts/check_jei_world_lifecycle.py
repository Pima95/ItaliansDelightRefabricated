#!/usr/bin/env python3
"""Prevent JEI null-client-world crashes and stale world recipe capture."""
from pathlib import Path
folder=Path("src/main/java/dev/italiansdelight/integration/jei")
for name in ("CheeseVatJeiRecipes.java", "CheeseAgingJeiRecipes.java"):
    data=(folder / name).read_text(encoding="utf-8")
    assert "throw new NullPointerException(" not in data, name
    assert "private final SynchronizedRecipes synchronizedRecipes;" not in data, name
    assert "Minecraft.getInstance().level" in data
    assert "return List.of();" in data
    assert "getSynchronizedRecipes()" in data
    assert "getAllOfType(" in data
    print(f"OK: {name}: recipes are read from the current world when available")
print("NOTE: JEI leave/rejoin and datapack reload still require a manual client test")
