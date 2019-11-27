{
    "map": {
        "name": "Metro",
        "version": "1.4",
        "description": "Using TNT cannons you must destroy the other team's base and kill your opoents at the same time. You must reach 2000 points to win.",
        "authors": ["c9c2b7fe-e2c1-4266-9556-aafccc0d1f13"]
    },

    "world": {
        "spawn": [{"point": {"xyz": "-1, 92, 59", "yaw": 180, "pitch": 0}}],
        "time_lock": "noon",
        "world_height": 110
    },

    "games": {
        "tnt_wars": {
            "max_score": 2000,
            "islands": [
                {"owner": "red", "region": "red_island"},
                {"owner": "blue", "region": "blue_island"}
            ]
        }
    },

    "regions": {
        "global": {
            "events": {
                "tnt": {"throw_blocks": true},
                "entity_damage": {
                    "damage_causes": ["entity_attack"],
                    "allow": false
                },
                "build": {
                    "blocks": ["dispenser"],
                    "gravity": true
                },
                "item_drops": {"allow": false}
            },
            "zones": [{"global": {}}]
        },
        "blue_island": {
            "events": {
                "exit": {
                    "apply": ["blue"],
                    "message": "region.exit.point",
                    "allow": false
                },
                "chest": {
                    "items": [
                        {"item": "tnt", "amount": 64},
                        {"item": "tnt", "amount": 64},
                        {"item": "tnt", "amount": 64},
                        {"item": "fence", "amount": 64},
                        {"item": "redstone_comparator", "amount": 64},
                        {"item": "diode", "amount": 64},
                        {"item": "water_bucket"},
                        {"item": "stained_clay", "damage": 11, "amount": 64},
                        {"item": "stained_clay", "damage": 11, "amount": 64}
                    ],
                    "fill": true
                }
            },
            "zones": [{"cube": {"center": {"xyz": "-45, 0, 1"}, "height": 115, "radius": 32}}]
        },
        "red_island": {
            "events": {
                "exit": {
                    "apply": ["red"],
                    "message": "region.exit.point",
                    "allow": false
                },
                "chest": {
                    "items": [
                        {"item": "tnt", "amount": 64},
                        {"item": "tnt", "amount": 64},
                        {"item": "tnt", "amount": 64},
                        {"item": "fence", "amount": 64},
                        {"item": "redstone_comparator", "amount": 64},
                        {"item": "diode", "amount": 64},
                        {"item": "water_bucket"},
                        {"item": "stained_clay", "damage": 14, "amount": 64},
                        {"item": "stained_clay", "damage": 14, "amount": 64}
                    ],
                    "fill": true
                }
            },
            "zones": [{"cube": {"center": {"xyz": "42, 0, 1"}, "height": 115, "radius": 33}}]
        }
    },

    "kits": {
        "red_spawn": {
            "armor": {
                "helmet": {"item":"leather_helmet"},
                "chestplate": {"item":"leather_chestplate", "nbt": {
                    "enchantments": [
                        {"name": "protection_explosions", "level": 2}
                    ]
                }},
                "leggings": {"item":"leather_leggings"},
                "boots": {"item":"leather_boots"}
            },
            "items": [
                {"slot": 0, "item": "diamond_pickaxe"},
                {"slot": 27, "item": "diamond_spade"},
                {"slot": 18, "item": "diamond_axe"},
                {"slot": 1, "item": "tnt", "amount": 64},
                {"slot": 28, "item": "tnt", "amount": 64},
                {"slot": 2, "item": "stained_clay", "damage": 14, "amount": 64},
                {"slot": 29, "item": "stained_clay", "damage": 14, "amount": 64},
                {"slot": 3, "item": "step", "amount": 32},
                {"slot": 4, "item": "ladder", "amount": 48},
                {"slot": 31, "item": "ladder", "amount": 48},
                {"slot": 5, "item": "redstone_block", "amount": 8},
                {"slot": 32, "item": "redstone_torch_on", "amount": 8},
                {"slot": 6, "item": "lever", "amount": 64},
                {"slot": 7, "item": "stone_button", "amount": 32},
                {"slot": 8, "item": "water_bucket"},
                {"slot": 35, "item": "water_bucket"},
                {"slot": 26, "item": "water_bucket"},
                {"slot": 9, "item": "dispenser", "amount": 2}
            ],
            "effects": [{"name": "saturation", "duration": -1, "ambient": true}]
        },
        "blue_spawn": {
            "armor": {
                "helmet": {"item":"leather_helmet"},
                "chestplate": {"item":"leather_chestplate", "nbt": {
                    "enchantments": [
                        {"name": "protection_explosions", "level": 2}
                    ]
                }},
                "leggings": {"item":"leather_leggings"},
                "boots": {"item":"leather_boots"}
            },
            "items": [
                {"slot": 0, "item": "diamond_pickaxe"},
                {"slot": 27, "item": "diamond_spade"},
                {"slot": 18, "item": "diamond_axe"},
                {"slot": 1, "item": "tnt", "amount": 64},
                {"slot": 28, "item": "tnt", "amount": 64},
                {"slot": 2, "item": "stained_clay", "damage": 11, "amount": 64},
                {"slot": 29, "item": "stained_clay", "damage": 11, "amount": 64},
                {"slot": 3, "item": "step", "amount": 32},
                {"slot": 4, "item": "ladder", "amount": 48},
                {"slot": 31, "item": "ladder", "amount": 48},
                {"slot": 5, "item": "redstone_block", "amount": 8},
                {"slot": 32, "item": "redstone_torch_on", "amount": 8},
                {"slot": 6, "item": "lever", "amount": 64},
                {"slot": 7, "item": "stone_button", "amount": 32},
                {"slot": 8, "item": "water_bucket"},
                {"slot": 35, "item": "water_bucket"},
                {"slot": 26, "item": "water_bucket"},
                {"slot": 9, "item": "dispenser", "amount": 2}
            ],
            "effects": [{"name": "saturation", "duration": -1, "ambient": true}]

        }
    },

    "teams": {
        "red": {
            "name": "Red",
            "color": "red",
            "kit": "red_spawn",
            "size": 10,
            "spawns": [{"point": {"xyz": "42, 69, 1", "yaw": 90, "pitch": 0}}]
        },
        "blue": {
            "name": "Blue",
            "color": "blue",
            "kit": "blue_spawn",
            "size": 10,
            "spawns": [{"point": {"xyz": "-45, 69, 1", "yaw": -90, "pitch": 0}}]
        }
    }
}
