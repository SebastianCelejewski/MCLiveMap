package pl.sebcel.mclivemap.domain;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    private String name;
    private List<PlayerLocation> locations = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlayerLocation> getLocations() {
        return locations;
    }

    public void addLocation(PlayerLocation location) {
        this.locations.add(location);
    }
    
    public PlayerLocation getLastLocation() {
        if (locations.size() > 0) {
            return locations.get(locations.size() -1);
        } else {
            return null;
        }
    }
}