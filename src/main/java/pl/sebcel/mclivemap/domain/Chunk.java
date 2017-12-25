package pl.sebcel.mclivemap.domain;

import java.util.Map;

public class Chunk {

    private int chunkX;
    private int chunkZ;
    private int[] heightMap;
    private Map<Integer, byte[]> sections;

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

    public Map<Integer, byte[]> getSections() {
        return sections;
    }

    public void setSections(Map<Integer, byte[]> sections) {
        this.sections = sections;
    }

}