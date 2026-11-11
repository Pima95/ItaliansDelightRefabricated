#!/usr/bin/env python3
"""Fail CI when files matching .gitignore are still tracked by Git.

This is a read-only check: it never deletes files from disk or the index.
It prevents accidentally reintroducing archived documents, generated files,
IDE metadata, build output or other ignored content into the repository.
"""
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[2]

def main() -> int:
    try:
        result = subprocess.run(
            ["git", "ls-files", "--cached", "--ignored", "--exclude-standard", "-z"],
            cwd=ROOT,
            check=True,
            capture_output=True,
        )
    except (OSError, subprocess.CalledProcessError) as exc:
        print(f"FAIL: Unable to inspect tracked files: {exc}", file=sys.stderr)
        return 2

    ignored = sorted(
        path.decode("utf-8", errors="surrogateescape")
        for path in result.stdout.split(b"\0")
        if path
    )
    if ignored:
        print("FAIL: These files are already tracked even though .gitignore excludes them:")
        for path in ignored:
            print(f"  {path}")
        print("To stop tracking a file without removing your local copy:")
        print("  git rm --cached -- <path>")
        print("Commit the index change; do not delete source files needed by the mod.")
        return 1

    print("OK: No tracked files match the current .gitignore rules.")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
