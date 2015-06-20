/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes;

import lombok.Getter;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.exceptions.WorldLoadException;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.map.CoreMapObject;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.map.MapObject;
import net.year4000.mapnodes.messages.Msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NodeFactory {
    private static NodeFactory inst;
    /** The games loaded into the queue, thread safe */
    @Getter
    private final Queue<Node> queueNodes = new LinkedBlockingQueue<>();
    /** The current playing game */
    private Node currentNode;
    /** Internal game id counter */
    private int gameID = 0;

    private NodeFactory() {
        MapFactory.getMaps(Settings.get().getLoadMaps()).parallelStream().forEach(world -> {
            if (!world.isDisabled()) {
                try {
                    addMap(world);
                }
                catch (InvalidJsonException | WorldLoadException e) {
                    MapNodesPlugin.log(e, false);
                }
            }
            else {
                MapNodesPlugin.debug(Msg.util("debug.world.disabled", world.getName()));
            }
        });
    }

    public static NodeFactory get() {
        if (inst == null) {
            inst = new NodeFactory();
        }
        return inst;
    }

    public void addMap(MapObject world) throws InvalidJsonException, WorldLoadException {
        CoreMapObject core = MapNodesPlugin.getInst().getApi().getMap(world.getURLCategory(), world.getURLName());
        core.setObject(world);
        addMap(core, false);
    }

    public void addMap(MapObject world, boolean next) throws InvalidJsonException, WorldLoadException {
        CoreMapObject core = MapNodesPlugin.getInst().getApi().getMap(world.getURLCategory(), world.getURLName());
        core.setObject(world);
        addMap(core, next);
    }

    public void addMap(CoreMapObject world) throws InvalidJsonException, WorldLoadException {
        addMap(world, false);
    }

    public void addMap(CoreMapObject world, boolean next) throws InvalidJsonException, WorldLoadException {
        if (next) {
            Queue<Node> maps = new LinkedBlockingQueue<>(queueNodes);
            queueNodes.clear();
            queueNodes.add(new Node(getGameID(), world));
            queueNodes.addAll(maps);
        }
        else {
            queueNodes.add(new Node(getGameID(), world));
        }
    }

    /** Create and return a game id for the node */
    private int getGameID() {
        return ++gameID;
    }

    /** Get a list of all que games and current game */
    public List<Node> getAllGames() {
        return new ArrayList<Node>() {{
            try {
                add(getCurrentGame());
                addAll(queueNodes);
            }
            catch (NullPointerException e) {
                MapNodesPlugin.debug(Msg.util("error.world.none"));
            }
        }};
    }

    /** Get the current node */
    public Node getCurrentGame() {
        if (currentNode == null) {
            currentNode = queueNodes.poll();
            currentNode.register();
            currentNode.getGame().load();
        }

        return currentNode;
    }

    /** Load the next game */
    public Node loadNextQueued() {
        if (currentNode != null) {
            try {
                currentNode.unregister();
            }
            catch(Exception e) {
                MapNodesPlugin.log(Msg.util("error.unload.fail"));
            }
        }

        Node newNode = queueNodes.poll();
        currentNode = newNode;

        newNode.getGame().load();

        return currentNode;
    }

    /** Peek at the next node */
    public Node peekNextQueued() {
        if (isQueuedGames()) {
            return queueNodes.peek();
        }

        return null;
    }

    /** Is there queued games */
    public boolean isQueuedGames() {
        return queueNodes.size() > 0;
    }

}
