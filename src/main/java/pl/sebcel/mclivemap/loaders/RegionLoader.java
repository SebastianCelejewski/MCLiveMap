package pl.sebcel.mclivemap.loaders;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

import pl.sebcel.mclivemap.domain.Chunk;
import pl.sebcel.mclivemap.domain.Region;
import pl.sebcel.mclivemap.domain.RegionCoordinates;
import pl.sebcel.mclivemap.utils.FileUtils;

public class RegionLoader {

    private Decompressor decompressor = new Decompressor();
    private IChunkLoader chunkLoader_1_12 = new ChunkLoader_1_12();
    private IChunkLoader chunkLoader_1_13 = new ChunkLoader_1_13();

    /**
     * May return null if region does not yet exist
     */
    public Region loadRegion(String worldDirectory, RegionCoordinates regionCoordinates) {
        String fileName = "r." + regionCoordinates.getRegionX() + "." + regionCoordinates.getRegionZ() + ".mca";
        String fullPath = worldDirectory + File.separator + "world" + File.separator + "region" + File.separator + fileName;
        System.out.println(" - Loading region from " + fullPath);

        byte[] regionData;
        try {
            regionData = FileUtils.loadFile(fullPath);
        } catch (Exception ex) {
            System.out.println("Could not load region " + regionCoordinates + ": " + ex.getMessage() + ". Skipping.");
            return null;
        }

        List<Chunk> chunks = new ArrayList<>();
        
        boolean loadedSuccessfully = true;
        Integer chunkX = null;
        Integer chunkZ = null;
        Integer dataVersion = null;

        for (int i = 0; i < 1024; i++) {
            try {
                chunkX = null;
                chunkZ = null;
                dataVersion = null;
                int chunkOffset = getChunkOffset(regionData, i);
                if (chunkOffset == 0) {
                    continue;
                }
                int chunkLengthInBytes = getChunkLength(regionData, chunkOffset);

                byte[] decompressedChunkData = decompressor.decompress(regionData, chunkOffset + 5, chunkLengthInBytes);
                Tag<?> t = deserializeTags(decompressedChunkData);

                CompoundMap child = (CompoundMap) t.getValue();
                CompoundMap levelTag = (CompoundMap) child.get("Level").getValue();
                dataVersion = ((IntTag) child.get("DataVersion")).getValue().intValue();
                boolean hasLegacyStructureData = levelTag.get("hasLegacyStructureData") != null && ((ByteTag) levelTag.get("hasLegacyStructureData")).getValue().byteValue() == 1;
                chunkX = ((Tag<Integer>) levelTag.get("xPos")).getValue();
                chunkZ = ((Tag<Integer>) levelTag.get("zPos")).getValue();

                Chunk chunk = new Chunk(chunkX, chunkZ);
                if (is1_12_chunk(dataVersion)) {
                    int[] heightMapData = chunkLoader_1_12.getHeightMap(levelTag);
                    int[] blockData = chunkLoader_1_12.getNumbericBlockIds(levelTag);
                    chunk.setHeightMap(heightMapData);
                    chunk.setNumericBlockIds(blockData);
                    chunk.setBlockIdsAreStrings(false);
                } else if (is1_13_chunk(dataVersion) && hasLegacyStructureData) {
                    int[] heightMapData = chunkLoader_1_12.getHeightMap(levelTag);
                    String[] blockData = chunkLoader_1_13.getStringBlockIds(levelTag);
                    chunk.setHeightMap(heightMapData);
                    chunk.setStringBlockIds(blockData);
                    chunk.setBlockIdsAreStrings(true);
                } else if (is1_13_chunk(dataVersion) && !hasLegacyStructureData) {
                    int[] heightMapData = chunkLoader_1_13.getHeightMap(levelTag);
                    String[] blockData = chunkLoader_1_13.getStringBlockIds(levelTag);
                    chunk.setHeightMap(heightMapData);
                    chunk.setStringBlockIds(blockData);
                    chunk.setBlockIdsAreStrings(true);
                } else {
                    throw new RuntimeException("Unknown type of a tag, dataVersion: " + dataVersion);
                }

                chunks.add(chunk);
            } catch (Exception ex) {
                System.err.println("Failed to load height data: " + ex.getMessage() + "\nFile: " + fullPath + ", idx: " + i + ", chunkX: " + chunkX + ", chunkZ: " + chunkZ + ", dataVersion: " + dataVersion);
                ex.printStackTrace();
                loadedSuccessfully = false;
            }
        }

        Region region = new Region(regionCoordinates, chunks, loadedSuccessfully);
        return region;
    }

    private boolean is1_12_chunk(int dataVersion) {
        return dataVersion < 1631;
    }

    private boolean is1_13_chunk(int dataVersion) {
        return dataVersion >= 1631;
    }

    private int getChunkOffset(byte[] regionData, int idx) {
        int i = idx * 4;
        return 4096 * (Byte.toUnsignedInt(regionData[i + 0]) * 256 * 256 + Byte.toUnsignedInt(regionData[i + 1]) * 256 + Byte.toUnsignedInt(regionData[i + 2]));
    }

    private int getChunkLength(byte[] regionData, int chunkOffset) {
        return Byte.toUnsignedInt(regionData[chunkOffset]) * 256 * 256 * 256 + Byte.toUnsignedInt(regionData[chunkOffset + 1]) * 256 * 256 + Byte.toUnsignedInt(regionData[chunkOffset + 2]) * 256 + Byte.toUnsignedInt(regionData[chunkOffset + 3]);
    }

    private Tag<?> deserializeTags(byte[] chunkData) throws Exception {
        try {
            NBTInputStream nis = new NBTInputStream(new ByteArrayInputStream(chunkData), false, ByteOrder.BIG_ENDIAN);
            Tag<?> t = nis.readTag();
            nis.close();
            return t;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to deserialize NBT tags from raw chunk data: " + ex.getMessage(), ex);
        }
    }
}