package pl.sebcel.mclivemap.loaders;

import java.util.List;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.LongArrayTag;
import com.flowpowered.nbt.Tag;

public class ChunkLoader_1_13 implements IChunkLoader {

    private NBTLongArrayDecompressor nbtLongArrayDecompressor = new NBTLongArrayDecompressor();
    private BlockIdsCache blockIdsCache;
    
    public void setBlockIdsCache(BlockIdsCache blockIdsCache) {
        this.blockIdsCache = blockIdsCache;
    }
    
    private int[] renderEmptyHeightMap() {
        int[] result = new int[256];
        for (int i = 0; i < 256; i++) {
            result[i] = 255;
        }
        return result;
    }

    @Override
    public int[] getHeightMap(CompoundMap levelTag, int dataVersion) {
        CompoundTag heightmapsTag = (CompoundTag) levelTag.get("Heightmaps");
        LongArrayTag surfaceHeightMapTag = (LongArrayTag) (heightmapsTag.getValue().get("WORLD_SURFACE"));
        if (surfaceHeightMapTag == null) {
            surfaceHeightMapTag = (LongArrayTag) (heightmapsTag.getValue().get("WORLD_SURFACE_WG"));
            if (surfaceHeightMapTag == null) {
                return renderEmptyHeightMap();
            } 
        }
        long[] compressedHeightMapData = surfaceHeightMapTag.getValue();
        int[] heightMapData = nbtLongArrayDecompressor.decompress(compressedHeightMapData, 256, dataVersion);
        return heightMapData;
    }

    @Override
    public int[] getBlockIds(CompoundMap levelTag, int dataVersion) {
        int[] blockIds = new int[16 * 16 * 256];
        ListTag sectionsTag = (ListTag) levelTag.get("Sections");
        List<CompoundTag> sectionsTags = sectionsTag.getValue();
        for (CompoundTag section : sectionsTags) {
            ListTag paletteTag = (ListTag) section.getValue().get("Palette");
            byte yPos = ((Tag<Byte>) section.getValue().get("Y")).getValue();
            if (section.getValue().get("BlockStates") == null) {
                continue;
            }
            long[] compressedSectionEncodedBlockIds = (long[]) section.getValue().get("BlockStates").getValue();
            int[] sectionBlockIdxs = nbtLongArrayDecompressor.decompress(compressedSectionEncodedBlockIds, 256 * 16, dataVersion);
            for (int i = 0; i < sectionBlockIdxs.length; i++) {
                int sectionBlockIdx = sectionBlockIdxs[i];
                String stringBlockId = null;
                if (sectionBlockIdx >= paletteTag.getValue().size()) {
                    stringBlockId = "minecraft:air";
//                    System.err.println("Palette has " + paletteTag.getValue().size() + " elements, but index was " + sectionBlockIdx + " (data version: " + dataVersion + ")");
                } else {
                	stringBlockId = ((CompoundTag) paletteTag.getValue().get(sectionBlockIdxs[i])).getValue().get("Name").getValue().toString();
                }
                
                int intBlockId = blockIdsCache.getNumericBlockId(stringBlockId);
                blockIds[i + yPos * 4096] = intBlockId;
            }
        }
        return blockIds;
    }
}