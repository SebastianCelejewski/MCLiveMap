package pl.sebcel.mclivemap.loaders;

import java.util.HashMap;
import java.util.Map;

public class BlockIdsCache {

    private int counter = 0;
    private Map<String, Integer> data = new HashMap<>();
    private Map<Integer, String> reverseData = new HashMap<>();

    public int getNumericBlockId(String stringBlockId) {
        if (data.containsKey(stringBlockId)) {
            return data.get(stringBlockId);
        } else {
            data.put(stringBlockId, counter);
            reverseData.put(counter, stringBlockId);
            return counter++;
        }
    }
    
    public String getStringBlockId(int numericBlockId) {
        return reverseData.get(numericBlockId);
    }
}