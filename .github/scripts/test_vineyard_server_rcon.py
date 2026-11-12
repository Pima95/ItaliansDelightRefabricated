#!/usr/bin/env python3
"""Behavioral smoke check on a disposable Minecraft 26.3 test world.

Connects only to the localhost RCON service exposed in the temporary Actions
runner. Verifies actual farmland survival after placing crop stakes, chili,
tomatoes, and grapes, and validates the new horizontal rope block states.
No player inventory, networking permissions, or persistent user worlds involved.
"""
import socket
import struct
import time

PASSWORD = "local-vineyard-1.2.0-tests-only"
HOST = ("127.0.0.1", 25575)

def recv_exact(sock, n):
    buf = bytearray()
    while len(buf) < n:
        data = sock.recv(n - len(buf))
        if not data:
            raise ConnectionError("RCON disconnected")
        buf.extend(data)
    return bytes(buf)

class Rcon:
    def __init__(self):
        last_error = None
        for _ in range(20):
            try:
                self.sock = socket.create_connection(HOST, timeout=5)
                self.sock.settimeout(5)
                break
            except OSError as exc:
                last_error = exc
                time.sleep(0.3)
        else:
            raise ConnectionError(f"RCON not listening on {HOST}: {last_error}")
        self.request_id = 10
        self._send(1, 3, PASSWORD)
        for _ in range(3):
            ident, kind, _ = self._recv()
            if ident == -1:
                raise RuntimeError("RCON rejected test password")
            if ident == 1 and kind == 2:
                break
        else:
            raise RuntimeError("RCON authentication timeout")

    def _send(self, ident, kind, payload):
        packet = struct.pack("<ii", ident, kind) + payload.encode("utf-8") + b"\x00\x00"
        self.sock.sendall(struct.pack("<i", len(packet)) + packet)

    def _recv(self):
        size = struct.unpack("<i", recv_exact(self.sock, 4))[0]
        if size < 10 or size > 4096:
            raise ValueError(f"Bad RCON frame length {size}")
        frame = recv_exact(self.sock, size)
        ident, kind = struct.unpack("<ii", frame[:8])
        return ident, kind, frame[8:-2].decode("utf-8", "replace")

    def command(self, command):
        self.request_id += 1
        ident = self.request_id
        self._send(ident, 2, command)
        for _ in range(5):
            response_id, _, payload = self._recv()
            if response_id == ident:
                return payload
        raise RuntimeError(f"Missing RCON reply for {command}")

    def close(self):
        self.sock.close()

def run(rcon, cmd):
    reply = rcon.command(cmd)
    print(f"RCON {cmd}: {reply}", flush=True)
    return reply

def assert_block(rcon, x, y, z, block):
    out = run(rcon, f"execute if block {x} {y} {z} {block}")
    if "Test passed" not in out:
        raise AssertionError(f"Block {block} at {x},{y},{z} not present: {out}")

def assert_any_block(rcon, x, y, z, *blocks):
    # A mature grape root reacts to its new horizontal support at the next
    # scheduled tick, often before RCON can inspect the just-placed rope.
    # The rope OR the resulting canopy is correct; neither is a regression.
    for block in blocks:
        reply = rcon.command(f"execute if block {x} {y} {z} {block}")
        if "Test passed" in reply:
            print(f"PASSED one of: ({x},{y},{z}) = {block}", flush=True)
            return
    raise AssertionError(f"None of {blocks} at {x},{y},{z}")

def wait_block(rcon, x, y, z, block, timeout=14):
    deadline = time.monotonic() + timeout
    cmd = f"execute if block {x} {y} {z} {block}"
    while time.monotonic() < deadline:
        result = rcon.command(cmd)
        if "Test passed" in result:
            print(f"PASSED eventually: {cmd}", flush=True)
            return
        time.sleep(0.25)
    raise AssertionError(f"Timed out waiting for {block} at {x},{y},{z}: {result}")

