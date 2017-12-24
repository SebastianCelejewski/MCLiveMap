package pl.sebcel.mclivemap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

public class NBTUtils {

    public static enum Mode {
        HEIGHT_MAP("height"), BLOCK_MAP("block");

        String shortName;

        Mode(String shortName) {
            this.shortName = shortName;
        }

        static Mode fromString(String shortName) {
            for (Mode m : Mode.values()) {
                if (m.shortName.equals(shortName)) {
                    return m;
                }
            }
            throw new RuntimeException("Unknown mode: " + shortName);
        }
    }

    private Decompressor decompressor = new Decompressor();
    private BlockData blockData;

    public NBTUtils(BlockData blockData) {
        this.blockData = blockData;
    }

    public HeightMap loadTerrainData(String worldDirectory, int[][] regionIds, Mode mode) throws Exception {
        System.out.println("Loading terrain data from " + worldDirectory);
        long startTime = new Date().getTime();

        HeightMap heightMap = new HeightMap();
        for (int regionIdx = 0; regionIdx < regionIds.length; regionIdx++) {
            int regionX = regionIds[regionIdx][0];
            int regionZ = regionIds[regionIdx][1];
            loadHeightData(worldDirectory, heightMap, regionX, regionZ, mode);
        }

        System.out.println(" - Loaded terrain data for " + heightMap.getMinX() + "<x<" + heightMap.getMaxX() + " and " + heightMap.getMinZ() + "<z<" + heightMap.getMaxZ());

        long endTime = new Date().getTime();
        System.out.println(" - Time: " + (endTime - startTime) + " ms");

        return heightMap;
    }

    private void loadHeightData(String worldDirectory, HeightMap heightMap, int regionIdX, int regionIdZ, Mode mode) throws Exception {
        String fileName = "r." + regionIdX + "." + regionIdZ + ".mca";
        String fullPath = worldDirectory + File.separator + "world" + File.separator + "region" + File.separator + fileName;
        System.out.println(" - Loading region from " + fullPath);
        int chunkOffset = 0;

        byte[] regionData = Files.readAllBytes(Paths.get(fullPath));

        for (int i = 0; i < 1024; i++) {
            try {
                chunkOffset = getChunkOffset(regionData, i);
                if (chunkOffset == 0) {
                    continue;
                }
                int chunkLengthInBytes = getChunkLength(regionData, chunkOffset);

                byte[] decompressedChunkData = decompressor.decompress(regionData, chunkOffset + 5, chunkLengthInBytes);
                Tag<?> t = deserializeTags(decompressedChunkData);

                CompoundMap child = (CompoundMap) t.getValue();
                CompoundMap levelTag = (CompoundMap) child.get("Level").getValue();
                int zPos = ((Tag<Integer>) levelTag.get("zPos")).getValue();
                int xPos = ((Tag<Integer>) levelTag.get("xPos")).getValue();

                if (mode == Mode.HEIGHT_MAP) {
                    int[] heightMapData = (int[]) levelTag.get("HeightMap").getValue();

                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            int v = heightMapData[x + 16 * z];
                            heightMap.setHeight(xPos * 16 + x, zPos * 16 + z, v);
                        }
                    }
                }

                if (mode == Mode.BLOCK_MAP) {
                    ListTag sectionsTag = (ListTag) levelTag.get("Sections");
                    List<CompoundTag> sections = sectionsTag.getValue();
                    for (CompoundTag section : sections) {
                        byte yPos = ((Tag<Byte>) section.getValue().get("Y")).getValue();
                        byte[] blockIds = (byte[]) section.getValue().get("Blocks").getValue();
                        for (int y = 0; y < 16; y++) {
                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {
                                    int v = blockIds[x + 16 * z + 16 * 16 * y];
                                    if (!blockData.isTransparent(v)) {
                                        heightMap.setHeight(xPos * 16 + x, zPos * 16 + z, v);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Failed to load height data: " + ex.getMessage() + "\nFile: " + fullPath + "\nidx: " + i + "\nchunkOffset: " + chunkOffset);
            }
        }
    }

    private int getChunkOffset(byte[] regionData, int idx) {
        int i = idx * 4;
        return 4096 * (Byte.toUnsignedInt(regionData[i + 0]) * 256 * 256 + Byte.toUnsignedInt(regionData[i + 1]) * 256 + Byte.toUnsignedInt(regionData[i + 2]));
    }

    private int getChunkLength(byte[] regionData, int chunkOffset) {
        return Byte.toUnsignedInt(regionData[chunkOffset]) * 256 * 256 * 256 + Byte.toUnsignedInt(regionData[chunkOffset + 1]) * 256 * 256 + Byte.toUnsignedInt(regionData[chunkOffset + 2]) * 256 + Byte.toUnsignedInt(regionData[chunkOffset + 3]);
    }

    private Tag<?> deserializeTags(byte[] chunkData) throws Exception {
        NBTInputStream nis = new NBTInputStream(new ByteArrayInputStream(chunkData), false, ByteOrder.BIG_ENDIAN);
        Tag<?> t = nis.readTag();
        nis.close();
        return t;
    }
}
