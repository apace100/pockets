# Pockets

## About
Pockets is a Fabric mod that grants bundle-like functionality to armor items. That means that you can store other items inside your armor, as if it had pockets.

## Configuration
While the mod itself doesn't generate a config file, it provides configurability via datapacks.

To have other armor items other than leather leggings have pockets, you can add them via the `pockets:has_pockets` item tag, e.g.:
```json
{
  "replace": false,
  "values": [
    "minecraft:golden_leggings",
    "minecraft:leather_chestplate",
    "spectrum:glow_vision_helmet"
  ]
}
```
This in a loaded datapack at `data/pockets/tags/items/has_pockets.json` would cause those three items to also have the ability to store items.

Additionally, the `pockets:pocket_blacklist` item tag can be used to prevent items from being stored inside an armor's pockets.
