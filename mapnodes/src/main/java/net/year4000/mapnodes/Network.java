/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes;

public final class Network {
    public static final String UNKNOWN = "unknown";

    private String name = UNKNOWN;

    public Network() {
    }

    public void updateName() {
        String[] header = new String[]{"GetServer"};
        final Network network = this;

        MapNodesPlugin.getInst().getConnector().send(header, (data, error) -> {
            if (error == null) {
                network.setName(data.readUTF());
            }
        });
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Network)) return false;
        final Network other = (Network) o;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "net.year4000.mapnodes.Network(name=" + this.getName() + ")";
    }

    public void setName(String name) {
        this.name = name;
    }
}
