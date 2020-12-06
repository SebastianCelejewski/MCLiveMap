package pl.sebcel.mclivemap.loaders.DecompressorSpecs;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import pl.sebcel.mclivemap.loaders.NBTLongArrayDecompressor;

public class When_decompressing_block_data {
	
	private NBTLongArrayDecompressor cut = new NBTLongArrayDecompressor();

    @Test
    public void should_return_indices_within_palette() throws Exception {
        List<String> compressedBlockIndicesStr = Files.readAllLines(Paths.get(this.getClass().getClassLoader().getResource("compressedBlockIndices-2.txt").toURI()));
        List<String> palette = Files.readAllLines(Paths.get(this.getClass().getClassLoader().getResource("palette-2.txt").toURI()));
        
        int numberOfValues = 256 * 16;
        
        Object[] inputLongs = compressedBlockIndicesStr.stream().map((s) -> Long.parseLong(s)).toArray();
        long[] input = new long[inputLongs.length];
        for (int i = 0; i < input.length; i++) {
        	input[i] = ((Long) inputLongs[i]).longValue();
        }
        int[] output = cut.decompress(input, numberOfValues);
        
        for (int i = 0; i < output.length; i++) {
        	Assert.assertTrue("Value " + output[i] + " at index " + i + " goes beyond the palette size", output[i] < palette.size());
        }
    }
}