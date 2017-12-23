package pl.sebcel.mclivemap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Inflater;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

public class NBTUtils {

    public HeightMap loadTerrainData(String worldDirectory, int[][] regionIds) throws Exception {
        System.out.println("Loading terrain data from " + worldDirectory);
        HeightMap heightMap = new HeightMap();
        for (int regionIdx = 0; regionIdx < regionIds.length; regionIdx++) {
            int regionX = regionIds[regionIdx][0];
            int regionZ = regionIds[regionIdx][1];
            loadHeightData(worldDirectory, heightMap, regionX, regionZ);
        }

        return heightMap;
    }

    private void loadHeightData(String worldDirectory, HeightMap heightMap, int regionIdX, int regionIdZ) throws Exception {
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

                byte[] decompressedChunkData = decompress(regionData, chunkOffset + 5, chunkLengthInBytes);

                Tag<?> t = deserializeTags(decompressedChunkData);

                CompoundMap child = (CompoundMap) t.getValue();
                CompoundMap levelTag = (CompoundMap) child.get("Level").getValue();
                int zPos = (int) levelTag.get("zPos").getValue();
                int xPos = (int) levelTag.get("xPos").getValue();
                int[] heightMapData = (int[]) levelTag.get("HeightMap").getValue();

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        int v = heightMapData[x + 16 * y];
                        heightMap.setHeight(xPos * 16 + x, zPos * 16 + y, v);
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

    private byte[] decompress(byte[] input, int offset, int length) throws Exception {
        Inflater decompressor = new Inflater();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        decompressor.setInput(input, offset, length);
        int readBytes = -1;
        while (readBytes != 0) {
            readBytes = decompressor.inflate(buffer);
            out.write(buffer, 0, readBytes);
        }
        out.close();

        return out.toByteArray();
    }

    private Tag<?> deserializeTags(byte[] chunkData) throws Exception {
        NBTInputStream nis = new NBTInputStream(new ByteArrayInputStream(chunkData), false, ByteOrder.BIG_ENDIAN);
        Tag<?> t = nis.readTag();
        nis.close();
        return t;
    }
}
