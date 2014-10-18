package net.year4000.mapnodes.game;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.AssignNodeGame;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.Validator;
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
import java.util.Set;

@Data
@NoArgsConstructor
/** Manage the items and effects that are given to the player. */
public class NodeKit implements GameKit, Validator, AssignNodeGame {
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
    @Since(2.0)
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

    private transient NodeGame game;
    @Setter(AccessLevel.NONE)
    private transient String id;
    public transient static final String DEFAULT_LEATHER = "A06540";

    /** Assign the game to this region */
    public void assignNodeGame(NodeGame game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    public String getId() {
        if (id == null) {
            NodeKit thisObject = this;

            game.getKits().forEach((string, object) -> {
                if (object.equals(thisObject)) {
                    id = string;
                }
            });
        }

        return id;
    }

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
        player.setArrowsStuck(0);
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
