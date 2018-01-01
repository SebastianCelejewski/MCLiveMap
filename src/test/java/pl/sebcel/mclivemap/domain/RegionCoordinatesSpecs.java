package pl.sebcel.mclivemap.domain;

import org.junit.Test;

import junit.framework.Assert;

public class RegionCoordinatesSpecs {

    private Bounds calculateBounds(int regionX, int regionZ) {
        RegionCoordinates cut = new RegionCoordinates(regionX, regionZ);
        return cut.getBounds();
    }

    @Test
    public void should_return_left_bound_as_512_times_region_horizontal_index() {
        Assert.assertEquals(-512, calculateBounds(-1, 0).getMinX());
        Assert.assertEquals(0, calculateBounds(0, 0).getMinX());
        Assert.assertEquals(512, calculateBounds(1, 0).getMinX());
        Assert.assertEquals(1024, calculateBounds(2, 0).getMinX());
        Assert.assertEquals(-1024, calculateBounds(-2, 0).getMinX());
    }

    @Test
    public void should_return_right_bound_as_left_bound_plus_512_blocks() {
        Assert.assertEquals(-513, calculateBounds(-2, 0).getMaxX());
        Assert.assertEquals(-1, calculateBounds(-1, 0).getMaxX());
        Assert.assertEquals(511, calculateBounds(0, 0).getMaxX());
        Assert.assertEquals(1023, calculateBounds(1, 0).getMaxX());
        Assert.assertEquals(2047, calculateBounds(3, 0).getMaxX());
    }

    @Test
    public void should_return_top_bound_as_512_times_region_vertical_index() {
        Assert.assertEquals(-512, calculateBounds(0, -1).getMinZ());
        Assert.assertEquals(0, calculateBounds(0, 0).getMinZ());
        Assert.assertEquals(512, calculateBounds(0, 1).getMinZ());
        Assert.assertEquals(1024, calculateBounds(0, 2).getMinZ());
        Assert.assertEquals(-1024, calculateBounds(0, -2).getMinZ());
    }

    @Test
    public void should_return_bottom_bound_as_top_bound_plus_512_blocks() {
        Assert.assertEquals(-513, calculateBounds(0, -2).getMaxZ());
        Assert.assertEquals(-1, calculateBounds(0, -1).getMaxZ());
        Assert.assertEquals(511, calculateBounds(0, 0).getMaxZ());
        Assert.assertEquals(1023, calculateBounds(0, 1).getMaxZ());
        Assert.assertEquals(2047, calculateBounds(0, 3).getMaxZ());
    }
}