package pl.sebcel.mclivemap.loaders;

import java.util.Arrays;
import java.util.stream.Collectors;

public class NBTLongArrayDecompressor {
    
    public int[] decompress(long[] input, int numberOfValues) {
        String bitsString = Arrays.stream(input)
              .map(l -> Long.reverse(l))
              .mapToObj(l -> Long.toBinaryString(l))
              .map(s -> padWithZeroesToMake64bits(s))
              .collect(Collectors.joining(""));

        int bitsPerValue = bitsString.length() / numberOfValues;
        int[] heightMap = Arrays.stream(splitIntoFixedLengthString(bitsString, bitsPerValue))
            .map(s -> reverse(s))
            .mapToInt(s -> Integer.parseInt(s, 2))
            .toArray();

        return heightMap;
    }

    private String padWithZeroesToMake64bits(String s) {
        while (s.length() < 64) {
            s = "0" + s;
        }
        return s;
    }
    
    private String reverse(String s) {
        char[] output = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            output[s.length()-i-1] = s.charAt(i);
        }
        return new String(output);
    }
    
    private String[] splitIntoFixedLengthString(String s, int chunkLength) {
        String[] outputChunks = new String[s.length() / chunkLength];
        for (int i = 0; i < outputChunks.length; i++) {
            outputChunks[i] = s.substring(i * chunkLength,  (i+1) * chunkLength);
        }
        return outputChunks;
    }
}
