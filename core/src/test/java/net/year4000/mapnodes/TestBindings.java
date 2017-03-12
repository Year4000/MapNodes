/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

public class TestBindings extends Bindings {
  @Override
  public void sendMessage(String player, String message) {
    System.out.println(player + ": " + message);
  }

  @Override
  public String playerMetaUuid(String uuid) {
    return "Herobrine:f84c6a79-0a4e-45e0-879b-cd49ebd4c4e2";
  }
}
