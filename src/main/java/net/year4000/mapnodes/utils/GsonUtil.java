/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.deserializers.*;
import net.year4000.mapnodes.utils.typewrappers.*;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonUtil {
    public static final Gson GSON = new Gson();

    /** Normal gson object for standard things */
    public static Gson createGson(World world) {
        return gsonBuilder()
            .registerTypeAdapter(LocationList.class, new LocationListDeserializer(world))
            .registerTypeAdapter(RegionEvents.class, new RegionEventsDeserializer(world))
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
            .setVersion(2.0)
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
            .registerTypeAdapter(RegionList.class, new RegionListDeserializer())
            .registerTypeAdapter(ItemStackList.class, new ItemListDeserializer())
            .registerTypeAdapter(Point.class, new PointDeserializer())
            .registerTypeAdapter(TimeDuration.class, new TimeDurationDeserializer())
            .registerTypeAdapter(Difficulty.class, new DifficultyDeserializer())
            .registerTypeAdapter(WorldTime.class, new WorldTimeDeserializer())
            .registerTypeAdapter(World.Environment.class, new EnvironmentDeserializer())
            ;
    }
}
