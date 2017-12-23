package pl.sebcel.mclivemap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;

public class MapUtils {

    private final static Color[] COLOR_TABLE = new Color[] { Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.LIGHT_GRAY, Color.DARK_GRAY};

    public byte[] renderMap(HeightMap heightMap, List<PlayerData> playersLocations) throws Exception {
        int xSize = heightMap.getMaxX() - heightMap.getMinX();
        int zSize = heightMap.getMaxZ() - heightMap.getMinZ();

        System.out.println("Generating map");
        BufferedImage image = new BufferedImage(xSize, zSize, BufferedImage.TYPE_INT_ARGB);

        for (int x = heightMap.getMinX(); x < heightMap.getMaxX(); x++) {
            for (int z = heightMap.getMinZ(); z < heightMap.getMaxZ(); z++) {
                Integer height = heightMap.getHeight(x, z);
                if (height != null) {
                    int rgb = getRGB(height);
                    int imageX = x - heightMap.getMinX();
                    int imageY = z - heightMap.getMinZ();
                    image.setRGB(imageX, imageY, rgb);
                }
            }
        }

        Graphics g = image.getGraphics();

        int colourIdx = 0;
        for (PlayerData playerData : playersLocations) {
            Integer previousImageX = null;
            Integer previousImageY = null;
            g.setColor(COLOR_TABLE[colourIdx++]);
            for (PlayerLocation location : playerData.getLocations()) {
                int x = location.getX();
                int z = location.getZ();

                if (x > heightMap.getMinX() && x < heightMap.getMaxX()) {
                    if (z > heightMap.getMinZ() && z < heightMap.getMaxZ()) {
                        int imageX = x - heightMap.getMinX();
                        int imageY = z - heightMap.getMinZ();
                        if (previousImageX != null) {
                            g.drawLine(previousImageX, previousImageY, imageX, imageY);
                        }
                        previousImageX = imageX;
                        previousImageY = imageY;
                    }
                }
            }
            if (previousImageX != null) {
                g.drawString(playerData.getName(), previousImageX, previousImageY);
            }
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);

        return output.toByteArray();
    }

    private int getRGB(int height) {
        int red = height;
        int green = 255 - height;
        int blue = 0;
        int alpha = 255;
        int rgb = red;
        rgb = (rgb << 8) + green;
        rgb = (rgb << 8) + blue;
        rgb = (rgb << 8) + alpha;
        return rgb;
    }
}