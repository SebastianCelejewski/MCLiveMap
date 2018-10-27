package pl.sebcel.mclivemap.domain;

import java.util.List;

public class Region {

    private RegionCoordinates coordinates;
    private List<Chunk> chunks;
    private boolean loadedSuccessfully;

    public Region(RegionCoordinates coordinates, List<Chunk> chunks, boolean loadedSuccessfully) {
        this.coordinates = coordinates;
        this.chunks = chunks;
        this.loadedSuccessfully = loadedSuccessfully;
    }

    public RegionCoordinates getCoordinates() {
        return coordinates;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public boolean isLoadedSuccessfully() {
        return loadedSuccessfully;
    }

}