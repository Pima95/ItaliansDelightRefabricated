#!/usr/bin/env python3
"""Test every original and new Rustic-style fermentation on the dedicated server.

This never touches the player's actual worlds. We use vanilla /data on a
fresh test barrel to seed the last few ticks of an otherwise 12,000-tick
cycle; this validates the *actual block entity ticker* and NBT migration
without sleeping for ten minutes for each of several scenarios.
"""
import re
import time
from test_vineyard_server_rcon import Rcon, run

X, Y, Z = 0, 98, 8


def read_field(rcon, field):
    result = rcon.command(f"data get block {X} {Y} {Z} {field}")
    match = re.search(r":\s*(-?\d+)(?:[bslL])?\s*$", result)
    if not match:
        raise AssertionError(f"Cannot parse {field} from server response {result!r}")
    return int(match.group(1))


def put_barrel(rcon, values):
    run(rcon, f"setblock {X} {Y} {Z} minecraft:air replace")
    reply = run(rcon, f"setblock {X} {Y} {Z} italiansdelight:brewing_barrel replace")
    assert "Changed the block" in reply, reply
    nbt = ",".join(f"{key}:{value}" for key, value in values.items())
    reply = run(rcon, f"data merge block {X} {Y} {Z} {{{nbt}}}")
    assert "Modified block data" in reply or "Nothing changed" in reply, reply


def wait_for_wine(rcon, expected, seconds=8):
    deadline = time.monotonic() + seconds
    while time.monotonic() < deadline:
        amount = read_field(rcon, "WineAmount")
        if amount == expected:
            return
        time.sleep(.12)
    raise AssertionError(f"Expected {expected} mB wine, got {amount}")


