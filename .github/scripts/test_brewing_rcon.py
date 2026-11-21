#!/usr/bin/env python3
"""Functional 26.3 red-wine and cider-vinegar brewing test in an isolated Actions world.

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

        # White grapes have NO corresponding wine: must stay untouched and
        # cannot quietly produce the existing red wine.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 1,
                            "WineAmount": 0, "CultureAmount": 250,
                            "CultureQuality": 75, "BrewTime": 11995})
        time.sleep(.35)
        assert read_field(client, "JuiceAmount") == 1000
        assert read_field(client, "WineAmount") == 0
        assert read_field(client, "BrewTime") == 0
        print("PASSED: white grapes do NOT ferment into red wine", flush=True)

        # Apple juice produces the preexisting vinegar (not wine) after one
        # Rustic 12,000-tick cycle. Culture has no effect on vinegar quality.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 2,
                            "WineAmount": 0, "OutputKind": 0,
                            "CultureAmount": 250, "CultureQuality": 75,
                            "BrewTime": 11995})
        wait_for_wine(client, 1000)
        assert read_field(client, "JuiceAmount") == 0
        assert read_field(client, "OutputKind") == 1
        assert read_field(client, "WineQuality") == 0
        assert read_field(client, "CultureAmount") == 250
        print("PASSED: apple juice -> 1000 mB cider vinegar, wine culture unaffected", flush=True)

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
