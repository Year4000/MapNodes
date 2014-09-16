package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import net.year4000.mapnodes.utils.typewrappers.PlayerArmorList;
import net.year4000.mapnodes.utils.typewrappers.PlayerInventoryList;
import net.year4000.mapnodes.utils.typewrappers.PotionEffectList;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
/** Manage the items and effects that are given to the player. */
public class NodeKit implements GameKit, Validator {
    /** The items to put in the player's inventory. */
    @Since(1.0)
    private PlayerInventoryList<ItemStack> items = new PlayerInventoryList<>();

    /** The potion effects to add to the player. */
    @Since(1.0)
    private PotionEffectList<PotionEffect> effects = new PotionEffectList<>();

    /** The armor for the player. */
    @Since(1.0)
    private PlayerArmorList<ItemStack> armor = new PlayerArmorList<>();

    /** The kits game mode. */
    @Since(1.0)
    private GameMode gamemode = GameMode.SURVIVAL;

    /** The kits's health level */
    @Since(1.0)
    private int health = 20;

    /** The kits's food level */
    @Since(1.0)
    private int food = 20;

    /** The kits permissions */
    @Since(1.0)
    private List<String> permissions = new ArrayList<>();

    /** Can this kit fly */
    @Since(1.0)
    private boolean fly = false;

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

    private transient static final String DEFAULT_LEATHER = "A06540";

    /** Reset the player to default settings */
    public static void reset(Player player) {
        SchedulerUtil.runSync(() -> {
            player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));

            // noinspection deprecation
            player.updateInventory();
        });

        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setArrowsStuck(0);
    }

    /** Give this kit to the game player */
    public void giveKit(GamePlayer player) {
        giveKit(player.getPlayer());

        // todo assign the task to the player's list of tasks
        SchedulerUtil.runSync(() -> {
            List<ItemStack> items = new ArrayList<>(armor);
            items.forEach(item -> {
                if (item.getItemMeta() instanceof LeatherArmorMeta) {
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

                    // Make sure the armor is not colored previously
                    if (meta.getColor().equals(Color.fromRGB(Integer.valueOf(DEFAULT_LEATHER, 16)))) {
                        meta.setColor(player.getTeam().getRawColor());
                        item.setItemMeta(meta);
                    }
                }
            });

            player.getPlayer().getInventory().setArmorContents(items.toArray(new ItemStack[items.size()]));
        }, 2L);
    }

    /** Give this kit to the player */
    public void giveKit(Player player) {
        reset(player);

        // todo assign the task to the player's list of tasks
        SchedulerUtil.runSync(() -> {
            player.getInventory().setContents(items.toArray(new ItemStack[items.size()]));
            player.getInventory().setArmorContents(armor.toArray(new ItemStack[armor.size()]));

            effects.forEach(effect -> player.addPotionEffect(effect, true));

            // noinspection deprecation
            player.updateInventory();
        }, 1L);

        player.setMaxHealth(health);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setGameMode(gamemode);
        player.setAllowFlight(fly);
        player.setFlying(fly);
    }

    /** Immortal starter kit */
    public static List<BukkitTask> immortal(Player player) {
        // todo handle if the player all rdy has potion effect
        Clocker immortal = new Clocker(MathUtil.ticks(10)) {
            PotionEffectType[] types = {
                PotionEffectType.DAMAGE_RESISTANCE,
                PotionEffectType.NIGHT_VISION,
                PotionEffectType.REGENERATION,
                PotionEffectType.FIRE_RESISTANCE
            };

            @Override
            public void runFirst(int position) {
                for (PotionEffectType type : types) {
                    player.addPotionEffect(new PotionEffect(type, getTime(), 10, true));
                }
                player.setPlayerTime(16000, false);
            }

            @Override
            public void runTock(int position) {
                player.setExp(MathUtil.percent(getTime(), position)/100);
            }


            @Override
            public void runLast(int position) {
                player.resetPlayerTime();
            }
        };

        return immortal.run();
    }
}
