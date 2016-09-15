/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils;

import com.google.common.base.Preconditions;
import net.minecraft.server.v1_8_R3.*;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class PacketHacks {
    private static final AtomicInteger CRACK_COUNTER = new AtomicInteger(0);
    private static final Map<BlockVector, Integer> CRACK_IDS = new HashMap<>();

    public PacketHacks() {
    }

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

    /** Send a message to the Action Bar */
    public static void sendActionBarMessage(Player player, String message) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        byte eight = (byte) 2;

        IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors(message)));
        craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(component, eight));
    }

    /** Set the tablist header and footer */
    public static void setTabListHeadFoot(Player player, String header, String footer) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(header);
        Preconditions.checkNotNull(footer);

        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent headTitle = IChatBaseComponent.ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors(header)));
        IChatBaseComponent footTitle = IChatBaseComponent.ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors(footer)));

        PacketPlayOutPlayerListHeaderFooter headFoot = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field head = headFoot.getClass().getDeclaredField("a");
            head.setAccessible(true);
            head.set(headFoot, headTitle);
            Field foot = headFoot.getClass().getDeclaredField("b");
            foot.setAccessible(true);
            foot.set(headFoot, footTitle);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {}

        craftPlayer.getHandle().playerConnection.sendPacket(headFoot);
    }

    public static void setTitle(Player player, String title, String sub) {
        setTitle(player, title, sub, 0, 60, 0);
    }

    public static void setTitle(Player player, String title, String sub, int fadeIn, int delay, int fadeOut) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        IChatBaseComponent headTitle = IChatBaseComponent.ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors(title)));
        IChatBaseComponent subTitle = IChatBaseComponent.ChatSerializer.a(Common.sanitize(MessageUtil.replaceColors(sub)));

        craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(fadeIn, delay, fadeOut));
        craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, headTitle));
        craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitle));
    }

    public static void title(Player player, String message, float percent) {
        percent = ((percent * 10) / 10) - 1;
        String sub = "....................................................................................................";
        setTitle(player, message, "&d" + sub.substring(0, (int) percent) + "&5" + sub.substring((int) percent, sub.length()));
    }

    public static void countTitle(Player player, String header, String time, float percent) {
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
            footer = footer.substring(0, percentInt <= 1 ? 2 : percentInt) + "&5" + footer.substring(percentInt <= 1 ? 2 : percentInt);
        }

        setTitle(player, "&a" + header, footer);
    }

    /** Crack the block with the given damage */
    public static void crackBlock(Block block, int damage) {
        BlockVector vector = block.getLocation().toVector().toBlockVector();
        CRACK_IDS.putIfAbsent(vector, CRACK_COUNTER.getAndIncrement());
        PacketPlayOutBlockBreakAnimation animation = new PacketPlayOutBlockBreakAnimation(
            CRACK_IDS.get(vector),
            new BlockPosition(block.getX(), block.getY(), block.getZ()),
            damage
        );

        Bukkit.getOnlinePlayers().parallelStream()
            .filter(player -> player.getLocation().distance(block.getLocation()) < 50)
            .map(player -> (CraftPlayer) player)
            .forEach(craftPlayer -> craftPlayer.getHandle().playerConnection.sendPacket(animation));
    }
}
