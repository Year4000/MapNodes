package net.year4000.mapnodes.utils;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.wepif.PermissionsResolverManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PlayerBadges {
    /** Get the player badge depending on known ranks. */
    public static String getBadge(Player player) {
        PermissionsResolverManager p = PermissionsResolverManager.getInstance();
        List<String> ranks = Arrays.asList(p.getGroups(player));

        String bage = "%s";
        String icon;

        if (ranks.contains("OPMode")) icon = "&4Ω";
        else if (ranks.contains("StaffMode")) icon = "&6Ω";
        else if (ranks.contains("Staff")) icon = "&cΩ";
        else if (ranks.contains("VIP")) icon = "&6σ";
        else if (ranks.contains("Diamond")) icon = "&bπ";
        else if (ranks.contains("Gold")) icon = "&eμ";
        else if (ranks.contains("Iron")) icon = "&7Θ";
        else icon = "&3α";

        return MessageUtil.replaceColors(bage.replaceAll("%s", icon));
    }
}
