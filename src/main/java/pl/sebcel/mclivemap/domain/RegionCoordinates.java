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
}