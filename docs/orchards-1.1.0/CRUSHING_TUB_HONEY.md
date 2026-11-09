# Crushing Tub — honeycomb-to-honey processing (1.1.0)

### Scope
Adds exactly one recipe to the already working tub in all fifteen wood variants:

| Input | Result per stomp | How to collect |
| --- | --- | --- |
| 1 vanilla honeycomb (`minecraft:honeycomb`) | 250 mB honey (`minecraft:honey`, legacy virtual tank ID) | Empty glass bottle => 1 vanilla honey bottle |

This adapts the verified [Rustic 1.12.2 Crushing Tub](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/crafting/Recipes.java) honeycomb->honey recipe to modern vanilla honeycomb; Rustic's separate honeycomb item is not present.

### Rules
- Honey is **not a registered placeable Fluid** and cannot be collected in a bucket; this is the same virtual `minecraft:honey` used by existing Barrel container compatibility. No new fluid IDs, liquid blocks, bucket items or source-creation behavior.
- One jump consumes one honeycomb and stores 250 mB; as with other recipes, max 64 input items and 8,000 mB in the tank.
- No mixing with olive oil, apple juice or water; at maximum capacity the next honeycomb is not consumed.
- Fluid remains in the tub until manually bottled; it is **not an immediate honey bottle drop**.
- The shared fluid surface renderer now uses vanilla `minecraft:block/honey_block_top`, not a fallback blue water sprite. It may be visually refined separately; no PNG assets are added.
- JEI displays a vanilla honey bottle **as a collection preview**, since the registry has no native honey Fluid to render as a fluid ingredient. Tooltip explicitly explains the 250 mB stored liquid and extraction with an empty glass bottle. The already approved `aging.png`-based GUI geometry remains unmodified.
- Existing three recipes and all Barrel/cauldron/world-fluid physics are unchanged.

### Test checklist in Minecraft
1. Place an empty Crushing Tub, insert 4 honeycombs, jump four times. Liquid should reach 1,000 mB, with honey-colored surface and no byproduct drop.
2. Collect exactly four vanilla honey bottles with four empty glass bottles. A bucket must **not** collect the honey.
3. Fill with 32 honeycombs to reach 8,000 mB. A 33rd stomp must not consume another honeycomb; verify counts before and after.
4. Try an apple or olive recipe while honey remains in the tub: no mixing or loss. Shift-right-click empty hand when no inputs remain to discard honey.
5. Save and reload a partially filled tub and confirm stored amount, no duplication and successful honey bottle extraction.
6. In JEI, search uses for vanilla honeycomb and open *Crushing Tub*: show the honey bottle sample with the 250 mB/extraction tooltip; verify other three recipes still render true fluids.
7. Repeat basic test on an olive and a fruit cherry tub.

### Verification
Automated static check `python3 .github/scripts/check_crushing_tub_honey_26_2.py` and Java 25 build in GitHub Actions. In-game outcomes require author confirmation.
