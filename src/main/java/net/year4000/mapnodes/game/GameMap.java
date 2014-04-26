package net.year4000.mapnodes.game;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.year4000.mapnodes.configs.MapConfig;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.configs.map.Game;
import net.year4000.mapnodes.configs.map.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.year4000.mapnodes.game.GameManager.createListLocation;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "unchecked"})
public class GameMap {
    /** The name of the map. */
    private String name = "null";
    /** The version of the map. */
    private String version = "null";
    /** The authors of the map. */
    private List<String> authors = Arrays.asList("null");
    /** The description for the map. */
    private String description = "null";
    /** The time length of the game. */
    @Deprecated
    private int timeLimit;
    /** The list of damage causes to stop entity damage. */
    private List<EntityDamageEvent.DamageCause> noDamage;
    /** The list of enabled mobs to spawn in the world. */
    private List<EntityType> enabledMobs;
    /** The time the world should be locked to. */
    private long worldLock = -1;
    /** The difficulty that the map should be set to. */
    private int difficulty = 0;
    /** Can the map be destroyed. */
    private boolean destructible = true;
     /** Should the map always have weather on. Un for snowy maps. */
    private boolean forceWeather = false;
    /** The max height of the world. */
    private int worldHeight = -1;
    /** The resource pack url for this map. */
    private String resourcepack = "";
    /** Should tnt be activated when placed. */
    private boolean instantTNT = false;
    /** Should tnt cause block damage. */
    private boolean tntBlockDamage = true;
    /** The percent of blocks that tnt can yield. */
    private int tntYield = 80;
    /** The entity that should shot from bows. */
    private EntityType bowEntity = EntityType.ARROW;
    /** The velocity that the entity should be shot from. */
    private Double bowVelocity = 1.0;
    /** The blocks that the player can spawn at. */
    private List<Location> spawn = new ArrayList<>();
    /** The items that can be dropped from the player. */
    private List<ItemStack> enabledPlayerDrops = new ArrayList<>();
    /** Elemintation mode setting */
    private boolean elemintation = false;
    /** The lives the player should have. */
    private int lives = -1;

    protected GameMap(MapConfig config, World world) throws NullPointerException, IllegalArgumentException {
        final Map configMap = config.getMap();
        final Game configGame = config.getGame();

        // Settings from the map json config class.
        checkNotNull(configMap, Messages.get("error-json-map"));
        setName(checkNotNull(configMap.getName(), Messages.get("error-json-map-name")));
        setDescription(checkNotNull(configMap.getDescription(), Messages.get("error-json-map-description")));
        setVersion(checkNotNull(configMap.getVersion(), Messages.get("error-json-map-version")));
        setAuthors(new ArrayList() {{
            if (configMap.getAuthors() != null) {
                for (int i = 0; i < configMap.getAuthors().length; i++) {
                    checkArgument(configMap.getAuthors().length > 0, Messages.get("error-json-map-author"));
                    add(configMap.getAuthors()[i]);
                }
            }
        }});

        // Setting from the game json config class.
        checkNotNull(configGame, Messages.get("error-json-game"));
        setSpawn(createListLocation(world, configGame.getSpawn()));
        setNoDamage(new ArrayList<EntityDamageEvent.DamageCause>() {{
            for (int i = 0; i < configGame.getNoDamage().length; i++) {
                if (configGame.getNoDamage().length == 0) break;
                add(EntityDamageEvent.DamageCause.valueOf(configGame.getNoDamage()[i].toUpperCase()));
            }
        }});
        setEnabledMobs(new ArrayList<EntityType>() {{
            for (int i = 0; i < configGame.getEnabledMobs().length; i++) {
                if (configGame.getEnabledMobs().length == 0) break;
                EntityType entity = EntityType.valueOf(configGame.getEnabledMobs()[i].toUpperCase());
                add(entity);
            }
        }});
        setEnabledPlayerDrops(new ArrayList<ItemStack>() {{
            for (int i = 0; i < configGame.getEnabledPlayerDrops().length; i++) {
                if (configGame.getEnabledPlayerDrops().length == 0) break;
                String itemName = configGame.getEnabledPlayerDrops()[i].toUpperCase();
                add(new ItemStack(Material.valueOf(itemName)));
            }
        }});
        setTimeLimit(configGame.getTimeLimit());
        setWorldHeight(configGame.getWorldHeight());
        setDestructible(configGame.isDestructable());
        setWorldLock(configGame.getTimeLock());
        setForceWeather(configGame.isForceWeather());
        setInstantTNT(configGame.getTnts().isInstant());
        setTntBlockDamage(configGame.getTnts().isBlockDamage());
        setTntYield(configGame.getTnts().getDrops());
        setBowEntity(EntityType.valueOf(configGame.getBows().getEntity().toUpperCase()));
        setBowVelocity(configGame.getBows().getVelocity());
        setResourcepack(configGame.getResourcepack());
        setLives(configGame.getLives());
        setElemintation(configGame.isElemintation());
    }
}
