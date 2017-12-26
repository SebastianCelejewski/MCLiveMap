package pl.sebcel.mclivemap.loaders;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

import pl.sebcel.mclivemap.domain.Chunk;
import pl.sebcel.mclivemap.domain.Region;
import pl.sebcel.mclivemap.domain.RegionCoordinates;

public class RegionLoader {

    private Decompressor decompressor = new Decompressor();
    
    public List<Region> loadRegions(String worldDirectory, List<RegionCoordinates> regionsCoordinates) {
        List<Region> result = new ArrayList<>();
        for (RegionCoordinates regionCoordinates : regionsCoordinates) {
            result.add(loadRegion(worldDirectory, regionCoordinates));
        }
        return result;
    }

    public Region loadRegion(String worldDirectory, RegionCoordinates regionCoordinates) {
        String fileName = "r." + regionCoordinates.getRegionX() + "." + regionCoordinates.getRegionZ() + ".mca";
        String fullPath = worldDirectory + File.separator + "world" + File.separator + "region" + File.separator + fileName;
        System.out.println(" - Loading region from " + fullPath);

        byte[] regionData = loadFile(fullPath);

        List<Chunk> chunks = new ArrayList<>();

        for (int i = 0; i < 1024; i++) {
            try {
                int chunkOffset = getChunkOffset(regionData, i);
                if (chunkOffset == 0) {
                    continue;
                }
                int chunkLengthInBytes = getChunkLength(regionData, chunkOffset);

                byte[] decompressedChunkData = decompressor.decompress(regionData, chunkOffset + 5, chunkLengthInBytes);
                Tag<?> t = deserializeTags(decompressedChunkData);

                CompoundMap child = (CompoundMap) t.getValue();
                CompoundMap levelTag = (CompoundMap) child.get("Level").getValue();
                int chunkX = ((Tag<Integer>) levelTag.get("xPos")).getValue();
                int chunkZ = ((Tag<Integer>) levelTag.get("zPos")).getValue();
                int[] heightMap = (int[]) levelTag.get("HeightMap").getValue();

                Chunk chunk = new Chunk(chunkX, chunkZ);
                chunk.setHeightMap(heightMap);

                byte[] blocks = new byte[16*16*256];

                ListTag sectionsTag = (ListTag) levelTag.get("Sections");
                List<CompoundTag> sectionsTags = sectionsTag.getValue();
                for (CompoundTag section : sectionsTags) {
                    byte yPos = ((Tag<Byte>) section.getValue().get("Y")).getValue();
                    byte[] sectionBlockIds = (byte[]) section.getValue().get("Blocks").getValue();
                    for (int idx = 0; idx < sectionBlockIds.length; idx++) {
                        blocks[idx + yPos * 4096] = sectionBlockIds[idx];
                    }
                }

                chunk.setBlocks(blocks);
                chunks.add(chunk);
            } catch (Exception ex) {
                System.err.println("Failed to load height data: " + ex.getMessage() + "\nFile: " + fullPath + "\nidx: " + i);
            }
        }

        Region region = new Region(regionCoordinates, chunks);
        return region;
    }

    private byte[] loadFile(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load file " + filePath + ": " + ex.getMessage(), ex);
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