package pl.sebcel.mclivemap.domain.regioncoordinates;

import org.junit.Test;

import junit.framework.Assert;
import pl.sebcel.mclivemap.domain.PlayerLocation;
import pl.sebcel.mclivemap.domain.RegionCoordinates;

public class When_obtaining_from_player_location {

    private RegionCoordinates calculate(int playerX, int playerZ) {
        PlayerLocation playerLocation = new PlayerLocation(0, playerX, 0, playerZ);
        return RegionCoordinates.fromPlayerLocation(playerLocation);
    }

    @Test
    public void for_positive_player_X_should_return_integer_of_player_X_divided_by_512() {
        Assert.assertEquals(0, calculate(0, 0).getRegionX());
        Assert.assertEquals(0, calculate(511, 0).getRegionX());
        Assert.assertEquals(1, calculate(512, 0).getRegionX());
        Assert.assertEquals(1, calculate(1023, 0).getRegionX());
        Assert.assertEquals(2, calculate(1024, 0).getRegionX());
    }

    @Test
    public void for_negative_player_X_should_return_floor_of_player_X_as_decimal_number_divided_by_512() {
        Assert.assertEquals(-1, calculate(-1, 0).getRegionX());
        Assert.assertEquals(-1, calculate(-512, 0).getRegionX());
        Assert.assertEquals(-2, calculate(-513, 0).getRegionX());
        Assert.assertEquals(-2, calculate(-1024, 0).getRegionX());
        Assert.assertEquals(-3, calculate(-1025, 0).getRegionX());
    }

    @Test
    public void for_positive_player_Z_should_return_integer_of_player_Z_divided_by_512() {
        Assert.assertEquals(0, calculate(0, 0).getRegionZ());
        Assert.assertEquals(0, calculate(0, 511).getRegionZ());
        Assert.assertEquals(1, calculate(0, 512).getRegionZ());
        Assert.assertEquals(1, calculate(0, 1023).getRegionZ());
        Assert.assertEquals(2, calculate(0, 1024).getRegionZ());
    }

    @Test
    public void for_negative_player_Z_should_return_floor_of_player_Z_as_decimal_number_divided_by_512() {
        Assert.assertEquals(-1, calculate(0, -1).getRegionZ());
        Assert.assertEquals(-1, calculate(0, -512).getRegionZ());
        Assert.assertEquals(-2, calculate(0, -513).getRegionZ());
        Assert.assertEquals(-2, calculate(0, -1024).getRegionZ());
        Assert.assertEquals(-3, calculate(0, -1025).getRegionZ());
    }
}