{
    "map": {
        "name": "Midnight",
        "version": "1.8",
        "description": "Kill the other team's before time runs out.",
        "authors": ["c9c2b7fe-e2c1-4266-9556-aafccc0d1f13"]
    },

    "world": {
        "spawn": [{"point": {"xyz": "0, 48, 0"}}],
        "time_lock": "midnight"
    },

    "games": {
        "deathmatch": {
            "time_limit": "15m",
            "max_score": 100
        }
    },

    "regions": {
        "global": {
            "events": {
                "destroy": {
                    "blocks": ["long_grass"],
                    "allow": true
                },
                "creature_spawns": {
                    "creatures": ["zombie", "skeleton", "bat"],
                    "allow": true
                },
                "item_drops": {
                    "items": ["golden_apple"],
                    "allow": true
                },
                "kill_player": {
                    "drop_items": [{"item": "golden_apple"}]
                }
            },
            "zones": [{"global": {}}]
        }
    },

    "kits": {
        "spawn": {
            "armor": {
                "helmet": {"item": "leather_helmet"},
                "chestplate": {"item": "chainmail_chestplate"},
                "leggings": {"item": "chainmail_leggings"},
                "boots": {"item": "leather_boots", "nbt": {
                    "enchantments": [{"name": "protection_fall", "level": 2}]
                }}
            },
            "items": [
                {"slot": 0, "item": "stone_sword"},
                {"slot": 1, "item": "bow", "nbt": {
                    "enchantments": [{"name": "arrow_infinite", "level": 1}]
                }},
                {"slot": 28, "item": "arrow"}
            ],
            "effects": [
                {"name": "night_vision", "duration": -1, "amplifier": 1, "ambient": true},
                {"name": "speed", "duration": -1, "amplifier": 1, "ambient": true}
            ]
        }
    },

    "teams": {
        "green": {
            "name": "Green",
            "color": "green",
            "kit": "spawn",
            "size": 12,
            "spawns": [{"point": {"xyz": "-54, 6, 0"}}]
        },
        "yellow": {
            "name": "Yellow",
            "color": "yellow",
            "kit": "spawn",
            "size": 12,
            "spawns": [{"point": {"xyz": "0, 6, 55"}}]
        },
        "red": {
            "name": "Red",
            "color": "red",
            "kit": "spawn",
            "size": 12,
            "spawns": [{"point": {"xyz": "0, 6, -54"}}]
        },
        "blue": {
            "name": "Blue",
            "color": "blue",
            "kit": "spawn",
            "size": 12,
            "spawns": [{"point": {"xyz": "55, 6, 0"}}]
        }
    }
}
