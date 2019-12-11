package net.year4000.mapnodes.v8;

import java.nio.file.Paths;

public class V8Engine {
  public native boolean startVm();

  public static void main(String[] args) {
    System.out.println(Paths.get(".").toAbsolutePath());
    System.load("/Users/joshua/Documents/year4000/MapNodes/core/build/libs/mapnodes/shared/libmapnodes.dylib");

    V8Engine v8 = new V8Engine();
    v8.startVm();
  }
}
