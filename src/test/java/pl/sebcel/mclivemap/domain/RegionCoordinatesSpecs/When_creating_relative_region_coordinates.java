package pl.sebcel.mclivemap.domain.RegionCoordinatesSpecs;

import org.junit.Assert;
import org.junit.Test;

import pl.sebcel.mclivemap.domain.RegionCoordinates;

public class When_creating_relative_region_coordinates {

    RegionCoordinates baseRegionCoordinates = new RegionCoordinates(5, 5);

    @Test
    public void should_create_LEFT_region_with_decremented_regionX() {
        Assert.assertEquals(new RegionCoordinates(4, 5), baseRegionCoordinates.left());
    }

    @Test
    public void should_create_RIGHT_region_with_incremented_regionX() {
        Assert.assertEquals(new RegionCoordinates(6, 5), baseRegionCoordinates.right());
    }

    @Test
    public void should_create_UP_region_with_decremented_regionZ() {
        Assert.assertEquals(new RegionCoordinates(5, 4), baseRegionCoordinates.up());
    }

    @Test
    public void should_create_DOWN_region_with_incremented_regionZ() {
        Assert.assertEquals(new RegionCoordinates(5, 6), baseRegionCoordinates.down());
    }
}
