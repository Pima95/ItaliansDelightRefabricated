#!/usr/bin/env python3
"""Functional 26.3 Rustic brewing test in an isolated GitHub Actions world.

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
    assert "Modified block data" in reply, reply


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

        # White-grape wine: same registered wine result, but a different
        # input juice. A 250 mB culture remains available and affects quality.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 1,
                            "WineAmount": 0, "CultureAmount": 250,
                            "CultureQuality": 75, "BrewTime": 11995})
        wait_for_wine(client, 1000)
        quality = read_field(client, "WineQuality")
        assert 74 <= quality <= 79, quality
        assert read_field(client, "CultureAmount") == 250
        assert read_field(client, "CultureQuality") == 75
        print(f"PASSED: white grape fermentation, conserved culture, quality {quality}", flush=True)

        # Rustic does not average qualities when adding to existing wine.
        put_barrel(client, {"JuiceAmount": 500, "JuiceKind": 0,
                            "WineAmount": 250, "WineQuality": 89,
                            "CultureAmount": 0, "CultureQuality": -1,
                            "BrewTime": 11995})
        wait_for_wine(client, 750)
        assert read_field(client, "WineQuality") == 89
        assert read_field(client, "JuiceAmount") == 0
        print("PASSED: existing output batch retains its quality", flush=True)

        # No wine loss or fermentation with a full output tank.
        put_barrel(client, {"JuiceAmount": 1000, "JuiceKind": 1,
                            "WineAmount": 8000, "WineQuality": 40,
                            "BrewTime": 11995})
        time.sleep(.35)
        assert read_field(client, "WineAmount") == 8000
        assert read_field(client, "JuiceAmount") == 1000
        assert read_field(client, "BrewTime") == 0
        print("PASSED: output full blocks brewing without consuming juice", flush=True)

        # Legacy culture from the GUI-only build had quality but no amount.
        put_barrel(client, {"JuiceAmount": 0, "WineAmount": 0,
                            "CultureQuality": 62})
        # /data merge starts from the *current* NBT which already has the
        # new key. Remove it to faithfully emulate a pre-upgrade save.
        run(client, f"data remove block {X} {Y} {Z} CultureAmount")
        assert read_field(client, "CultureAmount") == 250
        assert read_field(client, "CultureQuality") == 62
        print("PASSED: GUI-only old culture NBT migrates to 250 mB", flush=True)
        run(client, "save-all")
    finally:
        client.close()


if __name__ == "__main__":
    main()
