package pl.sebcel.mclivemap.domain;

/**
 * Identifies a region in Minecraft world
 * 
 * @author Sebastian Celejewski
 */
public class RegionCoordinates {

    private int regionX;
    private int regionZ;

    /**
     * Creates new instance of Region Coordinates class
     * 
     * @param regionX
     *            horizontal index of a region
     * @param regionZ
     *            vertical index of a region
     */
    public RegionCoordinates(int regionX, int regionZ) {
        this.regionX = regionX;
        this.regionZ = regionZ;
    }

    public int getRegionX() {
        return regionX;
    }

    public int getRegionZ() {
        return regionZ;
    }

    public Bounds getBounds() {
        int minX = regionX * 512;
        int maxX = minX + 511;
        int minZ = regionZ * 512;
        int maxZ = minZ + 511;

        return new Bounds(minX, minZ, maxX, maxZ);
    }

    public static RegionCoordinates fromPlayerLocation(PlayerLocation playerLocation) {
        int regionX = (int) Math.floor((double) playerLocation.getX() / 512);
        int regionZ = (int) Math.floor((double) playerLocation.getZ() / 512);

        return new RegionCoordinates(regionX, regionZ);
    }

    public RegionCoordinates left() {
        return new RegionCoordinates(regionX - 1, regionZ);
    }

    public RegionCoordinates right() {
        return new RegionCoordinates(regionX + 1, regionZ);
    }

    public RegionCoordinates up() {
        return new RegionCoordinates(regionX, regionZ - 1);
    }

    public RegionCoordinates down() {
        return new RegionCoordinates(regionX, regionZ + 1);
    }

    @Override
    public String toString() {
        return "(" + regionX + "," + regionZ + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + regionX;
        result = prime * result + regionZ;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegionCoordinates other = (RegionCoordinates) obj;
        if (regionX != other.regionX)
            return false;
        if (regionZ != other.regionZ)
            return false;
        return true;
    }

}