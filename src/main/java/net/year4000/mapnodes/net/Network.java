package net.year4000.mapnodes.net;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.year4000.mapnodes.MapNodesPlugin;

@Data
public final class Network {
    @Setter(AccessLevel.PRIVATE)
    private String name = "unknown";

    public void updateName() {
        String[] header = new String[] {"GetServer"};
        final Network network = this;

        MapNodesPlugin.getInst().getConnector().send(header, (data, error) -> {
            if (error == null) {
                network.setName(data.readUTF());
            }
        });
    }
}
