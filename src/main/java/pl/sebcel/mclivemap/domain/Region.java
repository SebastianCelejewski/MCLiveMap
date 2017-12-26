package pl.sebcel.mclivemap.domain;

import java.util.List;

public class Region {

    private RegionCoordinates coordinates;
    private List<Chunk> chunks;

    public Region(RegionCoordinates coordinates, List<Chunk> chunks) {
        this.coordinates = coordinates;
        this.chunks = chunks;
    }

    public RegionCoordinates getCoordinates() {
        return coordinates;
    }
    
    public List<Chunk> getChunks() {
        return chunks;
    }
    
    public void release() {
        chunks = null;
    }
}