package pl.sebcel.mclivemap.render;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;
import pl.sebcel.mclivemap.domain.WorldMap;

public class PlayerRenderer {

    private final static Color[] COLOR_TABLE = new Color[] { Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.DARK_GRAY };

    public void renderPlayers(WorldMap worldMap, List<PlayerData> playersData) {
        Graphics g = worldMap.getGraphics();

        int colourIdx = 0;
        for (PlayerData playerData : playersData) {
            Integer previousImageX = null;
            Integer previousImageY = null;
            g.setColor(COLOR_TABLE[colourIdx++]);

            List<PlayerLocation> locations = playerData.getLocations();
            if (locations.size() > 100) {
                locations = locations.subList(locations.size() - 100, locations.size());
            }

            for (PlayerLocation location : locations) {
                int dimension = location.getDimension();
                int x = location.getX();
                int z = location.getZ();

                if (dimension == 0) {
                    int imageX = x - worldMap.getMinX();
                    int imageY = z - worldMap.getMinZ();
                    if (previousImageX != null) {
                        g.drawLine(previousImageX, previousImageY, imageX, imageY);
                    }
                    previousImageX = imageX;
                    previousImageY = imageY;
                } else {
                    previousImageX = null;
                    previousImageY = null;
                }
            }
            if (previousImageX != null) {
                g.drawString(playerData.getName(), previousImageX, previousImageY);
            }
        }
    }
}