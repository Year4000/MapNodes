/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.eclipsesource.v8.V8;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import net.year4000.mapnodes.*;
import net.year4000.utilities.ErrorReporter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/** This will generate the maps for sponge */
public class SpongeNodeFactory implements NodeFactory {
  private Set<MapPackage> mapPackages = Sets.newHashSet();
  private final Random random = new Random();

  @Inject private Injector injector;
  @Inject private Logger logger;
  @Inject private Bindings bindings;
  @Inject private Settings settings;

  @Override
  public Node create(MapPackage map) throws Exception {
    return injector.createChildInjector((Module) binder -> binder.bind(MapPackage.class).toInstance(map)).getInstance(SpongeNode.class);
  }

  @Override
  public Collection<MapPackage> packages() {
    return mapPackages.stream().sorted((x, y) -> random.nextInt(2) - 1).collect(Collectors.toList());
  }

  @Override
  public void generatePackages() {
    Path path = Paths.get(settings.mapPath);
    try {
      Files.walk(path, FileVisitOption.FOLLOW_LINKS).forEach(dir -> {
        // Maps must have the world zip in them
        if (Files.isDirectory(dir) && Files.isRegularFile(dir.resolve(MapPackage.PACKAGE_WORLD)) && Files.isRegularFile(dir.resolve(MapPackage.PACKAGE_MAP))) {
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
  public V8 v8() {
    return bindings.v8();
  }
}
