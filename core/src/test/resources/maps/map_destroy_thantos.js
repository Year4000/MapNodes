{
    "map": {
        "name": "Thanatos",
        "version": "1.4",
        "description": "Each team is to destroy the enemy monument, situated on the sides of the enemy base.",
        "authors": ["c9c2b7fe-e2c1-4266-9556-aafccc0d1f13"]
    },

    "world": {
        "spawn": [{"point": {"xyz": "0, 64, 0"}}],
        "time_lock": "noon",
        "world_height": 80
    },

    "games": {
        "destroy": {
            "targets": [
                {"owner": "yellow", "challenger": "red", "name": "Left Monument", "region": "red_left_monument"},
                {"owner": "yellow", "challenger": "red", "name": "Right Monument", "region": "red_right_monument"},
                {"owner": "red", "challenger": "yellow", "name": "Left Monument", "region": "yellow_left_monument"},
                {"owner": "red", "challenger": "yellow", "name": "Right Monument", "region": "yellow_right_monument"}
            ]
        }
    },

    "regions": {
        "global": {
            "events": {
                "chest": {
                    "items": [
                        {"item": "arrow", "amount": 64},
                        {"item": "arrow", "amount": 64},
                        {"item": "bow", "nbt": {
                            "enchantments": [
                                {"name": "arrow_damage", "level": 1},
                                {"name": "arrow_knockback", "level": 1}
                            ]
                        }},
                        {"item": "glass", "amount": 64},
                        {"item": "wood", "amount": 64},
                        {"item": "glass", "amount": 64},
                        {"item": "bow", "nbt": {
                            "enchantments": [
                                {"name": "arrow_damage", "level": 1},
                                {"name": "arrow_knockback", "level": 1}
                            ]
                        }},
                        {"item": "arrow", "amount": 64},
                        {"item": "arrow", "amount": 64}
                    ],
                    "fill": true,
                    "keep_filled": true
                },
                "kill_player": {
                    "drop_items": [
                        {"item": "gold_nugget", "amount": 4},
                        {"item": "gold_ingot"}
                    ]
                },
                "item_drops": {
                    "items": [
                        "obsidian",
                        "redstone_block",
                        "daylight_detector",
                        "redstone_lamp_off",
                        "redstone_torch_off",
                        "pumkpin_seeds",
                        "seeds",
                        "sapling",
                        "yellow_flower",
                        "red_rose",
                        "pumpkin_pie",
                        "flint"
                    ],
                    "allow": false
                },
                "destroy": {"blocks": ["trapped_chest"], "allow": false}
            },
            "zones": [{"global": {}}]
        },
        "yellow_spawn": {
            "events": {
                "entity_damage": {"damage_causes": ["entity_attack"], "allow": false},
                "enter": {"apply": ["red"], "message": "region.enter.spawn", "allow": false},
                "build": {"message": "region.build.spawn", "allow": false},
                "destroy": {"message": "region.destroy.spawn", "allow": false}
            },
            "zones": [
                {"cylinder": {"center": {"xyz": "71, 50, -4"}, "radius": 5, "height": 13}},
                {"cylinder": {"center": {"xyz": "71, 50, 6"}, "radius": 5, "height": 13}},
                {"cube": {"center": {"xyz": "71, 50, 1"}, "radius": 5, "height": 13}}
            ]
        },
        "red_spawn": {
            "events": {
                "entity_damage": {"damage_causes": ["entity_attack"], "allow": false},
                "enter": {"apply": ["yellow"], "message": "region.enter.spawn", "allow": false},
                "build": {"message": "region.build.spawn", "allow": false},
                "destroy": {"message": "region.destroy.spawn", "allow": false}
            },
            "zones": [
                {"cylinder": {"center": {"xyz": "-71, 50, 0"}, "radius": 5, "height": 13}},
                {"cylinder": {"center": {"xyz": "-71, 50, 10"}, "radius": 5, "height": 13}},
                {"cube": {"center": {"xyz": "-71, 50, 5"}, "radius": 5, "height": 13}}
            ]
        },
        "red_left_monument": {"events": {"build": {"allow": false}}, "zones": [{"point": {"xyz": "65, 59, 40"}}, {"point": {"xyz": "65, 60, 40"}}]},
        "red_right_monument": {"events": {"build": {"allow": false}}, "zones": [{"point": {"xyz": "65, 59, -37"}}, {"point": {"xyz": "65, 60, -37"}}]},
        "yellow_left_monument": {"events": {"build": {"allow": false}}, "zones": [{"point": {"xyz": "-64, 59, -33"}}, {"point": {"xyz": "-64, 60, -33"}}]},
        "yellow_right_monument": {"events": {"build": {"allow": false}}, "zones": [{"point": {"xyz": "-64, 59, 44"}}, {"point": {"xyz": "-64, 60, 44"}}]}
    },

    "kits": {
        "spawn": {
            "armor": {
                "helmet": {"item": "leather_helmet"},
                "chestplate": {"item":"chainmail_chestplate"},
                "leggings": {"item":"leather_leggings"},
                "boots": {"item":"leather_boots"}
            },
            "items": [
                {"slot": 0, "item": "diamond_axe", "nbt": {
                    "enchantments": [{"name": "damage_all", "level": 1}]
                }},
                {"slot": 1, "item": "iron_pickaxe"},
                {"slot": 2, "item": "bow"},
                {"slot": 4, "item": "glass", "amount": 20},
                {"slot": 5, "item": "wood", "amount": 20},
                {"slot": 6, "item": "bucket"},
                {"slot": 8, "item": "pumpkin_pie", "amount": 16},
                {"slot": 29, "item": "arrow", "amount": 16}
            ]
        }
    },

    "teams": {
        "red": {
            "name": "Red",
            "color": "red",
            "kit": "spawn",
            "size": 25,
            "spawns": [{"point": {"xyz": "-70, 58, 5", "yaw": -90, "pitch": 0}}]
        },
        "yellow": {
            "name": "Yellow",
            "color": "yellow",
            "kit": "spawn",
            "size": 25,
            "spawns": [{"point": {"xyz": "71, 58, 1", "yaw": 90, "pitch": 0}}]
        }
    }
}
