package net.year4000.mapnodes.game.regions;

public enum EventTypes {
    CUSTOM("custom"),
    ENTER("enter"),
    ENTITY_DAMAGE("entity_damage"),
    EXIT("exit"),
    BUILD("build"),
    DESTROY("destroy"),
    CHEST("chest"),
    CREATURE_SPAWN("creature_spawn"),
    KILL_PLAYER("kill_player"),
    TNT("tnt"),
    BOW("bow"),
    BLOCK_DROP("block_drop"),
    PLAYER_DROP("player_drop"),
    ;

    private String name;

    EventTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isType(String name) {
        return this.getName().equals(name.toLowerCase());
    }

    public static EventTypes getFromName(String name) {
        for (EventTypes type : values()) {
            if (type.isType(name)) {
                return type;
            }
        }
        return null;
    }
}
