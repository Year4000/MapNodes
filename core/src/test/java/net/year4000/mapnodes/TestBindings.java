package net.year4000.mapnodes;

public class TestBindings extends Bindings {
  @Override
  public void sendMessage(String player, String message) {
    System.out.println(player + ": " + message);
  }
}
