package pl.sebcel.mclivemap.render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import pl.sebcel.mclivemap.BlockData;
import pl.sebcel.mclivemap.domain.Chunk;
import pl.sebcel.mclivemap.domain.Region;

public class TerrainRenderer {

    public BufferedImage renderTerrain(Region region, BlockData blockData) {
        System.out.println(" - Rendering terrain for region " + region.getCoordinates());

        int minX = region.getCoordinates().getMinX();
        int minZ = region.getCoordinates().getMinZ();

        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);

        for (Chunk chunk : region.getChunks()) {
            int chunkX = chunk.getChunkX();
            int chunkZ = chunk.getChunkZ();

            int[] heightMap = chunk.getHeightMap();
            byte[] blocks = chunk.getBlocks();
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int height = heightMap[x + 16 * z];

                    boolean foundNonTransparent = false;
                    while (!foundNonTransparent && height >= 0) {
                        int blockId = (int) blocks[x + 16 * z + 16 * 16 * height] & 0xFF;
                        if (!blockData.isTransparent(blockId)) {
                            Color color = blockData.getColor(blockId);
                            int imageX = chunkX * 16 + x - minX;
                            int imageY = chunkZ * 16 + z - minZ;
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
}