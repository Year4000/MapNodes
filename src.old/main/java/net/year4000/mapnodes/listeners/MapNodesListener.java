package net.year4000.mapnodes.listeners;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.utils.ClassException;
import net.year4000.mapnodes.utils.TeamException;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

/** Controls aspects of the game that belong to its self. */
@SuppressWarnings("unused")
public class MapNodesListener implements Listener {

    /** Register its self. */
    public MapNodesListener() {
        Bukkit.getPluginManager().registerEvents(this, MapNodesPlugin.getInst());
    }

    /** Add the player to the system */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(MapNodesPlugin.getInst(), () -> GamePlayer.join(event.getPlayer()));
    }

    /** Remove player from system. */
    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        WorldManager.get().getCurrentGame().getPlayer(event.getPlayer()).leave();
    }

    /** Ping event allows for current map stats. */
    @EventHandler
    public void onPing(ServerListPingEvent event) {
        try {
            GameManager gm = WorldManager.get().getCurrentGame();

            event.setMaxPlayers(gm.getGameMaxSize());
            event.setNumPlayers(gm.getGameSize());
            String motd = String.format(
                "%s%s &7| &5&o%s",
                GameStage.getStageColor(),
                gm.getStage().name(),
                gm.getMap().getName()
            );
            event.setMotd(MessageUtil.replaceColors(motd));
            if (gm.getIcon() != null) event.setServerIcon(gm.getIcon());
        } catch (Exception e) {/* Server is not ready*/}
    }

    /** Team picker GUI */
    @EventHandler
    public void onTeamPicker(InventoryClickEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        Player player = (Player)event.getWhoClicked();
        GamePlayer gPlayer = gm.getPlayer(player);

        if (event.getInventory().getName().equals(Messages.get("team-gui-title"))) {
            try {
                String team = event.getCurrentItem().getItemMeta().getDisplayName().toUpperCase();

                if (team.equals("RANDOM"))
                    gm.getTeams().get(gm.getSmallestTeam()).join(gPlayer, true);
                else
                    gm.getTeams().get(team).join(gPlayer);

                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                player.closeInventory();

                // If there is kits ask player for a kit
                if (gm.getTeamClasses().size() > 0) {
                    Bukkit.getScheduler().runTask(MapNodesPlugin.getInst(), () -> player.openInventory(GameClass.getClassesGUI()));
                }
            } catch (TeamException e) {
                player.sendMessage(MessageUtil.replaceColors(e.getMessage()));
            } catch (Exception e) {/*Left Blank as this will happen if item does not exist. */}
            event.setCancelled(true);
        }
    }

    /** Classes GUI */
    @EventHandler
    public void onClassesPicker(InventoryClickEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        Player player = (Player)event.getWhoClicked();
        GamePlayer gPlayer = gm.getPlayer(player);

        //MapNodes.log(event.getInventory().getName());
        if (event.getInventory().getName().equals(Messages.get("class-gui-title"))) {
            try {
                String classes = event.getCurrentItem().getItemMeta().getDisplayName().toUpperCase();

                //System.out.println(gm.getTeamClasses().toString());
                //System.out.println("ITEM: " + classes);

                gm.getTeamClasses().get(classes).give(gPlayer);

                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                player.closeInventory();
            } catch (ClassException e) {
                player.sendMessage(MessageUtil.replaceColors(e.getMessage()));
            } catch (Exception e) {/*Left Blank as this will happen if item does not exist. */}
            event.setCancelled(true);
        }
    }

    /** The item detector to join the game. */
    @EventHandler
    @SuppressWarnings("deprecation")
    public void onGameJoiner(PlayerInteractEvent event) {
        boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;
        boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;

        if (rightAir || rightBlock) {
            ItemStack hand = event.getPlayer().getItemInHand();
            Player player = event.getPlayer();

            if (!hand.hasItemMeta()) return;
            if (!hand.getItemMeta().hasDisplayName()) return;

            if (hand.getItemMeta().getDisplayName().equals(Messages.get("game-join"))) {
                if (!WorldManager.get().getCurrentGame().getPlayer(event.getPlayer()).isSpecatator())
                    event.getPlayer().sendMessage(Messages.get(player.getLocale(), "error") + Messages.get(player.getLocale(), "command-team-spectator"));
                else if (GameStage.isEndGame())
                    event.getPlayer().sendMessage(Messages.get(player.getLocale(), "error") + Messages.get(player.getLocale(), "team-join-error"));
                else
                    event.getPlayer().openInventory(GameTeam.getTeamsGUI());

                event.setCancelled(true);
                Bukkit.getScheduler().runTask(MapNodesPlugin.getInst(), () -> event.getPlayer().updateInventory());
            }
        }
    }

    /** Disable player achievements */
    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

}
