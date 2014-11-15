package net.year4000.mapnodes.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BadgeManager {
    public static final int MAX_RANK = 6;

    @AllArgsConstructor
    public enum Badges {
        ALPHA(ChatColor.DARK_AQUA, "α", "alpha", 1),
        THETA(ChatColor.GRAY, "Θ", "theta", 2),
        MU(ChatColor.YELLOW, "μ", "mu", 3),
        PI(ChatColor.AQUA, "π", "pi", 4),
        SIGMA(ChatColor.GOLD, "σ", "sigma", 5),
        OMEGA(ChatColor.RED, "Ω", "omega", 6),
        /* DIFF CHECKER */;

        @Getter
        private ChatColor color;
        @Getter
        private String badge, permission;
        @Getter
        private int rank;

        @Override
        public String toString() {
            return MessageUtil.replaceColors(color + badge);
        }
    }

    /** Find the badge the player should have */
    public Badges findBadge(Player player) {
        List<Badges> ranks = Arrays.asList(Badges.values());
        Collections.reverse(ranks);

        for (Badges badge : ranks) {
            if (player.hasPermission(badge.getPermission())) {
                return badge;
            }
        }

        return Badges.ALPHA;
    }

    /** Get the badge in bracket form */
    public String getBadge(Player player) {
        Badges badge = findBadge(player);
        return MessageUtil.replaceColors("&f[" + badge + "&f]");
    }
}