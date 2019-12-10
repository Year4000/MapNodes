{
    "map": {
        "name": "Viper",
        "version": "1.2",
        "description": "Capture the two enemy wool's and bring them back to your podium.",
        "authors": ["c9c2b7fe-e2c1-4266-9556-aafccc0d1f13"]
    },
 
    "world": {
        "spawn": [{"point": {"xyz": "0, 69, 0"}}],
        "time_lock": "noon",
        "world_height": 85
    },

    "games": {
        "capture": {
            "block_captures": [
                {"owner": "orange", "challenger": "blue", "region": "yellow_wool", "wool": "yellow"},
                {"owner": "orange", "challenger": "blue", "region": "orange_wool", "wool": "gold"},
                {"owner": "blue", "challenger": "orange", "region": "blue_wool", "wool": "blue"},
                {"owner": "blue", "challenger": "orange", "region": "aqua_wool", "wool": "aqua"}
            ]
        }
    },

    "regions": {
        "protections": {
            "events": {
                "build": {
                    "allow": false
                },
                "destroy": {
                    "allow": false
                }
            },
            "zones": [
                {"point": {"xyz": "0, 68, 0"}},
                {"cube": {"center": {"xyz": "68, 70, 24"}, "radius": 3, "height": 10}},
                {"cube": {"center": {"xyz": "68, 70, -23"}, "radius": 3, "height": 10}},
                {"cube": {"center": {"xyz": "-67, 70, -23"}, "radius": 3, "height": 10}},
                {"cube": {"center": {"xyz": "-67, 70, 24"}, "radius": 3, "height": 10}},
                {"cube": {"center": {"xyz": "44, 65, -27"}, "radius": 10, "height": 10}},
                {"cube": {"center": {"xyz": "44, 65, 28"}, "radius": 10, "height": 10}},
                {"cube": {"center": {"xyz": "-43, 65, -27"}, "radius": 10, "height": 10}},
                {"cube": {"center": {"xyz": "-43, 65, 28"}, "radius": 10, "height": 10}}
            ]
        },
        "aqua_wool_room": {
            "events": {
                "chest": {
                    "items": [
                        {"item": "wool", "damage": 3}
                    ],
                    "keep_filled": true,
                    "fill": true
                },
                "enter": {
                    "apply": ["blue"],
                    "message": "region.enter.room",
                    "allow": false
                },
                "build": {
                    "allow": false
                },
                "destroy": {
                    "allow": false
                }
            },
            "zones": [{"cylinder": {"center": {"xyz": "68, 70, 0"}, "radius": 5, "height": 15}}]
        },
        "blue_wool_room": {
            "events": {
                "chest": {
                    "items": [
                        {"item": "wool", "damage": 11}
                    ],
                    "keep_filled": true,
                    "fill": true
                },
                "enter": {
                    "apply": ["blue"],
                    "message": "region.enter.room",
                    "allow": false
                },
                "build": {
                    "allow": false
                },
                "destroy": {
                    "allow": false
                }
            },
            "zones": [{"cylinder": {"center": {"xyz": "16, 68, 0"}, "radius": 5, "height": 15}}]
        },
        "orange_wool_room": {
            "events": {
                "chest": {
                    "items": [
                        {"item": "wool", "damage": 1}
                    ],
                    "keep_filled": true,
                    "fill": true
                },
                "enter": {
                    "apply": ["orange"],
                    "message": "region.enter.room",
                    "allow": false
                },
                "build": {
                    "allow": false
                },
                "destroy": {
                    "allow": false
                }
            },
            "zones": [{"cylinder": {"center": {"xyz": "-15, 68, 0"}, "radius": 5, "height": 15}}]
        },
        "yellow_wool_room": {
            "events": {
                "chest": {
                    "items": [
                        {"item": "wool", "damage": 4}
                    ],
                    "keep_filled": true,
                    "fill": true
                },
                "enter": {
                    "apply": ["orange"],
                    "message": "region.enter.room",
                    "allow": false
                },
                "build": {
                    "allow": false
                },
                "destroy": {
                    "allow": false
                }
            },
            "zones": [{"cylinder": {"center": {"xyz": "-67, 70, 0"}, "radius": 5, "height": 15}}]
        },
        "blue_wool": {"zones": [{"point": {"xyz": "-67, 72, -23"}}]},
        "aqua_wool": {"zones": [{"point": {"xyz": "-67, 72, 24"}}]},
        "yellow_wool": {"zones": [{"point": {"xyz": "68, 72, 24"}}]},
        "orange_wool": {"zones": [{"point": {"xyz": "68, 72, -23"}}]}
    },
 
    "kits": {
        "spawn": {
            "armor": {
                "helmet": {"item": "leather_helmet"},
                "chestplate": {"item":"leather_chestplate"},
                "leggings": {"item":"leather_leggings"},
                "boots": {"item":"leather_boots"}
            },
            "items": [
                {"slot": 0, "item": "stone_sword"},
                {"slot": 1, "item": "bow", "nbt": {
                    "enchantments": [{"name": "arrow_infinite", "level": 1}]
                }},
                {"slot": 2, "item": "stone_pickaxe", "nbt": {
                    "enchantments": [
                        {"name": "dig_speed", "level": 1},
                        {"name": "durability", "level": 2}
                    ]
                }},
                {"slot": 3, "item": "stone_axe", "nbt": {
                    "enchantments": [
                        {"name": "dig_speed", "level": 1},
                        {"name": "durability", "level": 2}
                    ]
                }},
                {"slot": 4, "item": "carrot_item", "amount": 64},
                {"slot": 5, "item": "smooth_brick", "amount": 64},
                {"slot": 6, "item": "wood", "damage": 1, "amount": 64},
                {"slot": 28, "item": "arrow"}
            ]
        }
    },
 
    "teams": {
        "orange": {
            "name": "Orange",
            "color": "gold",
            "kit": "spawn",
            "size": 25,
            "spawns": [
                {"point": {"xyz": "-43, 72, 28", "yaw": 180, "pitch": 0}},
                {"point": {"xyz": "-43, 72, -27"}}
            ]
        },
        "blue": {
            "name": "Blue",
            "color": "blue",
            "kit": "spawn",
            "size": 25,
            "spawns": [
                {"point": {"xyz": "44, 72, 28", "yaw": 180, "pitch": 0}},
                {"point": {"xyz": "44, 72, -27"}}
            ]
        }
    }
}
