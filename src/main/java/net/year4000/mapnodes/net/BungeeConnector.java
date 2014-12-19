package net.year4000.mapnodes.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.utils.Callback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;


public final class BungeeConnector implements PluginMessageListener {
    private static final String CHANNEL = "BungeeCord";
    private Deque<AbstractMap.Entry<String, Callback<ByteArrayDataInput>>> requests = new ArrayDeque<>();

    /** Register plugin channels */
    public void register() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(MapNodesPlugin.getInst(), CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(MapNodesPlugin.getInst(), CHANNEL, this);
    }

    /** Send data and return a callback */
    public void send(String[] data, Callback<ByteArrayDataInput> back) {
        if (Bukkit.getOnlinePlayers().size() == 0) {
            MapNodesPlugin.debug("No players online can not send data");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for (String line : data) {
            out.writeUTF(line);
        }

        Player player = Bukkit.getOnlinePlayers().iterator().next();
        player.sendPluginMessage(MapNodesPlugin.getInst(), CHANNEL, out.toByteArray());
        requests.add(new AbstractMap.SimpleImmutableEntry<>(data[0], back));
    }

    /** Send data with out needing an response */
    public void send(String[] data) {
        if (Bukkit.getOnlinePlayers().size() == 0) {
            MapNodesPlugin.debug("No players online can not send data");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for (String line : data) {
            out.writeUTF(line);
        }

        Player player = Bukkit.getOnlinePlayers().iterator().next();
        player.sendPluginMessage(MapNodesPlugin.getInst(), CHANNEL, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals(CHANNEL)) return;

        if (requests.size() > 0) {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String subChannel = in.readUTF();

            if (subChannel.equals(requests.peek().getKey())) {
                requests.poll().getValue().callback(in, null);
            }
        }
    }
}