def test():
    rcon = Rcon()
    try:
        run(rcon, "forceload add 0 0")
        run(rcon, "setblock 1 80 0 minecraft:water")
        run(rcon, "setblock 0 80 0 minecraft:farmland[moisture=7]")
        assert_block(rcon, 0, 80, 0, "minecraft:farmland")
        run(rcon, "setblock 0 81 0 italiansdelight:crop_stake")
        assert_block(rcon, 0, 81, 0, "italiansdelight:crop_stake")
        assert_block(rcon, 0, 80, 0, "minecraft:farmland")
        # Check farmland AFTER real server ticks, not just in the same tick
        # as /setblock. The old forceSolidOn flag could cause later decay.
        time.sleep(3)
        assert_block(rcon, 0, 81, 0, "italiansdelight:crop_stake")
        assert_block(rcon, 0, 80, 0, "minecraft:farmland")

        run(rcon, "setblock 0 81 0 italiansdelight:chili_crop[age=7]")
        assert_block(rcon, 0, 80, 0, "minecraft:farmland")
        run(rcon, "setblock 0 81 0 italiansdelight:staked_tomatoes[age=3]")
        assert_block(rcon, 0, 80, 0, "minecraft:farmland")

        run(rcon, "setblock 0 81 0 italiansdelight:grape_stem[age=7]")
        assert_block(rcon, 0, 80, 0, "minecraft:farmland")
        # Isolate rope-attachment tests from grape growth: a mature grape
        # root would convert this rope to canopy before it can be checked.
        # Grape canopy growth is tested separately on hydrated soil at x=10.
        run(rcon, "setblock 0 81 0 italiansdelight:crop_stake")
        run(rcon, "setblock 0 82 0 italiansdelight:horizontal_rope[axis=x]")
        run(rcon, "setblock 1 82 0 italiansdelight:horizontal_rope[axis=x]")
        assert_any_block(rcon, 0, 82, 0,
            "italiansdelight:horizontal_rope[axis=x]",
            "italiansdelight:grape_leaves[distance=0]")
        assert_any_block(rcon, 1, 82, 0,
            "italiansdelight:horizontal_rope[axis=x]",
            "italiansdelight:grape_leaves[distance=1]")
        # Assert the rope attaches to newly placed stakes, and disconnects
        # when a stake is removed (not merely that the rope exists).
        run(rcon, "setblock 2 82 0 italiansdelight:crop_stake")
        assert_block(rcon, 1, 82, 0,
            "italiansdelight:horizontal_rope[axis=x,tied_positive=true]")
        run(rcon, "setblock 2 82 0 minecraft:air")
        assert_block(rcon, 1, 82, 0,
            "italiansdelight:horizontal_rope[axis=x,tied_positive=false]")
        run(rcon, "setblock 2 81 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 2 82 0 italiansdelight:crop_stake")
        assert_block(rcon, 1, 82, 0,
            "italiansdelight:horizontal_rope[axis=x,tied_positive=true]")
        assert_block(rcon, 2, 82, 0, "italiansdelight:crop_stake[rope_tied=true]")
        # Planting replaces the post with a crop block but retains the stake
        # anchor and its single leash-style loop. This verifies state updates
        # (not a simulated right click; that requires manual client testing).
        run(rcon, "setblock 2 82 0 italiansdelight:chili_crop[age=0,rope_tied=true]")
        assert_block(rcon, 2, 82, 0, "italiansdelight:chili_crop[rope_tied=true]")
        assert_block(rcon, 1, 82, 0,
            "italiansdelight:horizontal_rope[axis=x,tied_positive=true]")
        run(rcon, "setblock 2 82 0 italiansdelight:staked_tomatoes[age=0,rope_tied=true]")
        assert_block(rcon, 2, 82, 0, "italiansdelight:staked_tomatoes[rope_tied=true]")
        assert_block(rcon, 1, 82, 0,
            "italiansdelight:horizontal_rope[axis=x,tied_positive=true]")
        assert_block(rcon, 2, 81, 0, "minecraft:farmland")
        assert_block(rcon, 0, 80, 0, "minecraft:farmland")

        # A crop that loses farmland drops its plant but leaves the physical
        # stake at the exact same position, also for a newly planted tomato.
        for x, crop in ((3, "italiansdelight:staked_tomatoes"),
                        (4, "italiansdelight:chili_crop")):
            run(rcon, f"setblock {x} 80 0 minecraft:farmland[moisture=7]")
            run(rcon, f"setblock {x} 81 0 {crop}")
            assert_block(rcon, x, 81, 0, f"{crop}[age=0]")
            run(rcon, f"setblock {x} 80 0 minecraft:dirt")
            wait_block(rcon, x, 81, 0, "italiansdelight:crop_stake")
            assert_block(rcon, x, 80, 0, "minecraft:dirt")

        # Standalone chili crops NEVER produce a wooden post when uprooted.
        # They use the same ID but staked=false; seeds normally call
        # BlockItem.useOn and getStateForPlacement for this default.
        run(rcon, "setblock 8 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 8 81 0 italiansdelight:chili_crop[age=0,staked=false]")
        assert_block(rcon, 8, 81, 0, "italiansdelight:chili_crop[age=0,staked=false]")
        run(rcon, "setblock 8 80 0 minecraft:dirt")
        wait_block(rcon, 8, 81, 0, "minecraft:air")

        # Invariant: the wooden post never gets replaced while the crop
        # grows, is harvested, or dies because farmland was trampled.
        run(rcon, "setblock 14 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 14 81 0 italiansdelight:crop_stake[crop=1,crop_age=0]")
        assert_block(rcon, 14, 81, 0, "italiansdelight:crop_stake[crop=1,crop_age=0]")
        run(rcon, "setblock 14 81 0 italiansdelight:crop_stake[crop=1,crop_age=7]")
        assert_block(rcon, 14, 81, 0, "italiansdelight:crop_stake[crop=1,crop_age=7]")
        run(rcon, "setblock 14 80 0 minecraft:dirt")
        wait_block(rcon, 14, 81, 0, "italiansdelight:crop_stake[crop=0,crop_age=0]")
        assert_block(rcon, 14, 80, 0, "minecraft:dirt")

        # Rich Soil Farmland survives under the same physical support and
        # supports both plants, without turning into vanilla farmland.
        run(rcon, "setblock 15 80 0 farmersdelight:rich_soil_farmland[moisture=7]")
        run(rcon, "setblock 15 81 0 italiansdelight:crop_stake[crop=2,crop_age=0]")
        assert_block(rcon, 15, 80, 0, "farmersdelight:rich_soil_farmland")
        assert_block(rcon, 15, 81, 0, "italiansdelight:crop_stake[crop=2,crop_age=0]")
        run(rcon, "setblock 15 81 0 italiansdelight:crop_stake[crop=1,crop_age=0]")
        assert_block(rcon, 15, 81, 0, "italiansdelight:crop_stake[crop=1,crop_age=0]")

        # Rooted chili can climb onto pre-existing wooden supports while
        # leaving their block IDs intact, never spawning fused chili_crop.
        run(rcon, "setblock 12 80 1 minecraft:water")
        run(rcon, "setblock 12 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 12 81 0 italiansdelight:crop_stake[crop=1,crop_age=7]")
        run(rcon, "setblock 12 82 0 italiansdelight:crop_stake")
        run(rcon, "setblock 12 83 0 italiansdelight:crop_stake")
        # A single cultivated post can climb onto two more stacked posts,
        # in the same manner as original FDR tomatoes climb rope segments.
        # The first crop stage is 0; FDR tomatoes have a 0..3 age range.
        for x, crop, mature in (
                (5, "italiansdelight:staked_tomatoes", 3),
                (6, "italiansdelight:chili_crop", 7)):
            run(rcon, f"setblock {x} 80 1 minecraft:water")
            run(rcon, f"setblock {x} 80 0 minecraft:farmland[moisture=7]")
            # Legacy roots must migrate seamlessly to the persistent wooden
            # block; the upper supports stay crop_stake, only CROP changes.
            run(rcon, f"setblock {x} 81 0 {crop}[age={mature}]")
            run(rcon, f"setblock {x} 82 0 italiansdelight:crop_stake")
            run(rcon, f"setblock {x} 83 0 italiansdelight:crop_stake")
        # Free-standing mature chili grows onto the original vertical FDR
        # rope just like tomatoes. The upper part has no post collision and
        # becomes a hanging chili block, with a maximum total height of 3.
        run(rcon, "setblock 13 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 13 81 0 italiansdelight:chili_crop[age=7,staked=false]")
        run(rcon, "setblock 13 82 0 farmersdelight:rope")
        run(rcon, "setblock 13 83 0 farmersdelight:rope")
        assert_block(rcon, 13, 81, 0, "italiansdelight:chili_crop[staked=false]")
        # Existing centered vine + horizontal rope neighbors, including
        # adjacent spans which are themselves directly above wooden stakes.
        for x in (9, 11):
            run(rcon, f"setblock {x} 80 0 minecraft:dirt")
            run(rcon, f"setblock {x} 81 0 italiansdelight:crop_stake")
        # Keep the grape root hydrated. Without water within four blocks
        # accelerated random ticks dry the farmland, uproot the stem, and
        # correctly remove its canopy; this is not a lateral-growth failure.
        run(rcon, "setblock 10 80 1 minecraft:water")
        run(rcon, "setblock 10 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 10 81 0 italiansdelight:grape_stem[age=7]")
        for x in (9, 10, 11):
            run(rcon, f"setblock {x} 82 0 italiansdelight:horizontal_rope[axis=x]")
        # The matured root must start a leaf canopy immediately when a
        # horizontal rope is newly installed, without waiting for random ticks.
        assert_block(rcon, 10, 81, 0, "italiansdelight:grape_stem[age=7]")
        wait_block(rcon, 10, 82, 0,
            "italiansdelight:grape_leaves[distance=0]", timeout=8)
        run(rcon, "time set day")
        run(rcon, "gamerule minecraft:random_tick_speed 700")
        try:
            for x, kind, mature in ((5, 2, 3), (6, 1, 7)):
                wait_block(rcon, x, 81, 0,
                    f"italiansdelight:crop_stake[crop={kind},crop_age={mature}]", timeout=20)
                wait_block(rcon, x, 82, 0,
                    f"italiansdelight:crop_stake[crop={kind}]", timeout=20)
                # Accelerate only this test by setting the age, not the block ID.
                run(rcon, f"setblock {x} 82 0 italiansdelight:crop_stake[crop={kind},crop_age={mature}]")
                wait_block(rcon, x, 83, 0,
                    f"italiansdelight:crop_stake[crop={kind}]", timeout=20)
                assert_block(rcon, x, 80, 0, "minecraft:farmland")
            wait_block(rcon, 12, 82, 0,
                "italiansdelight:crop_stake[crop=1]", timeout=20)
            run(rcon, "setblock 12 82 0 italiansdelight:crop_stake[crop=1,crop_age=7]")
            wait_block(rcon, 12, 83, 0,
                "italiansdelight:crop_stake[crop=1]", timeout=20)
            assert_block(rcon, 12, 81, 0, "italiansdelight:crop_stake[crop=1,crop_age=7]")
            assert_block(rcon, 15, 80, 0, "farmersdelight:rich_soil_farmland")
            wait_block(rcon, 13, 82, 0,
                "italiansdelight:chili_on_rope[age=0,staked=false]", timeout=20)
            run(rcon, "setblock 13 82 0 italiansdelight:chili_on_rope[age=7,staked=false]")
            wait_block(rcon, 13, 83, 0,
                "italiansdelight:chili_on_rope[staked=false]", timeout=20)
            assert_block(rcon, 13, 81, 0, "italiansdelight:chili_crop[staked=false]")
            wait_block(rcon, 10, 82, 0, "italiansdelight:grape_leaves[distance=0]", timeout=20)
            wait_block(rcon, 9, 82, 0, "italiansdelight:grape_leaves[distance=1]", timeout=20)
            wait_block(rcon, 11, 82, 0, "italiansdelight:grape_leaves[distance=1]", timeout=20)
            assert_block(rcon, 10, 80, 0, "minecraft:farmland")
            assert_block(rcon, 10, 81, 0, "italiansdelight:grape_stem[age=7]")
        finally:
            # Diagnostics on a failed canopy assertion: differentiate loss of
            # the mature root from loss of the center leaf or its side spans.
            for x, y, z, expected in (
                    (10, 80, 0, "minecraft:farmland"),
                    (10, 80, 0, "minecraft:dirt"),
                    (10, 81, 0, "italiansdelight:grape_stem[age=7]"),
                    (10, 82, 0, "italiansdelight:grape_leaves[distance=0]"),
                    (10, 82, 0, "italiansdelight:horizontal_rope[axis=x]"),
                    (9, 82, 0, "italiansdelight:grape_leaves[distance=1]"),
                    (11, 82, 0, "italiansdelight:grape_leaves[distance=1]")):
                run(rcon, f"execute if block {x} {y} {z} {expected}")
            run(rcon, "gamerule minecraft:random_tick_speed 3")

        run(rcon, "save-all")
        print("PASSED: real permanent stakes, rich soil farmland, chili rope climbing, original tomato ages and grape leaves", flush=True)
    finally:
        rcon.close()

if __name__ == "__main__":
    test()
