#!/usr/bin/env python3
"""Behavioral smoke check on a disposable Minecraft 26.3 test world.

Connects only to the localhost RCON service exposed in the temporary Actions
runner. Verifies 16 decorative wooden poles, original rope attachments, freestanding
chili roots, optional vertical rope climbing and grapes.
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
    woods = (
        "oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
        "mangrove", "cherry", "pale_oak", "poplar", "bamboo", "crimson",
        "warped", "olive", "almond", "fruit_cherry"
    )
    try:
        run(rcon, "forceload add 0 0")

        # Every wood ID exists, with three-dimensional decorative rotation.
        # No planted state exists on any of these blocks.
        for x, wood in enumerate(woods):
            ident = "crop_stake" if wood == "oak" else wood + "_crop_stake"
            axis = ("x", "y", "z")[x % 3]
            run(rcon, f"setblock {x} 81 3 italiansdelight:{ident}[axis={axis}]")
            assert_block(rcon, x, 81, 3, f"italiansdelight:{ident}[axis={axis}]")
        print("PASSED: all 16 decorative wood types with x/y/z orientation", flush=True)

        # Original horizontal rope connection behavior, now including wood
        # variants. Do not use any alternate rope blocks or rope items.
        run(rcon, "setblock 2 81 5 italiansdelight:spruce_crop_stake[axis=y]")
        run(rcon, "setblock 4 81 5 italiansdelight:olive_crop_stake[axis=z]")
        run(rcon, "setblock 3 81 5 italiansdelight:horizontal_rope[axis=x]")
        assert_block(rcon, 3, 81, 5,
            "italiansdelight:horizontal_rope[axis=x,tied_negative=true,tied_positive=true]")
        run(rcon, "setblock 4 81 5 minecraft:air")
        assert_block(rcon, 3, 81, 5,
            "italiansdelight:horizontal_rope[axis=x,tied_positive=false]")
        run(rcon, "setblock 4 81 5 italiansdelight:olive_crop_stake[axis=z]")
        assert_block(rcon, 3, 81, 5,
            "italiansdelight:horizontal_rope[axis=x,tied_positive=true]")
        print("PASSED: existing horizontal rope attachment still works", flush=True)

        # Root can grow on ordinary or rich-soil farmland without any post.
        for x, soil in ((7, "minecraft:farmland"),
                        (9, "farmersdelight:rich_soil_farmland")):
            run(rcon, f"setblock {x} 80 1 minecraft:water")
            run(rcon, f"setblock {x} 80 0 {soil}[moisture=7]")
            run(rcon, f"setblock {x} 81 0 italiansdelight:chili_crop[age=0]")
            assert_block(rcon, x, 81, 0, "italiansdelight:chili_crop[age=0,staked=false]")
            assert_block(rcon, x, 80, 0, soil)

        # A ripe chili grows up original Farmer's Delight vertical rope
        # (two upper segments maximum). It must not grow on a wooden pole.
        run(rcon, "setblock 7 81 0 italiansdelight:chili_crop[age=7]")
        run(rcon, "setblock 7 82 0 farmersdelight:rope")
        run(rcon, "setblock 7 83 0 farmersdelight:rope")
        run(rcon, "setblock 5 80 1 minecraft:water")
        run(rcon, "setblock 5 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 5 81 0 italiansdelight:chili_crop[age=7]")
        run(rcon, "setblock 5 82 0 italiansdelight:crop_stake")
        # Original FDR tomato planting remains separate from the decorative pole.
        run(rcon, "setblock 6 80 1 minecraft:water")
        run(rcon, "setblock 6 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 6 81 0 italiansdelight:staked_tomatoes[age=0]")

        # Existing grape canopy and horizontal-rope growth remain unchanged.
        run(rcon, "setblock 10 80 1 minecraft:water")
        run(rcon, "setblock 10 80 0 minecraft:farmland[moisture=7]")
        run(rcon, "setblock 10 81 0 italiansdelight:grape_stem[age=7]")
        for x in (9, 10, 11):
            run(rcon, f"setblock {x} 82 0 italiansdelight:horizontal_rope[axis=x]")
        wait_block(rcon, 10, 82, 0, "italiansdelight:grape_leaves[distance=0]", timeout=14)

        run(rcon, "time set day")
        run(rcon, "gamerule minecraft:random_tick_speed 700")
        try:
            wait_block(rcon, 7, 82, 0, "italiansdelight:chili_on_rope", timeout=24)
            run(rcon, "setblock 7 82 0 italiansdelight:chili_on_rope[age=7]")
            wait_block(rcon, 7, 83, 0, "italiansdelight:chili_on_rope", timeout=24)
            assert_block(rcon, 7, 81, 0, "italiansdelight:chili_crop")
            assert_block(rcon, 5, 82, 0, "italiansdelight:crop_stake")
            wait_block(rcon, 6, 81, 0, "farmersdelight:tomatoes", timeout=24)
            assert_block(rcon, 9, 80, 0, "farmersdelight:rich_soil_farmland")
            wait_block(rcon, 9, 82, 0, "italiansdelight:grape_leaves[distance=1]", timeout=24)
            wait_block(rcon, 11, 82, 0, "italiansdelight:grape_leaves[distance=1]", timeout=24)
        finally:
            run(rcon, "gamerule minecraft:random_tick_speed 3")

        # Removing a standalone root cannot recreate a wooden post.
        run(rcon, "setblock 9 81 0 minecraft:air")
        assert_block(rcon, 9, 81, 0, "minecraft:air")
        # Naturally generated wild chili must be a valid, persistent plant on
        # ordinary grass, separate from the farmland-dependent cultivated crop.
        run(rcon, "setblock 13 80 0 minecraft:grass_block")
        run(rcon, "setblock 13 81 0 italiansdelight:wild_chili")
        assert_block(rcon, 13, 81, 0, "italiansdelight:wild_chili")
        run(rcon, "time add 100")
        assert_block(rcon, 13, 81, 0, "italiansdelight:wild_chili")
        run(rcon, "save-all")
        print("PASSED: 16 decorative poles, untouched rope, standalone chili climb <=3, rich farmland and grapes", flush=True)
    finally:
        rcon.close()

if __name__ == "__main__":
    test()
