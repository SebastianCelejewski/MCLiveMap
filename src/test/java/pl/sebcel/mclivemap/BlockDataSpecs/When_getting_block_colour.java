package pl.sebcel.mclivemap.BlockDataSpecs;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.sebcel.mclivemap.BlockData;
import pl.sebcel.mclivemap.domain.Block;

public class When_getting_block_colour {

    private BlockData blockData;
    private Color color = Color.RED;

    @Before
    public void setUp() {
        Map<Integer, Block> data = new HashMap<>();
        data.put(1, new Block(1, "a name", false, color));
        blockData = new BlockData(data);
    }

    @Test
    public void should_return_false_for_unknown_block() {
        Assert.assertEquals(Color.WHITE, blockData.getColor(0));
    }

    @Test
    public void should_return_actual_transparency_for_known_block() {
        Assert.assertEquals(color, blockData.getColor(1));
    }
}
