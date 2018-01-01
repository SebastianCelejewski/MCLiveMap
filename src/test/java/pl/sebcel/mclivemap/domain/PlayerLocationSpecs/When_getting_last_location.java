package pl.sebcel.mclivemap.domain.PlayerLocationSpecs;

import org.junit.Test;

import junit.framework.Assert;
import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;

public class When_getting_last_location {

    @Test
    public void should_return_null_if_there_is_no_location_data_for_a_player() {
        Assert.assertEquals(null, new PlayerData().getLastLocation());
    }

    @Test
    public void should_return_last_item_on_location_list_if_there_any_locations() {
        PlayerLocation location1 = new PlayerLocation(0, 1, 2, 3);
        PlayerLocation location2 = new PlayerLocation(0, 4, 5, 6);

        PlayerData playerData = new PlayerData();
        playerData.addLocation(location1);
        playerData.addLocation(location2);

        Assert.assertEquals(location2, playerData.getLastLocation());
    }

}
