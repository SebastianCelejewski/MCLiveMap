package pl.sebcel.mclivemap;

import java.util.HashMap;

public class HeightMap {

    private HashMap<String, Integer> data = new HashMap<>();
    private Integer minX = null;
    private Integer maxX = null;
    private Integer minZ = null;
    private Integer maxZ = null;

    public void setHeight(int x, int z, int value) {
        String key = getKey(x, z);
        data.put(key, value);
        updateMaximumValues(x, z);
    }

    public Integer getHeight(int x, int z) {
        String key = getKey(x, z);
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return null;
        }
    }
    
    public int getMinX() {
        return minX;
    }
    
    public int getMaxX() {
        return maxX;
    }
    
    public int getMinZ() {
        return minZ;
    }
    
    public int getMaxZ() {
        return maxZ;
    }
    

    private String getKey(int x, int z) {
        return x + "," + z;
    }

    private void updateMaximumValues(int x, int z) {
        if (minX == null || minX > x) {
            minX = x;
        }

        if (maxX == null || maxX < x) {
            maxX = x;
        }

        if (minZ == null || minZ > z) {
            minZ = z;
        }

        if (maxZ == null || maxZ < z) {
            maxZ = z;
        }
    }
}