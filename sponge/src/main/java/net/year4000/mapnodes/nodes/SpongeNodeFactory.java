/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.eclipsesource.v8.V8;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import net.year4000.mapnodes.*;
import net.year4000.utilities.ErrorReporter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/** This will generate the maps for sponge */
public class SpongeNodeFactory implements NodeFactory {
  private Set<MapPackage> mapPackages = Sets.newHashSet();

  @Inject private Logger logger;
  @Inject private Bindings bindings;
  @Inject private Settings settings;

  @Override
  public Node create(MapPackage map) throws Exception {
    return new SpongeNode(this, map);
  }

  @Override
  public Collection<MapPackage> packages() {
    return mapPackages.stream().collect(Collectors.toSet());
  }

  @Override
  public void generatePackages() {
    Path path = Paths.get(settings.mapPath);
    try {
      Files.walk(path, FileVisitOption.FOLLOW_LINKS).forEach(dir -> {
        // Maps must have the world zip in them
        if (Files.isDirectory(dir) && Files.isRegularFile(dir.resolve("world.zip"))) {
          try {
            mapPackages.add(new MapPackage(dir.toUri()));
          } catch (IOException error) { // Could be multiple problems when map can not load
            logger.error(ErrorReporter.builder(error).add("IO Error in path: " + dir).build().toString());
          }
        }
      });
    } catch (IOException error) { // Problem with root map path
      logger.error(ErrorReporter.builder(error).add("IO Error in path: " + settings.mapPath).build().toString());
    }
  }

  @Override
  public V8ThreadLock<V8> v8Thread() {
    return bindings.v8Thread();
  }
}
