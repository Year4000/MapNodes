/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.backend;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.utilities.AbstractBadgeManager;
import net.year4000.utilities.MessageUtil;

public class MapNodesBadgeManager extends AbstractBadgeManager<GamePlayer> {
    @Override
    public Badges findBadge(GamePlayer player) {
        return Badges.valueOf(((NodePlayer) player).getCache().getRank().toUpperCase());
    }

    @Override
    public String getBadge(GamePlayer player) {
        Badges badge = findBadge(player);
        return MessageUtil.replaceColors("&f[" + badge.getColor() + badge.getBadge() + "&f]");
    }
}
