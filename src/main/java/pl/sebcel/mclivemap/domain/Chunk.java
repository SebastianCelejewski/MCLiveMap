package pl.sebcel.mclivemap.domain;

public class Chunk {

    private int chunkX;
    private int chunkZ;
    private int[] heightMap;
    private byte[] blocks;

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

    public byte[] getBlocks() {
        return blocks;
    }

    public void setBlocks(byte[] blocks) {
        this.blocks = blocks;
    }

}