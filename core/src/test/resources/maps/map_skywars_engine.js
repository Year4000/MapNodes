{
    "map": {
        "name": "Engine",
        "version": "1.1",
        "description": "Kill the other players and be the last to win! Your bow shoots exploding eggs.",
        "authors": ["c9c2b7fe-e2c1-4266-9556-aafccc0d1f13", "17192846-4356-447d-b0b2-1dbf176d5c74"]
    },

    "world": {
        "spawn": [{"point": {"xyz": "57, 95, -76", "yaw": 45, "pitch": 30}}],
        "time_lock": "noon",
        "world_height": 95
    },

    "games": {
        "skywars": {}
    },

    "regions": {
        "global": {
            "events": {
                "bow": {"explode": true},
                "tnt": {"throw_blocks": true},
                "creature_spawns": {"creatures": ["snowman", "pig"], "allow": true},
                "chest": {
                    "kits": [
                        {"name": "skywars_armor", "amount": 3},
                        {"name": "skywars_wepons", "amount": 2},
                        {"name": "skywars_blocks", "amount": 3, "repeat": true},
                        {"name": "skywars_food", "amount": 2},
                        {"name": "skywars_items", "amount": 4, "repeat": true}
                    ]
                }
            },
            "zones": [{"global": {}}]
        },
        "center": {
            "events": {
                "chest": {
                    "items": [
                        {"item": "stone_axe", "nbt": {
                            "enchantments": [{"name": "dig_speed", "level": 1}]
                        }},
                        {"item": "iron_axe", "nbt": {
                            "enchantments": [{"name": "damage_all", "level": 1}]
                        }},
                        {"item": "stone_pickaxe", "nbt": {
                            "enchantments": [{"name": "dig_speed", "level": 1}]
                        }},
                        {"item": "iron_pickaxe", "nbt": {
                            "enchantments": [{"name": "loot_bonus_blocks", "level": 1}]
                        }},
                        {"item": "stone_sword", "nbt": {
                            "enchantments": [{"name": "fire_aspect", "level": 1}]
                        }},
                        {"item": "iron_sword", "nbt": {
                            "enchantments": [{"name": "loot_bonus_mobs", "level": 1}]
                        }},
                        {"item": "diamond_chestplate", "nbt": {
                            "enchantments": [{"name": "thorns", "level": 1}]
                        }},
                        {"item": "leather_leggings", "nbt": {
                            "enchantments": [{"name": "protection_environmental", "level": 1}]
                        }},
                        {"item": "chainmail_helmet", "nbt": {
                            "enchantments": [{"name": "protection_explosions", "level": 1}]
                        }},
                        {"item": "gold_boots", "nbt": {
                            "enchantments": [{"name": "protection_fall", "level": 1}]
                        }},
                        {"item": "stone_sword"},
                        {"item": "stone_pickaxe"},
                        {"item": "stone_axe"},
                        {"item": "wood_sword"},
                        {"item": "wood_pickaxe"},
                        {"item": "wood_axe"},
                        {"item": "chainmail_chestplate"},
                        {"item": "iron_leggings"},
                        {"item": "gold_helmet"},
                        {"item": "leather_boots"},
                        {"item": "wood", "damage": 1, "amount": 10},
                        {"item": "wood", "damage": 1, "amount": 5},
                        {"item": "arrow", "amount": 3},
                        {"item": "arrow", "amount": 3},
                        {"item": "arrow", "amount": 3},
                        {"item": "arrow", "amount": 3},
                        {"item": "arrow"},
                        {"item": "bow", "nbt": {
                            "enchantments": [{"name": "arrow_fire", "level": 1}]
                        }},
                        {"item": "bow", "nbt": {
                            "enchantments": [{"name": "arrow_damage", "level": 1}]
                        }},
                        {"item": "bow", "nbt": {
                            "enchantments": [{"name": "arrow_knockback", "level": 1}]
                        }},
                        {"item": "lava_bucket"},
                        {"item": "lava_bucket"},
                        {"item": "water_bucket"},
                        {"item": "water_bucket"},
                        {"item": "snow_ball", "amount": 8},
                        {"item": "snow_ball", "amount": 6},
                        {"item": "snow_ball", "amount": 4},
                        {"item": "snow_ball", "amount": 2},
                        {"item": "ender_pearl", "amount": 8},
                        {"item": "ender_pearl", "amount": 6},
                        {"item": "ender_pearl", "amount": 4},
                        {"item": "ender_pearl", "amount": 2},
                        {"item": "tnt", "amount": 4},
                        {"item": "tnt", "amount": 4},
                        {"item": "tnt"}
                    ],
                    "amount": 4,
                    "scatter": true
                }
            },
            "zones": [
                {"cylinder": {"center": {"xyz": "0, 47, -20"}, "radius": 50, "height": 3}},
                {"cylinder": {"center": {"xyz": "0, 65, -20"}, "radius": 10, "height": 30}}
            ]
        }
    },

    "teams": {
        "players": {
            "name": "Players",
            "color": "green",
            "size": 15,
            "spawns": [
                {"point": {"xyz": "0, 61, -66"}},
                {"point": {"xyz": "-16, 61, -62"}},
                {"point": {"xyz": "-31, 61, -52"}},
                {"point": {"xyz": "17, 61, -62"}},
                {"point": {"xyz": "-41, 61, -38", "yaw": -90, "pitch": 0}},
                {"point": {"xyz": "-45, 61, -20", "yaw": -90, "pitch": 0}},
                {"point": {"xyz": "-41, 61, -2", "yaw": -90, "pitch": 0}},
                {"point": {"xyz": "-31, 61, 11", "yaw": -90, "pitch": 0}},
                {"point": {"xyz": "-17, 61, 21", "yaw": 180, "pitch": 0}},
                {"point": {"xyz": "0, 61, 25", "yaw": 180, "pitch": 0}},
                {"point": {"xyz": "18, 61, 21", "yaw": 180, "pitch": 0}},
                {"point": {"xyz": "32, 61, 11", "yaw": 180, "pitch": 0}},
                {"point": {"xyz": "42, 61, -2", "yaw": 90, "pitch": 0}},
                {"point": {"xyz": "46, 61, -20", "yaw": 90, "pitch": 0}},
                {"point": {"xyz": "42, 61, -37", "yaw": 90, "pitch": 0}},
                {"point": {"xyz": "32, 61, -52", "yaw": 90, "pitch": 0}}
            ]
        }
    }
}
