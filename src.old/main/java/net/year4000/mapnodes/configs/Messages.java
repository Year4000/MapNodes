package net.year4000.mapnodes.configs;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.year4000.mapnodes.MapNodesPlugin;

import java.io.File;
import java.util.TreeMap;

@SuppressWarnings("unused")
public class Messages extends Config {
    public static final String ERROR = " &7[&e⚠&7] &c";

    private Messages(String locale) {
        // When local is not found default to messages
        locale = new File(MapNodesPlugin.getInst().getDataFolder(), locale + ".yml").exists() ? locale : "messages";
        //System.out.println(locale);
        /*try {
            CONFIG_HEADER = new String[] {"Messages for MapNodes"};
            CONFIG_FILE = new File(MapNodes.getInst().getDataFolder(), locale + ".yml");
            init();
        } catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }*/
    }

    /** Get this instance. */
    public static String get(String message) {
        return get("messages", message);
    }

    /** Get this instance while using the player's locale. */
    public static String get(String locale, String message) {
        //System.out.println(locale);
        Messages instance = new Messages(locale);

        // Check is the message is there if not use the general message.
        String newMessage;
        if (instance.messages.get(message) == null)
            newMessage = instance.messages.get("general");
        else
            newMessage = instance.messages.get(message);

        return MessageUtil.replaceColors(newMessage);
    }

    @Comment("All messages that can be changed.")
    private TreeMap<String, String> messages = new TreeMap<String, String>() {{
        put("error", ERROR);
        put("general", "Did I do that!");

        // Messages for clocks
        put("clock-start", "&6Starting &a%s&6 in &a%s&6.");
        put("clock-start-last", "&6&lGame started time on the clock!");
        put("clock-next", "&6Loading &a%s &6in &a%s&6.");
        put("clock-next-last", "&6&lChanging Map!");
        put("clock-restart", "&6Server restarting in &a%s&6.");
        put("clock-restart-last", "&6&lServer restarting!");
        put("clock-restart-kick", "&c&lServer restarting to change up the maps.");
        put("clock-dead", "&lYou are dead, sending to lobby!");
        put("clock-delay", "&6Joining the game in &a%s&6.");
        put("clock-delay-last", "&6&lYou have entered the game.");

        // Team and Classes
        put("game-join", "&aGame Joiner");
        put("team-gui-title", "&8&lJoin a Team!");
        put("team-gui-perm", "mapnodes.vip");
        put("team-gui-perm-message","&cOnly premium accounts can pick a team. \n&6&owww.year4000.net/page/shop");
        put("team-gui-join", "&6Click to join this team!");
        put("team-gui-join-random", "&6Click to join a random team!");
        put("team-full", "&cOnly premium accounts can join full teams. \n&6&owww.year4000.net/page/shop");
        put("team-join", "&7You have joined the team %s&7!");
        put("team-join-error", "&6You can not join a team, at this time.");
        put("class-gui-title", "&8&lPick a Class!");
        put("class-join", "&aYou have joined the &6%s &aclass!");
        put("class-join-error", "&6You can not join a class, at this time.");

        // Commands
        put("command-console", "&cMust be ran as a player!");
        put("command-team-unknown", "&cThere is no team by that name.");
        put("command-team-spectator", "&6You may only use this as a spectator.");
        put("command-team-player", "&6You may only use this when you are playing the game.");
        put("command-info-top", "&8&m************&6&l Game Stats &8&m************");
        put("command-info-map", "&7Map&8: &a%s &7version &a%s");
        put("command-info-authors", "&7Author(s)&8: &a%s");
        put("command-info-description", "&7Description&8: &a%s");
        put("command-info-time", "&7Start Time&8: &a%s");
        put("command-info-team-top", "&7 - &aTeams");
        put("command-info-team", "&a%s &7(%s&7) &ascore&8: &f%s");
        put("command-info-bottom", "&8&m**************************************");
        put("command-match-start", "&6You can only start a match is its has not started yet.");
        put("command-match-stop", "&6You can only stop a match if its running.");
        put("command-match-start-notice", "&6You are starting the match.");
        put("command-match-stop-notice", "&6You are ending the match.");
        put("command-node-generate-start", "&6Generating maps to load to server...");
        put("command-node-generate-end", "&6Maps loaded to server (&e%s&6ms)");
        put("command-node-noWorld", "World does not exist in folder.");
        put("command-node-future", "You can only manage future maps.");
        put("command-node-out", "Can not manage maps not in scope.");
        put("command-node-loaded", "&6Map &e'%s' &6and now can be played.");
        put("command-node-notLoaded", "Could not load world check console.");
        put("command-node-removed", "&6Map &e'%s' &6removed from node rotation.");
        put("command-node-moved", "&6Map &e'%s' &6has been moved.");

        // Game Messages
        put("game-login", "&aYou are currently on &2%s&a, version &2%s&a by &2%s&a.");
        put("game-start-top", "&8&m***********&a&l Game Started &8&m**********");
        put("game-start-map", "&7Map&8: &a%s &7version &a%s");
        put("game-start-authors", "&7Author(s)&8: &a%s");
        put("game-start-description", "&7Description&8: &a%s");
        put("game-start-bottom", "&8&m***************************************");
        put("game-end-top", "&8&m*************&c&l Game Ended &8&m***********");
        put("game-end-team", "&7Your team was&8: %s");
        put("game-end-team-score", "&7Your team score was&8: &a%s");
        put("game-end-player-score", "&7Your score was&8: &a%s");
        put("game-end-bottom", "&8&m****************************************");
        put("game-height-max", ERROR + "You have reached the world height of &6%s&c.");
        put("game-life", "&cYou died, You have &6%s&c lives left.");
        put("game-life-dead", "&cYou ran out of lives and been set to spectator.");
        put("game-elimination", "&a%s &chas been eliminated.");

        // Error checking
        put("error-null", ERROR + "This may not be null.");
        put("error-games-none", ERROR + "Could not load any maps, disabling plugin.");
        put("error-json-effect-name", ERROR + "You must supply a name for the effect.");
        put("error-json-effect-length", ERROR + "You must supply a length for the effect.");
        put("error-json-item-name", ERROR + "You must supply a name for the item.");
        put("error-json-spawn", ERROR + "You must include at least one spawn point.");
        put("error-json-team-name", ERROR + "You must set the team name.");
        put("error-json-team-color", ERROR + "You must set the team color.");
        put("error-json-team-size", ERROR + "You must set the team max size.");
        put("error-json-team-one", ERROR + "You must have at lease one teams.");
        put("error-json-team-kit", ERROR + "You must have a proper kit for this team.");
        put("error-json-map", ERROR + "You must define a map section.");
        put("error-json-map-name", ERROR + "The map must have a name!");
        put("error-json-map-version", ERROR + "The map must have a version!");
        put("error-json-map-description", ERROR + "The map must have a description!");
        put("error-json-map-author", ERROR + "You must have at least one author");
        put("error-json-game", ERROR + "You must have a game section.");
        put("error-json-class-name", ERROR + "You must have a name for a class.");
        put("error-json-class-icon", ERROR + "You must have give the class an icon.");
        put("error-json-class-description", ERROR + "You must have a description for the class.");
        put("error-json-class-kit", ERROR + "You must have a proper kit for this class.");
    }};
}
