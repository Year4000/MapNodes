/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.Since;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.typewrappers.PlayerArmorList;
import net.year4000.mapnodes.utils.typewrappers.PlayerInventoryList;
import net.year4000.mapnodes.utils.typewrappers.PotionEffectList;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
/** Manage the items and effects that are given to the player. */
public class NodeKit implements GameKit {
    /** The parents the kit will inherit */
    @Since(2.0)
    protected List<String> parents = new ArrayList<>();
    /** The items to put in the player's inventory. */
    @Since(1.0)
    protected PlayerInventoryList<ItemStack> items = new PlayerInventoryList<>();
    /** The potion effects to add to the player. */
    @Since(1.0)
    protected PotionEffectList<PotionEffect> effects = new PotionEffectList<>();
    /** The armor for the player. */
    @Since(1.0)
    protected PlayerArmorList<ItemStack> armor = new PlayerArmorList<>();
    /** The kits game mode. */
    @Since(1.0)
    protected GameMode gamemode = GameMode.SURVIVAL;
    /** The kits's health level */
    @Since(1.0)
    protected int health = 20;
    /** The kits's food level */
    @Since(1.0)
    protected int food = 20;
    /** The kits permissions */
    @Since(2.0)
    protected List<String> permissions = new ArrayList<>();

    @Override
    public void validate() throws InvalidJsonException {
        if (health < 1) {
            throw new InvalidJsonException(Msg.util("settings.kit.health"));
        }

        if (food < 0) {
            throw new InvalidJsonException(Msg.util("settings.kit.food"));
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    public transient static final String DEFAULT_LEATHER = "A06540";
    @Getter(lazy = true)
    private final transient String id = id();
    /** Can this kit fly */
    @Since(1.0)
    protected boolean fly = false;
    private transient GameManager game;

    /** Reset the player to default settings */
    public static void reset(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();

        gamePlayer.getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));

            // Clear Items
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.getInventory().setContents(new ItemStack[35]);

            // noinspection deprecation
            player.updateInventory();
        }));

        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.setFireTicks(0);
        player.setFallDistance(0);
        // todo Spigot player.setArrowsStuck(0);
    }

    /** Immortal starter kit */
    public static BukkitTask immortal(Player player) {
        Clocker immortal = new Clocker(MathUtil.ticks(10)) {
            Set<PotionEffectType> types = ImmutableSet.of(
                PotionEffectType.DAMAGE_RESISTANCE,
                PotionEffectType.NIGHT_VISION,
                PotionEffectType.REGENERATION,
                PotionEffectType.FIRE_RESISTANCE
            );

            @Override
            public void runFirst(int position) {
                player.getActivePotionEffects().stream()
                    .filter(p -> p.getDuration() != Integer.MAX_VALUE)
                    .map(PotionEffect::getType)
                    .filter(types::contains)
                    .forEach(player::removePotionEffect);

                for (PotionEffectType type : types) {
                    player.addPotionEffect(new PotionEffect(type, getTime(), 10, true));
                }
                player.setPlayerTime(16000, false);
            }

            @Override
            public void runTock(int position) {
                // If effects were not applied some how just reset player time.
                if (!player.getActivePotionEffects().stream().map(PotionEffect::getType).collect(Collectors.toList()).containsAll(types)) {
                    for (PotionEffectType type : types) {
                        player.addPotionEffect(new PotionEffect(type, getTime(), MathUtil.sec(position), true));
                    }
                }

                player.setExp(MathUtil.percent(getTime(), position) / 100);
            }


            @Override
            public void runLast(int position) {
                player.resetPlayerTime();
            }
        };

        return immortal.run();
    }

    /** Assign the game to this region */
    public void assignNodeGame(GameManager game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    private String id() {
        NodeKit thisObject = this;

        for (Map.Entry<String, GameKit> entry : game.getKits().entrySet()) {
            if (thisObject.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Can not find the id of " + this.toString());
    }

    /** Give this kit to the player */
    public void giveKit(GamePlayer player) {
        Player rawPlayer = player.getPlayer();
        reset(player);

        player.getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            rawPlayer.getInventory().setContents(this.items.toArray(new ItemStack[this.items.size()]));

            // Color the armor
            List<ItemStack> items = new ArrayList<>();
            armor.forEach(item -> {
                item = item.clone();

                if (item.getItemMeta() instanceof LeatherArmorMeta) {
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

                    // Make sure the armor is not colored previously
                    if (meta.getColor().equals(Color.fromRGB(Integer.valueOf(DEFAULT_LEATHER, 16)))) {
                        meta.setColor(player.getTeam().getRawColor());
                        item.setItemMeta(meta);
                    }
                }

                items.add(item);
            });

            rawPlayer.getInventory().setArmorContents(items.toArray(new ItemStack[items.size()]));

            effects.forEach(effect -> rawPlayer.addPotionEffect(effect, true));

            // noinspection deprecation
            rawPlayer.updateInventory();
        }, 1L));

        rawPlayer.setMaxHealth(health);
        rawPlayer.setHealth(health);
        rawPlayer.setFoodLevel(food);
        rawPlayer.setGameMode(gamemode);
        rawPlayer.setAllowFlight(fly);
        rawPlayer.setFlying(fly);
    }

    @Override
    public List<ItemStack> getNonAirItems() {
        return items.getNonAirItems();
    }

    public void addKit(GamePlayer player) {
        Player rawPlayer = player.getPlayer();

        player.getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            Inventory inv = player.getPlayer().getInventory();
            // Apply the items to the player if it has a slot set that slot if not just add it
            items.getRawItems().forEach(slotItem -> {
                if (slotItem.getSlot() == -1) {
                    inv.addItem(slotItem.create());
                }
                else {
                    inv.setItem(slotItem.getSlot(), slotItem.create());
                }
            });

            rawPlayer.updateInventory();
        }, 1L));

        rawPlayer.setMaxHealth(health);
        rawPlayer.setHealth(health);
        rawPlayer.setFoodLevel(food);
        rawPlayer.setGameMode(gamemode);
        rawPlayer.setAllowFlight(fly);
        rawPlayer.setFlying(fly);
    }
}
