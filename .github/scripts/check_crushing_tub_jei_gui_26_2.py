"""Protect the user-provided aging-based Crushing Tub JEI art and slot geometry.

No Pillow needed: binary source/derivative hashes and Java locations are checked.
The hash of crushing_tub.png must be intentionally updated if the art changes.
"""
import hashlib
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
ART = ROOT / "src/main/resources/assets/italiansdelight/textures/gui/jei"
JAVA = ROOT / "src/main/java/dev/italiansdelight/integration/jei/CrushingTubRecipeCategory.java"

for filename, sha256 in (
    ("aging.png", "d8a244f1d99773e9525e1afcb08ad73eed21a93ea1a1318349a842d9e438b96f"),
    ("crushing_tub.png", "b3d211cb0a40abeca380fac3a86815d274418b9edfa1c1a6b4119d19a0a2c76e"),
):
    content = (ART / filename).read_bytes()
    assert content[:8] == b"\x89PNG\r\n\x1a\n", filename
    assert struct.unpack_from(">IIBBBBB", content, 16) == (256, 256, 8, 6, 0, 0, 0), filename
    assert hashlib.sha256(content).hexdigest() == sha256, filename

java = JAVA.read_text()
for needle in (
    "private static final int WIDTH = 151;",
    "private static final int HEIGHT = 45;",
    "helper.createDrawable(TEXTURE, 0, 64, 18, 18)",
    "builder.addInputSlot(9, 26)",
    "builder.addOutputSlot(93, 26)",
    "builder.addOutputSlot(126, 26)",
    "byproductFrame.draw(graphics, 125, 25)",
    "FluidConstants.BUCKET / 1000L",
    ".setFluidRenderer(fluidAmount, false, 16, 16)",
):
    assert needle in java, needle
print("OK: aging original preserved; Crushing Tub JEI artwork/coordinates locked")
