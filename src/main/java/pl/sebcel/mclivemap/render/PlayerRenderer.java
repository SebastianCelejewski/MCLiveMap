package pl.sebcel.mclivemap.render;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;
import pl.sebcel.mclivemap.domain.WorldMap;

public class PlayerRenderer {

    private Color[] colourPalette = new Color[22];

    public PlayerRenderer() {
        colourPalette[0] = new Color(230, 25, 75);
        colourPalette[1] = new Color(60, 180, 75);
        colourPalette[2] = new Color(255, 225, 25);
        colourPalette[3] = new Color(0, 130, 200);
        colourPalette[4] = new Color(245, 130, 48);
        colourPalette[5] = new Color(145, 30, 180);
        colourPalette[6] = new Color(70, 240, 240);
        colourPalette[7] = new Color(240, 50, 230);
        colourPalette[8] = new Color(210, 245, 60);
        colourPalette[9] = new Color(250, 190, 190);
        colourPalette[10] = new Color(0, 128, 128);
        colourPalette[11] = new Color(230, 190, 255);
        colourPalette[12] = new Color(170, 110, 40);
        colourPalette[13] = new Color(255, 250, 200);
        colourPalette[14] = new Color(128, 0, 0);
        colourPalette[15] = new Color(170, 255, 195);
        colourPalette[16] = new Color(128, 128, 0);
        colourPalette[17] = new Color(255, 215, 180);
        colourPalette[18] = new Color(0, 0, 128);
        colourPalette[19] = new Color(128, 128, 128);
        colourPalette[20] = new Color(255, 255, 255);
        colourPalette[21] = new Color(0, 0, 0);
    }

    public void renderPlayers(WorldMap worldMap, List<PlayerData> playersData, boolean drawPaths) {
        Graphics g = worldMap.getGraphics();

        int colourIdx = 0;
        for (PlayerData playerData : playersData) {
            Integer previousImageX = null;
            Integer previousImageY = null;
            g.setColor(colourPalette[colourIdx++]);

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

                if (previousImageX != null && drawPaths) {
                    g.drawLine(previousImageX, previousImageY, imageX, imageY);
                }

                previousImageX = imageX;
                previousImageY = imageY;

            }

            if (previousImageX != null) {
                g.fillOval(previousImageX - 4, previousImageY - 4, 9, 9);
                g.drawString(playerData.getName(), previousImageX + 9, previousImageY);
            }
        }
    }

    private int getDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y2;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
}