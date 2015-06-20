/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.commands.mapnodes;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.messages.MessageManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.pagination.SimplePaginatedResult;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public final class MapNodesSub {
    @Command(
        aliases = {"git"},
        desc = "View info about the git version."
    )
    @CommandPermissions({"omega", "mapnodes.admin", "mapnodes.*"})
    public static void git(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(Msg.locale(sender, "cmd.git.build"));
        gitQuickSend(sender, "cmd.git.name", "git.build.user.name");
        gitQuickSend(sender, "cmd.git.email", "git.build.user.email");
        gitQuickSend(sender, "cmd.git.time", "git.build.time");
        sender.sendMessage(Msg.locale(sender, "cmd.git.commit"));
        gitQuickSend(sender, "cmd.git.remote", "git.remote.origin.url");
        gitQuickSend(sender, "cmd.git.name", "git.commit.user.name");
        gitQuickSend(sender, "cmd.git.email", "git.commit.user.email");
        gitQuickSend(sender, "cmd.git.time", "git.commit.time");
        gitQuickSend(sender, "cmd.git.commit.branch", "git.branch");
        gitQuickSend(sender, "cmd.git.commit.id", "git.commit.id");
        gitQuickSend(sender, "cmd.git.commit.message", "git.commit.message.short");
    }

    @Command(
        aliases = {"locale", "language"},
        desc = "View info about you locale and available locales"
    )
    public static void locale(CommandContext args, CommandSender sender) throws CommandException {
        localeQuickSend(sender, "cmd.mapnodes.locale.code", "locale.code");
        localeQuickSend(sender, "cmd.mapnodes.locale.name", "locale.name");

        if (sender.hasPermission("omega") || sender.hasPermission("mapnodes.admin") || sender.hasPermission("mapnodes.*")) {
            sender.sendMessage(Msg.locale(sender, "cmd.mapnodes.locale.locales"));
            MessageManager.get().getLocales().keySet().stream()
                .map(Locale::toString)
                .filter(locale -> locale.contains("_"))
                .forEach(sender::sendMessage);
        }
    }

    @Command(
        aliases = {"addons", "addon"},
        desc = "View loaded add-ons."
    )
    public static void addon(CommandContext args, CommandSender sender) throws CommandException {
        final int MAX_PER_PAGE = 8;

        new SimplePaginatedResult<AddonInfo>(null, MAX_PER_PAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                return Msg.locale(sender, "cmd.addon.header", String.valueOf(page), String.valueOf(maxPages));
            }

            @Override
            public String format(AddonInfo addonInfo, int i) {
                return (i + 1) + " - " + Msg.locale(sender, "cmd.addon.list",
                    addonInfo.name(),
                    Common.formatSeparators(addonInfo.version(), ChatColor.GREEN, ChatColor.DARK_GRAY),
                    Common.shortMessage(25, addonInfo.description()));
            }
        }.display(
            new BukkitWrappedCommandSender(sender),
            MapNodesPlugin.getInst().getAddons().builder().getInfos(),
            args.argsLength() == 1 ? args.getInteger(0) : 1
        );
    }

    /** Simple the git message output */
    private static void gitQuickSend(CommandSender sender, String head, String body) {
        sender.sendMessage(Msg.locale(sender, head, Msg.git(body)));
    }

    /** Simple the locale message output */
    private static void localeQuickSend(CommandSender sender, String head, String body) {
        sender.sendMessage(Msg.locale(sender, head, Msg.locale(sender, body)));
    }
}
