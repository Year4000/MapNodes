package net.year4000.mapnodes.utils;

import net.minecraft.server.v1_7_R4.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

public final class PacketHacks {
    /** Re-spawn a dead player */
    public static void respawnPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().moveToWorld(craftPlayer.getHandle(), 0, false);
    }

    /** Set the tablist header and footer */
    public static void setTabListHeadFoot(Player player, String header, String footer) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        Packet headFoot = new ProtocolInjector.PacketPlayOutPlayerListHeaderFooter(header, footer);

        craftPlayer.getHandle().playerConnection.sendPacket(headFoot);
    }
}
