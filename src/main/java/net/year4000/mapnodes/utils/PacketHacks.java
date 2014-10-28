package net.year4000.mapnodes.utils;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.Packet;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

import java.util.concurrent.TimeUnit;

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
        if (!isTitleAble(player)) return;

        CraftPlayer craftPlayer = (CraftPlayer) player;

        Packet headFoot = new ProtocolInjector.PacketPlayOutPlayerListHeaderFooter(
            Common.sanitize(header),
            Common.sanitize(footer)
        );

        craftPlayer.getHandle().playerConnection.sendPacket(headFoot);
    }

    /** Send BossBar or new Title Packets when client is 1.8 */
    public static boolean isTitleAble(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47;
    }

    public static void title(Player player, String message, float percent) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        // Boss bar
        if (!isTitleAble(player)) {
            BossBar.setMessage(player, message, percent);
        }
        // Title bar
        else {
            percent = ((percent * 10) / 10) - 1;
            String sub = "....................................................................................................";
            IChatBaseComponent title = ChatSerializer.a(Common.sanitize(message));
            IChatBaseComponent subtitle = ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors("&d" + sub.substring(0, (int) percent) + "&5" + sub.substring((int) percent, sub.length()))));

            craftPlayer.getHandle().playerConnection.sendPacket(new PacketInjector.PacketTitle(PacketInjector.PacketTitle.Action.TIMES, 0, -1, 1));
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketInjector.PacketTitle(PacketInjector.PacketTitle.Action.TITLE, title));
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketInjector.PacketTitle(PacketInjector.PacketTitle.Action.SUBTITLE, subtitle));
        }
    }

    public static void countTitle(Player player, String header, String time, float percent) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        if (isTitleAble(player)) {
            int percentInt = (int) (((percent * 10) / 10) * .80);
            String bar = Common.textLine(time, 80, '.', "&d", "");
            String footer;

            // If percent is bigger than no no zone print normal
            if (percentInt > 43 + time.length()) {
                footer = bar.substring(0, percentInt) + "&5" + bar.substring(percentInt);
            }
            // Don't show position in clock
            else if (percentInt > 40 && percentInt < 44 + time.length()) {
                footer = bar.substring(0, 43 + time.length()) + "&5" + bar.substring(43 + time.length());
            }
            else {
                footer = bar.substring(0, 43 + time.length()) + "&5" + bar.substring(43 + time.length());
                footer = footer.substring(0, percentInt <= 1 ? 2 : percentInt) + "&5" + footer.substring( percentInt <= 1 ? 2 : percentInt);
            }

            IChatBaseComponent title = ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors("&a" + header)));
            IChatBaseComponent subtitle = ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors(footer)));

            craftPlayer.getHandle().playerConnection.sendPacket(new PacketInjector.PacketTitle(PacketInjector.PacketTitle.Action.TIMES, 0, -1, 1));
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketInjector.PacketTitle(PacketInjector.PacketTitle.Action.TITLE, title));
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketInjector.PacketTitle(PacketInjector.PacketTitle.Action.SUBTITLE, subtitle));

        }
    }
}
