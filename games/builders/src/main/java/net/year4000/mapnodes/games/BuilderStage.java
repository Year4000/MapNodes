package net.year4000.mapnodes.games;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.scoreboard.SidebarManager;

public enum BuilderStage {
    PRE_GAME,
    BUILDING,
    VOTING,
    RESULTS,
    ;

    public void setSidebar(GamePlayer player) {

    }

    private SidebarManager getSidebarManager(GamePlayer player) {
        switch (this) {
            case BUILDING:
                return buildingSidebar(player);
            case VOTING:
                return null;
            case RESULTS:
                return null;
            default:
                return null;
        }
    }

    public SidebarManager buildingSidebar(GamePlayer player) {
        SidebarManager sidebar = new SidebarManager();

        return sidebar;
    }

    public SidebarManager votingSidebar(GamePlayer player) {
        SidebarManager sidebar = new SidebarManager();

        return sidebar;
    }

    public SidebarManager resultsSidebar(GamePlayer player) {
        SidebarManager sidebar = new SidebarManager();

        return sidebar;
    }
}
