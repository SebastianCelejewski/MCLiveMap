package pl.sebcel.mclivemap.render;

import java.awt.Color;
import java.util.Map;

import pl.sebcel.mclivemap.BlockData;
import pl.sebcel.mclivemap.domain.Chunk;
import pl.sebcel.mclivemap.domain.Region;
import pl.sebcel.mclivemap.domain.WorldMap;

public class TerrainRenderer {

    public static enum Mode {
        HEIGHT_MAP("height"), BLOCK_MAP("block");

        String shortName;

        Mode(String shortName) {
            this.shortName = shortName;
        }

        public static Mode fromString(String shortName) {
            for (Mode m : Mode.values()) {
                if (m.shortName.equals(shortName)) {
                    return m;
                }
            }
            throw new RuntimeException("Unknown mode: " + shortName);
        }
    }

    public void renderTerrain(WorldMap worldMap, Region region, Mode mode, BlockData blockData) {
        System.out.println("Rendering region " + region.getCoordinates());
        for (Chunk chunk : region.getChunks()) {
            int chunkX = chunk.getChunkX();
            int chunkZ = chunk.getChunkZ();

            if (mode == Mode.HEIGHT_MAP) {
                int[] heightMap = chunk.getHeightMap();
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int height = heightMap[x + 16 * z];
                        Color color = getColor(height);
                        worldMap.setPixel(chunkX * 16 + x, chunkZ * 16 + z, color);
                    }
                }
            }

            if (mode == Mode.BLOCK_MAP) {
                Map<Integer, byte[]> sections = chunk.getSections();
                for (Map.Entry<Integer, byte[]> section : sections.entrySet()) {
                    byte[] blockIds = section.getValue();
                    for (int y = 0; y < 16; y++) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                int v = blockIds[x + 16 * z + 16 * 16 * y];
                                if (!blockData.isTransparent(v)) {
                                    Color color = blockData.getColor(v);
                                    worldMap.setPixel(chunkX * 16 + x, chunkZ * 16 + z, color);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private Color getColor(int height) {
        int red = height;
        int green = 255 - height;
        int blue = 0;
        return new Color(red, green, blue);
    }
}