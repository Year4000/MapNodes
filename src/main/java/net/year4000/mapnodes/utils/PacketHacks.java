package net.year4000.mapnodes.utils;

import net.minecraft.server.v1_7_R4.Packet;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

public final class PacketHacks {
    /** Re-spawn a dead player */
    public static void respawnPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        final int MAX = 64;
        int counter = 0;

        // Keep forcing respawn if else fail kick player.
        while (craftPlayer.isDead()) {
            if (counter >= MAX) {
                craftPlayer.kickPlayer(Msg.locale(player, "error.cmd.error"));
                break;
            }

            ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().moveToWorld(craftPlayer.getHandle(), 0, false);
            ++counter;
        }
    }

    /** Set the tablist header and footer */
    public static void setTabListHeadFoot(Player player, String header, String footer) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        Packet headFoot = new ProtocolInjector.PacketPlayOutPlayerListHeaderFooter(header, footer);

        craftPlayer.getHandle().playerConnection.sendPacket(headFoot);
    }
}
