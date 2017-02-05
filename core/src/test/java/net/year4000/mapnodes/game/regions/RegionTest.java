/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import com.flowpowered.math.vector.Vector3i;
import org.junit.Assert;
import org.junit.Test;

public class RegionTest {
    @Test
    public void pointRegionTest() {
        PointRegion region = new PointRegion(Vector3i.ZERO);
        System.out.println(region);
        Assert.assertTrue(region.getPoints().isPresent());
        Assert.assertTrue(region.getPoints().get().size() == 1);
        Assert.assertTrue(region.contains(Vector3i.ZERO));
        Assert.assertFalse(region.contains(Vector3i.ONE));
    }

    @Test
    public void globalRegionTest() {
        GlobalRegion region = new GlobalRegion();
        System.out.println(region);
        Assert.assertFalse(region.getPoints().isPresent());
        Assert.assertTrue(region.contains(Vector3i.ZERO));
        Assert.assertTrue(region.contains(Vector3i.ONE));
    }

    @Test
    public void cuboidRegion() {
        CuboidRegion region = new CuboidRegion(new Vector3i(2, 2, 2), new Vector3i(10, 10, 10));
        System.out.println(region);
        Assert.assertTrue(region.contains(new Vector3i(5, 5,5)));
        Assert.assertFalse(region.contains(new Vector3i(0,0,0)));
        Assert.assertTrue(region.getPoints().isPresent());
        int volume = (int) Math.pow(8, 3); // volume of the cuboid we are testing
        Assert.assertEquals(volume, region.getPoints().get().size());
    }

    @Test
    public void sphereRegion() {
        SphereRegion region = new SphereRegion(Vector3i.ZERO, 6);
        System.out.println(region);
        Assert.assertTrue(region.contains(Vector3i.ZERO));
        Assert.assertFalse(region.contains(new Vector3i(7,0,0)));
        Assert.assertTrue(region.getPoints().isPresent());
        // todo Minecraft circles are not the same volume formula
        //int volume = (int) Math.ceil((4 * Math.PI * Math.pow(6, 3)) / 3); // volume of the sphere
        //Assert.assertEquals(volume, region.getPoints().get().size());
    }

    @Test
    public void cubeRegion() {
        final int radius = 5;
        CubeRegion region = new CubeRegion(Vector3i.ZERO, radius);
        System.out.println(region);
        Assert.assertTrue(region.contains(Vector3i.ZERO));
        Assert.assertFalse(region.contains(new Vector3i(6, 6, 6)));
        Assert.assertTrue(region.getPoints().isPresent());
        int volume = (int) Math.pow(radius * 2, 3); // volume of the cube we are testing
        Assert.assertEquals(volume, region.getPoints().get().size());
    }
}
