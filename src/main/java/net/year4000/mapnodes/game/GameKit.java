package net.year4000.mapnodes.game;

import com.ewized.utilities.bukkit.util.ItemUtil;
import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.MapConfig;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.configs.map.Kits;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class GameKit {
    private final String DEFAULT_LEATHER = "A06540";
    /** The storage of what items the player should get. */
    private HashMap<Integer, ItemStack> itemKit = new HashMap<>();
    /** The armor storage. */
    private ItemStack[] armor = new ItemStack[4];
    /** The storage of the effects. */
    private List<PotionEffect> effectKit = new ArrayList<>();
    /** The kits game mode. */
    private GameMode gamemode = GameMode.SURVIVAL;
    /** The kits's health level */
    private int health = 20;
    /** The kits's food level */
    private int food = 20;
    /** The kits permissions */
    private String[] permissions = new String[] {};
    /** Can this kit fly */
    private boolean fly = false;

    public GameKit(String kit, MapConfig config) throws NullPointerException {
        Kits kits = config.getKits().get(kit);

        // Load parents
        for (String parent : kits.getInherient()) {
            Kits parentKit = config.getKits().get(parent);
            GameKit temp = new GameKit(parent, config);

            // Checks do not set if null.
            if (temp.getItemKit().size() > 0)
                getItemKit().putAll(temp.getItemKit());
            if (temp.getEffectKit().size() > 0)
                getEffectKit().addAll(temp.getEffectKit());
            if (temp.getArmor().length > 0)
                setArmor(getArmor());

            // Setting that can not be added to but has to be overwritten
            setHealth(parentKit.getHealth());
            setFly(parentKit.isFly());
            setGamemode(checkNotNull(GameMode.valueOf(parentKit.getGamemode().toUpperCase())));
            setFood(parentKit.getFood());
        }

        // Load self
        if (kits.getItems() != null)
            loadItems(kits.getItems());
        if (kits.getEffects() != null)
            loadEffects(kits.getEffects());
        if (kits.getArmor() != null)
            loadArmor(kits.getArmor());

        // Setting that can not be added to but has to be overwritten
        setHealth(kits.getHealth());
        setFly(kits.isFly());
        setGamemode(checkNotNull(GameMode.valueOf(kits.getGamemode().toUpperCase())));
        setFood(kits.getFood());

    }

    /** Load the item kits into memory. */
    public void loadItems(Kits.Items... items) throws NullPointerException {
        if (items == null) return;
        for (Kits.Items item : items) {
            getItemKit().put(item.getSlot() == null ? 0 : item.getSlot(), loadItem(item));
        }
    }

    /** Load the effects into the kits. */
    public void loadEffects(Kits.Effects... effects) throws NullPointerException {
        if (effects == null) return;
        for (Kits.Effects effect : effects) {
            int length = checkNotNull(effect.getDuration(), Messages.get("error.json.effect.length"));
            getEffectKit().add(new PotionEffect(
                checkNotNull(
                    PotionEffectType.getByName(effect.getName().toUpperCase()),
                    Messages.get("error.json.effect.name")
                ),
                length < 0 ? 99999 : length * 20,
                effect.getAmplifier(),
                effect.isAmbient()
            ));
        }
    }

    /** Load the armor into the kits. */
    public ItemStack loadItem(Kits.Items item) {
        checkNotNull(item, Messages.get("error.json.item.name"));

        Gson gson = new Gson();

        ItemStack itemStack = new ItemStack(
            checkNotNull(
                Material.valueOf(item.getItem().toUpperCase()),
                Messages.get("error.json.item.name")
            ),
            item.getAmount() == null ? 1 : item.getAmount(),
            item.getDamage() == null ? 0 : item.getDamage()
        );

        // Only add nbt if the items has nbt
        if (item.getNbt() != null) {
            itemStack.setItemMeta(ItemUtil.addMeta(
                itemStack,
                gson.toJson(item.getNbt())
            ));
        }

        return itemStack;
    }

    /** Load the armor. */
    public void loadArmor(Kits.Armor armor) {
        if (armor.getHelmet() != null)
            getArmor()[3] = loadItem(armor.getHelmet());
        if (armor.getChestplate() != null)
            getArmor()[2] = loadItem(armor.getChestplate());
        if (armor.getLeggings() != null)
            getArmor()[1] = loadItem(armor.getLeggings());
        if (armor.getBoots() != null)
            getArmor()[0] = loadItem(armor.getBoots());
    }

    /** Give this kit to a player. */
    public void giveKit(GamePlayer gPlayer) {
        Player player = gPlayer.getPlayer();
        ItemStack[] inv = new ItemStack[36];
        for (Map.Entry<Integer, ItemStack> item : getItemKit().entrySet())
            inv[item.getKey() > 36 ? 36 : item.getKey()] = item.getValue();

        // Default normal leather armor to team's color
        ItemStack[] playerArmor = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            if (getArmor()[i] == null) continue;

            if (getArmor()[i].getItemMeta() instanceof LeatherArmorMeta) {
                playerArmor[i] = getArmor()[i].clone();
                LeatherArmorMeta meta = (LeatherArmorMeta)getArmor()[i].getItemMeta();

                // Make sure the armor is not colored previously
                if (meta.getColor().equals(Color.fromRGB(Integer.valueOf(DEFAULT_LEATHER, 16)))) {
                    meta.setColor(gPlayer.getTeam().getColor());
                    playerArmor[i].setItemMeta(meta);
                }
            }
        }

        // Reset the player
        reset(player);

        Bukkit.getScheduler().runTaskLater(MapNodes.getInst(), () -> {
            PlayerInventory playerInv = player.getInventory();

            // Effects
            for (PotionEffect effect : getEffectKit())
                player.addPotionEffect(effect, true);

            // Armor default leather get dye to the teams color
            playerInv.setArmorContents(playerArmor);

            // Items
            playerInv.setContents(inv);

            // Food and health
            player.setMaxHealth(getHealth());
            player.setHealth(getHealth());

            // Game mode / flying
            player.setGameMode(getGamemode());
            player.setAllowFlight(isFly());
            player.setFlying(isFly());

            // Permissions
            // TODO ALLOW PLAYERS TO GET PERMISSIONS
        }, 2L /* One tick after the tick delay to reset the player */);
    }

    /** Give the kit to the player. */
    public void giveKit(Player player) {
        giveKit(WorldManager.get().getCurrentGame().getPlayer(player));
    }

    /** Reset the player */
    public static void reset(Player player) {
        // Settings that must be ran a tick later
        Bukkit.getScheduler().runTask(MapNodes.getInst(), () -> {
            // Clear items / armor
            player.getInventory().clear();
            //player.getInventory().setArmorContents(null);

            // Remove effects so we can reset them.
            for (PotionEffect potion : player.getActivePotionEffects())
                player.removePotionEffect(potion.getType());
        });

        // Minecraft default settings
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.resetMaxHealth();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setFallDistance(0);

        // Settings based on gamemode
        if (Bukkit.getDefaultGameMode() == GameMode.CREATIVE) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        player.setGameMode(Bukkit.getDefaultGameMode());
    }
}
