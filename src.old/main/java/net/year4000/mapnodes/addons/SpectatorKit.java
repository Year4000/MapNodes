package net.year4000.mapnodes.addons;

import com.google.gson.Gson;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.configs.map.Kits;
import net.year4000.mapnodes.game.GameKit;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class SpectatorKit extends GameKit {
    public SpectatorKit() {
        Gson gson = new Gson();
        // TODO: Grab the info from the config file.
        PotionEffect hide = new PotionEffect(
            PotionEffectType.INVISIBILITY,
            999999,
            1,
            true
        );
        setFood(20);
        setHealth(20);
        setGamemode(GameMode.ADVENTURE);
        setFly(true);
        getItemKit().put(0, loadItem(gson.fromJson("{\"item\": \"EYE_OF_ENDER\", \"nbt\":{display:{name:\"" + Messages.get("game-join")+"\", lore:[\"&6Open the menu to pick a\", \"&6team and join the game!\"]}}}", Kits.Items.class)));
        getItemKit().put(8, loadItem(gson.fromJson("{\"item\": \"enchanted_book\", \"nbt\":{display:{name:\"&aGame Servers\", lore:[\"&6Open a menu with\",\"&6the game servers!\"]}}}", Kits.Items.class)));
        getEffectKit().add(hide);
    }
}
