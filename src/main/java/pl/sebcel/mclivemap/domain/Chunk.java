package pl.sebcel.mclivemap.domain;

public class Chunk {

    private int chunkX;
    private int chunkZ;
    private int[] heightMap;
    private boolean id1_13;
    private int[] blockIds;

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

    public boolean isId1_13() {
        return id1_13;
    }

    public void setId1_13(boolean id1_13) {
        this.id1_13 = id1_13;
    }

    public int[] getBlockIds() {
        return blockIds;
    }

    public void setBlockIds(int[] blockIds) {
        this.blockIds = blockIds;
    }
}