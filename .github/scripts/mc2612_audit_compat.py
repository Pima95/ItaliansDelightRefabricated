#!/usr/bin/env python3
"""Read 26.1.2 loot in normalized 26.2 form FOR STATIC AUDITS ONLY.

The committed datapacks remain in the original Minecraft 26.1.2 format.
This shim does not modify source assets, Gradle outputs or game runtime.
"""
import json
from pathlib import Path


def _condition(node):
    if isinstance(node, list):
        return [_condition(x) for x in node]
    if not isinstance(node, dict):
        return node
    kind = node.get("condition")
    if kind is None:
        return _normal(node)
    if kind == "minecraft:block_state_property":
        out = {
            "type": "minecraft:match_block",
            "blocks": node["block"],
            "state": node.get("properties", {}),
        }
        out.update({k: _normal(v) for k, v in node.items()
                    if k not in ("condition", "block", "properties")})
        return out
    out = {"type": kind}
    for k, v in node.items():
        if k == "condition":
            continue
        if k == "terms":
            out[k] = [_condition(x) for x in v]
        elif k == "term":
            out[k] = _condition(v)
        else:
            out[k] = _normal(v)
    return out


def _function(node):
    assert isinstance(node, dict) and "function" in node, node
    out = {"type": node["function"]}
    for k, v in node.items():
        if k == "function":
            continue
        elif k == "conditions":
            out["condition"] = _combine_conditions(v)
        else:
            out[k] = _normal(v)
    return out


def _combine_conditions(conditions):
    if len(conditions) == 1:
        return _condition(conditions[0])
    if not conditions:
        return {"type": "minecraft:all_of", "terms": []}
    return {"type": "minecraft:all_of", "terms": [_condition(x) for x in conditions]}


def _normal(node):
    if isinstance(node, list):
        return [_normal(x) for x in node]
    if not isinstance(node, dict):
        return node
    out = {}
    for k, v in node.items():
        if k == "conditions":
            out["condition"] = _combine_conditions(v)
        elif k == "functions":
            out["modifier"] = [_function(x) for x in v]
        else:
            out[k] = _normal(v)
    return out


def load(path):
    path = Path(path)
    result = json.loads(path.read_text(encoding="utf-8"))
    if "loot_table" in path.parts:
        return _normal(result)
    return result
