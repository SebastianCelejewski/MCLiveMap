package pl.sebcel.mclivemap.domain;

public class RegionCoordinates {

    private int regionX;
    private int regionZ;

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

    public int getMinX() {
        return regionX * 512;
    }

    public int getMaxX() {
        return getMinX() + 512;
    }

    public int getMinZ() {
        return regionZ * 512;
    }

    public int getMaxZ() {
        return getMinZ() + 512;
    }

    public static RegionCoordinates fromPlayerLocation(PlayerLocation playerLocation) {
        int regionX = (int) Math.floor(playerLocation.getX() / 512);
        int regionZ = (int) Math.floor(playerLocation.getZ() / 512);
        if (playerLocation.getX() < 0) {
            regionX = regionX - 1;
        }
        if (playerLocation.getZ() < 0) {
            regionZ = regionZ - 1;
        }

        return new RegionCoordinates(regionX, regionZ);
    }

    @Override
    public String toString() {
        return "(" + regionX + "," + regionZ + ")";
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