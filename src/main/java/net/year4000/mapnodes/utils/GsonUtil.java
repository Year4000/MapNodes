package net.year4000.mapnodes.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.mapnodes.utils.deserializers.*;
import net.year4000.mapnodes.utils.typewrappers.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

public final class GsonUtil {
    public static final Gson GSON = new Gson();

    /** Normal gson object for standard things */
    public static Gson createGson(World world) {
        return gsonBuilder()
            .registerTypeAdapter(LocationList.class, new LocationListDeserializer(world))
            .create();
    }

    /** Normal gson object for standard things */
    public static Gson createGson() {
        return gsonBuilder().create();
    }

    /** Normal gson builder for standard things */
    public static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
            //.setPrettyPrinting()
            .setVersion(1.0)
            .registerTypeAdapter(MaterialList.class, new MaterialListDeserializer())
            .registerTypeAdapter(DamageCauseList.class, new DamageCauseListDeserializer())
            .registerTypeAdapter(EntityTypeList.class, new EntityTypeListDeserializer())
            .registerTypeAdapter(EntityType.class, new EntityTypeDeserializer())
            .registerTypeAdapter(Material.class, new MaterialDeserializer())
            .registerTypeAdapter(PotionEffectType.class, new PotionEffectDeserializer())
            .registerTypeAdapter(ChatColor.class, new ChatColorDeserializer())
            .registerTypeAdapter(GameMode.class, new GameModeDeserializer())
            .registerTypeAdapter(PotionEffectList.class, new PotionEffectListDeserializer())
            .registerTypeAdapter(PlayerInventoryList.class, new PlayerInventoryDeserializer())
            .registerTypeAdapter(PlayerArmorList.class, new PlayerArmorDeserializer())
            .registerTypeAdapter(GameSet.class, new GameModesDeserializer())
            ;
    }
}
