package pl.sebcel.mclivemap.domain;

/**
 * Represents coordinates of the edges of an area like chunk, region, or map
 * 
 * @author Sebastian Celejewski
 */
public class Bounds {

    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;

    /**
     * Creates new instance of the Bounds class
     * 
     * @param minX
     *            physical coordinate of the left edge
     * @param minZ
     *            physical coordinate of the top edge
     * @param maxX
     *            physical coordinate of the right edge
     * @param maxZ
     *            physical coordinate of the bottom edge
     */
    public Bounds(int minX, int minZ, int maxX, int maxZ) {
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getWidth() {
        return maxX - minX + 1;
    }

    public int getHeight() {
        return maxZ - minZ + 1;
    }

    @Override
    public String toString() {
        return getWidth() + " x " + getHeight() + "(minX: " + minX + ", minZ: " + minZ + ", maxX: " + maxX + ", maxZ: " + maxZ + ")";
    }
}