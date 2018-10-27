package pl.sebcel.mclivemap.domain;

public class Chunk {

    private int chunkX;
    private int chunkZ;
    private int[] heightMap;
    private boolean blockIdsAreStrings;
    private int[] numericBlockIds;
    private String[] stringBlockIds;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int[] getHeightMap() {
        return heightMap;
    }

    public void setHeightMap(int[] heightMap) {
        this.heightMap = heightMap;
    }

    public boolean isBlockIdsAreStrings() {
        return blockIdsAreStrings;
    }

    public void setBlockIdsAreStrings(boolean blockIdsAreStrings) {
        this.blockIdsAreStrings = blockIdsAreStrings;
    }

    public int[] getNumericBlockIds() {
        return numericBlockIds;
    }

    public void setNumericBlockIds(int[] numericBlockIds) {
        this.numericBlockIds = numericBlockIds;
    }

    public String[] getStringBlockIds() {
        return stringBlockIds;
    }

    public void setStringBlockIds(String[] stringBlockIds) {
        this.stringBlockIds = stringBlockIds;
    }

}