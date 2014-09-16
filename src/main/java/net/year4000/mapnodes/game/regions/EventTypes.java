package net.year4000.mapnodes.game.regions;

public enum EventTypes {
    ENTER("enter")
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
