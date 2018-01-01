package pl.sebcel.mclivemap.domain.WorldMapSpecs;

import java.awt.image.BufferedImage;

import org.junit.Assert;
import org.junit.Test;

import pl.sebcel.mclivemap.domain.Bounds;
import pl.sebcel.mclivemap.domain.WorldMap;

public class When_adding_a_map_fragment {

    @Test
    public void should_embed_it_in_location_based_on_relative_coordinates() {

        // Map size: 200x100
        // Map bounds: left: 0, top 0, right: 199, bottom: 199
        Bounds mapBounds = new Bounds(0, 0, 199, 99);
        WorldMap map = new WorldMap(mapBounds);

        // Pasted image size: 100x50
        // Pasted image bounds: left: 51, top: 26, bottom: 175, right: 75
        Bounds pastedImageBounds = new Bounds(51, 26, 150, 75);
        BufferedImage pastedImage = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);

        // Setting color pixels on the corners of the pasted image
        pastedImage.setRGB(0, 0, 1); // top left
        pastedImage.setRGB(99, 0, 2); // top right
        pastedImage.setRGB(99, 49, 3); // bottom right
        pastedImage.setRGB(0, 49, 4); // bottom left

        map.setImageFragment(pastedImage, pastedImageBounds);

        BufferedImage mapImage = map.getImage();
        Assert.assertEquals(1, mapImage.getRGB(51, 26));
        Assert.assertEquals(2, mapImage.getRGB(150, 26));
        Assert.assertEquals(3, mapImage.getRGB(150, 75));
        Assert.assertEquals(4, mapImage.getRGB(51, 75));
    }

}
