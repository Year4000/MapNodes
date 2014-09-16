package net.year4000.mapnodes.game.system;

import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.utils.typewrappers.PlayerArmorList;
import net.year4000.mapnodes.utils.typewrappers.PlayerInventoryList;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpectatorKit extends NodeKit {
    public SpectatorKit() {
        setFly(true);

        setGamemode(GameMode.ADVENTURE);

        getEffects().add(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2));

        setItems(new PlayerInventoryList<ItemStack>() {{
            for (int i = 0; i < 36; i++) {
                add(ItemUtil.makeItem("air"));
            }
        }});

        setArmor(new PlayerArmorList<>());
    }
}
