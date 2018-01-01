package pl.sebcel.mclivemap.domain.BoundsSpecs;

import org.junit.Test;

import junit.framework.Assert;
import pl.sebcel.mclivemap.domain.Bounds;

public class When_calculating_width_and_height {

    private Bounds bounds = new Bounds(1, 2, 3, 5);

    @Test
    public void should_return_number_of_blocks_between_minX_and_maxX_as_width() {
        Assert.assertEquals(3, bounds.getWidth());
    }

    @Test
    public void should_return_number_of_blocks_between_minZ_and_maxZ_as_height() {
        Assert.assertEquals(4, bounds.getHeight());
    }
}