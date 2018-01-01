package pl.sebcel.mclivemap.domain;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

/**
 * Represents a single map displayed on a web page
 * 
 * @author Sebastian Celejewski
 */
public class WorldMap {

    private Bounds mapBounds;
    private BufferedImage image;

    /**
     * Creates new WorldMap instance
     * 
     * @param mapBounds
     *            map bounds (physical coordinates of map edges)
     */
    public WorldMap(Bounds mapBounds) {
        this.mapBounds = mapBounds;
        image = new BufferedImage(mapBounds.getWidth(), mapBounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public Bounds getBounds() {
        return mapBounds;
    }

    /**
     * Adds an image that represents a fragment of a map
     * 
     * @param pastedImage
     *            image to be added as a fragment of a map (1 region = 512 x 512 blocks)
     * @param imageMinX
     *            coordinate of the left edge of the added fragment
     * @param imageMinZ
     *            coordinate of the top edge of the added fragment
     */
    public void setImageFragment(BufferedImage pastedImage, Bounds pastedImageBounds) {
        // totally lame, but image.getGraphics().drawImage(...) does not work
        int localX = pastedImageBounds.getMinX() - mapBounds.getMinX();
        int localY = pastedImageBounds.getMinZ() - mapBounds.getMinZ();
        
        for (int x = 0; x < pastedImageBounds.getWidth(); x++) {
            for (int y = 0; y < pastedImageBounds.getHeight(); y++) {
                image.setRGB(localX + x, localY + y, pastedImage.getRGB(x, y));
            }
        }
    }

    /**
     * Returns image representing the whole map as BufferedImage object
     * 
     * @return map image as BufferedImage
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns an image representing the whole map as PNG file
     * 
     * @return map image as PNG file
     */
    public byte[] getImageAsPNG() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render image into png file: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns the Graphics object so that it is possible to add additional elements to the map image, e.g. graphics representing players and their locations
     * 
     * @return Graphics object
     */
    public Graphics getGraphics() {
        return image.getGraphics();
    }
}