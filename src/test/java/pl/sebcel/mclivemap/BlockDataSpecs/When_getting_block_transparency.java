package pl.sebcel.mclivemap.BlockDataSpecs;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.sebcel.mclivemap.BlockData;
import pl.sebcel.mclivemap.domain.Block;

public class When_getting_block_transparency {

    private BlockData blockData;
    private boolean transparent = true;

    @Before
    public void setUp() {
        Map<Integer, Block> data = new HashMap<>();
        data.put(1, new Block(1, "a name", transparent, Color.WHITE));
        blockData = new BlockData(data);
    }

    @Test
    public void should_return_white_for_unknown_block() {
        Assert.assertEquals(false, blockData.isTransparent(0));
    }

    @Test
    public void should_return_actual_colour_for_known_block() {
        Assert.assertEquals(transparent, blockData.isTransparent(1));
    }
}
