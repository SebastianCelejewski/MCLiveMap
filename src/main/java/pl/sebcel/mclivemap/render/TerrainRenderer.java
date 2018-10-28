package pl.sebcel.mclivemap.render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import pl.sebcel.mclivemap.BlockData;
import pl.sebcel.mclivemap.ColourTable;
import pl.sebcel.mclivemap.domain.Chunk;
import pl.sebcel.mclivemap.domain.Region;
import pl.sebcel.mclivemap.loaders.BlockIdsCache;

public class TerrainRenderer {

    private BlockData blockData;
    private ColourTable colourTable;
    private BlockIdsCache blockIdsCache;

    public void setBlockData(BlockData blockData) {
        this.blockData = blockData;
    }

    public void setColourTable(ColourTable colourTable) {
        this.colourTable = colourTable;
    }

    public void setBlockIdsCache(BlockIdsCache blockIdsCache) {
        this.blockIdsCache = blockIdsCache;
    }

    public BufferedImage renderTerrain(Region region) {
        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);

        if (region == null) {
            System.out.println("Returning empty image for null region");
            return image;
        }

        System.out.println(" - Rendering terrain for region " + region.getCoordinates());

        int minX = region.getCoordinates().getBounds().getMinX();
        int minZ = region.getCoordinates().getBounds().getMinZ();

        int[][] regionHeightMap = getHeighMapForTheWholeRegion(region);

        for (Chunk chunk : region.getChunks()) {
            int chunkX = chunk.getChunkX();
            int chunkZ = chunk.getChunkZ();

            int[] heightMap = chunk.getHeightMap();
            int[] blockIds = chunk.getBlockIds();
            boolean is1_13 = chunk.isId1_13();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int baseHeight = heightMap[x + 16 * z];
                    int height = baseHeight;
                    boolean foundNonTransparent = false;
                    while (!foundNonTransparent && height >= 0) {
                        int blockId = blockIds[x + 16 * z + 16 * 16 * height];
                        Color color = null;
                        if (is1_13) {
                            String stringBlockId = blockIdsCache.getStringBlockId(blockId);
                            color = colourTable.getColor(stringBlockId);
                        } else {
                            color = blockData.getColor(blockId);
                        }

                        if (color != null) {
                            int imageX = chunkX * 16 + x - minX;
                            int imageY = chunkZ * 16 + z - minZ;
                            color = modifyColorBasedOnRegionHeightMap(color, regionHeightMap, heightMap, x, z, imageX, imageY, baseHeight);
                            image.setRGB(imageX, imageY, color.getRGB());
                            foundNonTransparent = true;
                        } else {
                            height--;
                        }
                    }
                }
            }
        }

        return image;
    }

    private int[][] getHeighMapForTheWholeRegion(Region region) {
        int[][] regionHeightMap = new int[512][512];
        int minX = region.getCoordinates().getBounds().getMinX();
        int minZ = region.getCoordinates().getBounds().getMinZ();

        for (Chunk chunk : region.getChunks()) {
            int chunkX = chunk.getChunkX();
            int chunkZ = chunk.getChunkZ();

            int[] heightMap = chunk.getHeightMap();
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int height = heightMap[x + 16 * z];
                    int imageX = chunkX * 16 + x - minX;
                    int imageY = chunkZ * 16 + z - minZ;
                    regionHeightMap[imageX][imageY] = height;
                }
            }
        }
        return regionHeightMap;
    }

    private Color modifyColorBasedOnRegionHeightMap(Color color, int[][] regionHeightMap, int[] heightMap, int x, int z, int imageX, int imageY, int baseHeight) {
        if (imageX > 1 && regionHeightMap[imageX - 1][imageY] < heightMap[x + 16 * z]) {
            color = brighter(color, 0.95);
        }
        if (imageX > 1 && regionHeightMap[imageX - 1][imageY] > heightMap[x + 16 * z]) {
            color = darker(color, 0.95);
        }
        if (imageY > 1 && regionHeightMap[imageX][imageY - 1] < heightMap[x + 16 * z]) {
            color = brighter(color, 0.9);
        }
        if (imageY > 1 && regionHeightMap[imageX][imageY - 1] > heightMap[x + 16 * z]) {
            color = darker(color, 0.9);
        }
        return color;
    }

    public Color brighter(Color c, double factor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int alpha = c.getAlpha();

        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }

        int newRed = Math.min((int) (r / factor), 255);
        int newGreen = Math.min((int) (g / factor), 255);
        int newBlue = Math.min((int) (b / factor), 255);

        return new Color(newRed, newGreen, newBlue, alpha);
    }

    public Color darker(Color c, double factor) {
        int newRed = Math.max((int) (c.getRed() * factor), 0);
        int newGreen = Math.max((int) (c.getGreen() * factor), 0);
        int newBlue = Math.max((int) (c.getBlue() * factor), 0);
        return new Color(newRed, newGreen, newBlue, c.getAlpha());
    }
}