def main():
    client = Rcon()
    try:
        run(client, "forceload add 0 8")

        # Purple-grape first batch: exactly 1000 mB produced, with Rustic's
        # 5..75% randomly assigned quality and no mandatory culture.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 0,
                            "WineAmount": 0, "CultureAmount": 0,
                            "CultureQuality": -1, "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "JuiceAmount") == 0
        quality = read_field(client, "WineQuality")
        assert 5 <= quality <= 75, quality
        print(f"PASSED: purple grape fermentation, 1000 mB, base quality {quality}", flush=True)

        # White grapes now ferment into their OWN stable white-wine ID.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 1,
                            "WineAmount": 0, "CultureAmount": 250,
                            "CultureQuality": 75, "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "OutputKind") == 2
        assert 74 <= read_field(client, "WineQuality") <= 79
        assert read_field(client, "CultureAmount") == 250
        print("PASSED: white grape must -> distinct white wine (culture preserved)", flush=True)

        # Cherry juice from the Crushing Tub becomes cherry wine (kind 3).
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 3,
                            "WineAmount": 0, "CultureAmount": 0,
                            "CultureQuality": -1, "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "OutputKind") == 3
        print("PASSED: cherry juice -> cherry wine", flush=True)

        # Original Rustic honey in bottles/buckets is an input, not a
        # placeholder registered Fluid that does not exist in Minecraft.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 4,
                            "WineAmount": 0, "CultureAmount": 0,
                            "CultureQuality": -1, "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "OutputKind") == 4
        print("PASSED: honey -> quality-bearing mead", flush=True)

        # Apple juice produces the preexisting vinegar (not wine) after one
        # Rustic 12,000-tick cycle. Culture has no effect on vinegar quality.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 2,
                            "WineAmount": 0, "OutputKind": 0,
                            "CultureAmount": 0, "CultureQuality": -1,
                            "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "JuiceAmount") == 0
        assert read_field(client, "OutputKind") == 1
        assert read_field(client, "WineQuality") == 0
        assert read_field(client, "CultureAmount") == 0
        print("PASSED: unseeded apple juice -> 1000 mB cider vinegar", flush=True)

        # Seeded apple juice instead yields CIDER while reusing the culture.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 2,
                            "WineAmount": 0, "OutputKind": 0,
                            "CultureAmount": 250, "CultureQuality": 70,
                            "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "OutputKind") == 5
        assert read_field(client, "CultureAmount") == 250
        assert 69 <= read_field(client, "WineQuality") <= 74
        print("PASSED: seeded apple juice -> cider, not vinegar", flush=True)

        # Distinct two-stage route: cider can ferment into apple cider vinegar.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 6,
                            "WineAmount": 0, "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "OutputKind") == 1
        print("PASSED: cider -> apple cider vinegar", flush=True)

        # Red wine itself is a fermenter input for red-wine vinegar.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 5,
                            "WineAmount": 0, "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "OutputKind") == 6
        assert read_field(client, "WineQuality") == 0
        print("PASSED: red wine -> wine vinegar (no alcoholic quality)", flush=True)

        # Test the actual new bottle IDs through the block inventory, not
        # only NBT output-kind discriminators.
        for kind, bottle_id in (
            (2, "white_wine_bottle"),
            (3, "cherry_wine_bottle"),
            (4, "mead_bottle"),
            (5, "cider_bottle"),
            (6, "red_wine_vinegar_bottle"),
        ):
            put_barrel(client, {"JuiceAmount": 0, "WineAmount": 1000,
                                "WineQuality": 65, "OutputKind": kind,
                                "BrewTime": 0})
            run(client, f"item replace block {X} {Y} {Z} container.1 with minecraft:glass_bottle 1")
            wait_for_wine(client, 750)
            returned = run(client, f"data get block {X} {Y} {Z} Items")
            assert "italiansdelight:" + bottle_id in returned, (kind, returned)
            print(f"PASSED: actual output item {bottle_id}", flush=True)

        # A real honey bottle is accepted as input and returns the glass.
        put_barrel(client, {"JuiceAmount": 0, "WineAmount": 0, "BrewTime": 0})
        run(client, f"item replace block {X} {Y} {Z} container.0 with minecraft:honey_bottle 1")
        deadline = time.monotonic() + 5
        while read_field(client, "JuiceAmount") != 250 and time.monotonic() < deadline:
            time.sleep(.1)
        assert read_field(client, "JuiceAmount") == 250
        assert read_field(client, "JuiceKind") == 4
        print("PASSED: real honey bottle transfers into mead must", flush=True)

        # Restore vinegar output for the bottle-return test below.
        put_barrel(client, {"JuiceAmount": 0, "JuiceKind": 2,
                            "WineAmount": 1000, "OutputKind": 1,
                            "WineQuality": 0, "BrewTime": 0})

        # Check the ACTUAL vessel-to-tank transfer on the server (not only
        # seeded NBT): an empty bottle in output slot 1 must be replaced with
        # the mod's preexisting cider vinegar item in the lower return slot.
        run(client, f"item replace block {X} {Y} {Z} container.1 with minecraft:glass_bottle 1")
        wait_for_wine(client, 750)
        output_items = run(client, f"data get block {X} {Y} {Z} Items")
        assert "italiansdelight:apple_cider_vinegar" in output_items, output_items
        print("PASSED: vinegar bottle is produced by the actual output slot", flush=True)

        # Likewise, feeding an actual apple-juice bucket into slot 0 fills
        # the internal tank and sends an empty bucket to the lower return slot.
        put_barrel(client, {"JuiceAmount": 0, "JuiceKind": 0,
                            "WineAmount": 0, "OutputKind": 0, "BrewTime": 0})
        run(client, f"item replace block {X} {Y} {Z} container.0 with italiansdelight:apple_juice_bucket 1")
        deadline = time.monotonic() + 5
        while read_field(client, "JuiceAmount") != 1000 and time.monotonic() < deadline:
            time.sleep(.1)
        assert read_field(client, "JuiceAmount") == 1000
        assert read_field(client, "JuiceKind") == 2
        returned_items = run(client, f"data get block {X} {Y} {Z} Items")
        assert "minecraft:bucket" in returned_items, returned_items
        print("PASSED: apple juice bucket feeds the fermentation tank and returns the empty bucket", flush=True)

        # The same tank must never mix red wine with apple cider vinegar.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 2,
                            "WineAmount": 500, "OutputKind": 0,
                            "WineQuality": 60, "BrewTime": 11995})
        time.sleep(.35)
        assert read_field(client, "JuiceAmount") == 1000
        assert read_field(client, "WineAmount") == 500
        assert read_field(client, "OutputKind") == 0
        assert read_field(client, "BrewTime") == 0
        print("PASSED: no mixing of vinegar into existing wine", flush=True)

        # Rustic does not average qualities when adding to existing wine.
        put_barrel(client, {"JuiceAmount": 500, "JuiceKind": 0,
                            "WineAmount": 250, "OutputKind": 0, "WineQuality": 89,
                            "CultureAmount": 0, "CultureQuality": -1,
                            "BrewTime": 11995})
        wait_for_wine(client, 750)
        assert read_field(client, "WineQuality") == 89
        assert read_field(client, "JuiceAmount") == 0
        print("PASSED: existing output batch retains its quality", flush=True)

        # No wine loss or fermentation with a full output tank.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 0,
                            "WineAmount": 8000, "OutputKind": 0, "WineQuality": 40,
                            "BrewTime": 11995})
        time.sleep(.35)
        assert read_field(client, "WineAmount") == 8000
        assert read_field(client, "JuiceAmount") == 1000
        assert read_field(client, "BrewTime") == 0
        print("PASSED: output full blocks brewing without consuming juice", flush=True)

        # Verify the newly persisted culture data via the real block entity.
        # Old-format migration is checked statically: vanilla /data merge
        # always includes the new tag from the live block and cannot emulate
        # a pre-upgrade save that genuinely lacks CultureAmount.
        put_barrel(client, {"JuiceAmount": 0, "WineAmount": 0,
                            "CultureAmount": 250, "CultureQuality": 62})
        assert read_field(client, "CultureAmount") == 250
        assert read_field(client, "CultureQuality") == 62
        print("PASSED: new culture amount and quality survive NBT updates", flush=True)
        run(client, "save-all")
    finally:
        client.close()


if __name__ == "__main__":
    main()
