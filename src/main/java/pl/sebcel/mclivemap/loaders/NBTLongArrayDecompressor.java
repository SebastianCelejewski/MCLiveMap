package pl.sebcel.mclivemap.loaders;

public class NBTLongArrayDecompressor {

    public int[] decompress(long[] input, int numberOfValues) {
        int[] result = new int[numberOfValues];

        int bitsPerValue = 64 * input.length / numberOfValues;

        int inputLongIdx = 0;
        int inputBitIdx = 0;

        for (int outputIntIdx = 0; outputIntIdx < result.length; outputIntIdx++) {
            for (int outputBitIdx = 0; outputBitIdx < bitsPerValue; outputBitIdx++) {
                long value = input[inputLongIdx] & (1L << inputBitIdx);
                if (value != 0) {
                    value = 1;
                }
                result[outputIntIdx] = (int) ((result[outputIntIdx] | (value << outputBitIdx)) & 0xff);
                inputBitIdx++;
                if (inputBitIdx > 63) {
                    inputLongIdx++;
                    inputBitIdx = 0;
                }
            }
        }

        return result;
    }
}