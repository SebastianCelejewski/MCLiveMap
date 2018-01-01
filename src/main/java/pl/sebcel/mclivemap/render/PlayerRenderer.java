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

            for (PlayerLocation location : locations) {
                int dimension = location.getDimension();
                int x = location.getX();
                int z = location.getZ();

                int imageX = x - worldMap.getBounds().getMinX();
                int imageY = z - worldMap.getBounds().getMinZ();

                // Entered Nether - stop drawing
                if (dimension != 0) {
                    previousImageX = null;
                    previousImageY = null;
                    continue;
                }

                // Too long jump (death and respawn) - stop drawing
                if (previousImageX != null && getDistance(imageX, imageY, previousImageX, previousImageY) > 50) {
                    previousImageX = null;
                    previousImageY = null;
                    continue;
                }

                if (previousImageX != null) {
                    g.drawLine(previousImageX, previousImageY, imageX, imageY);
                }

                previousImageX = imageX;
                previousImageY = imageY;

            }
            if (previousImageX != null) {
                g.fillOval(previousImageX - 2, previousImageY - 2, 5, 5);
                g.drawString(playerData.getName(), previousImageX, previousImageY);
            }
        }
    }

    private int getDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y2;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
}