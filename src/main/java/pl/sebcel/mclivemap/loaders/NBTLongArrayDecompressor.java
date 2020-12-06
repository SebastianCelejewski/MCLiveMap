package pl.sebcel.mclivemap.loaders;

public class NBTLongArrayDecompressor {

    public int[] decompress(long[] input, int numberOfValues, int dataVersion) {
        int[] result = new int[numberOfValues];

        int bitsPerValue = 64 * input.length / numberOfValues;
        
        if (dataVersion == 1631) {
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
        } else {
            if (bitsPerValue < 4) {
            	bitsPerValue = 4;
            }
            int numberOfNumbersInSingleLongValue = (int) (64 / bitsPerValue);
            
            long mask = (1L << bitsPerValue) - 1;
            
            for (int i = 0; i < input.length; i++) {
            	for (int j = 0; j < numberOfNumbersInSingleLongValue; j++) {
            		int outputIdx = i*numberOfNumbersInSingleLongValue + j; 
            		int shift = j * bitsPerValue;
            		long value = (input[i] >> shift ) & mask;
            		if (outputIdx < result.length) {
                		result[outputIdx] = (int) value;
            		}
            	}
            }
        }

        return result;
    }
}