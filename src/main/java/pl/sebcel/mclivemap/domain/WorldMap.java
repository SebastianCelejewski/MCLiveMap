package pl.sebcel.mclivemap.domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

public class WorldMap {

    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;

    private BufferedImage image;

    public WorldMap(int minX, int maxX, int minZ, int maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;

        int width = maxX - minX;
        int height = maxZ - minZ;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void setPixel(int x, int z, Color color) {
        if (x > minX && x < maxX && z > minZ && z < maxZ) {
            int mapX = x - minX;
            int mapY = z - minZ;
            image.setRGB(mapX, mapY, color.getRGB());
        }
    }

    public void setImage(BufferedImage pastedImage, int imageMinX, int imageMinZ) {
        // totally lame, but image.getGraphics().drawImage(...) does not work
        int localX = imageMinX - minX;
        int localY = imageMinZ - minZ;
        for (int x = 0; x < 512; x++) {
            for (int y = 0; y < 512; y++) {
                image.setRGB(localX + x, localY + y, pastedImage.getRGB(x, y));
            }
        }
        // this.image.getGraphics().drawImage(image, 0, 0, null);
    }

    public byte[] getImage() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render image into png file: " + ex.getMessage(), ex);
        }
    }

    public Graphics getGraphics() {
        return image.getGraphics();
    }

    /* To be removed! */

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

}