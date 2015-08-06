/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.skywars;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Tracker {
    private static List<Reference<Tracker>> trackers = new CopyOnWriteArrayList<>();
    private World world;
    private int id;
    private Effect particle;
    private int data;

    public Tracker(World world, int id, Effect particle, int data) {
        this.world = world;
        this.id = id;
        this.particle = particle;
        this.data = data;
        trackers.add(new SoftReference<>(this));
    }

    public static class TrackerRunner implements Runnable {
        @Override
        public void run() {
            for (Tracker track : trackers.stream().map(Reference::get).collect(Collectors.toList())) {
                if (track == null) continue;

                List<Entity> shoot = track.world.getEntities().stream()
                    .filter(id -> id.getEntityId() == track.id)
                    .collect(Collectors.toList());

                if (shoot.size() == 0) {
                    continue;
                }

                shoot.forEach(e -> {
                    Bukkit.getOnlinePlayers().parallelStream().forEach(player -> {
                        player.playEffect(e.getLocation(), track.particle, track.data);
                    });
                });
            }
        }
    }
}