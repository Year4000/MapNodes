package net.year4000.mapnodes.game.regions;

public enum RegionTypes {
    CHUNK("chunk"),
    CUBOID("cuboid"),
    CYLINDER("cylinder"),
    GLOBAL("global"),
    POINT("point"),
    SPHERE("sphere"),
    VOID("void"),
    ;

    private String name;

    RegionTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isType(String name) {
        return this.getName().equals(name.toLowerCase());
    }

    public static RegionTypes getFromName(String name) {
        for (RegionTypes type : values()) {
            if (type.isType(name)) {
                return type;
            }
        }
        return null;
    }
}
