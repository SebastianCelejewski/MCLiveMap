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

    @Override
    public int[] getHeightMap(CompoundMap levelTag) {
        CompoundTag heightmapsTag = (CompoundTag) levelTag.get("Heightmaps");
        LongArrayTag surfaceHeightMapTag = (LongArrayTag) (heightmapsTag.getValue().get("WORLD_SURFACE"));
        long[] compressedHeightMapData = surfaceHeightMapTag.getValue();
        int[] heightMapData = nbtLongArrayDecompressor.decompress(compressedHeightMapData, 256);
        return heightMapData;
    }

    @Override
    public int[] getBlockIds(CompoundMap levelTag) {
        int[] blockIds = new int[16 * 16 * 256];
        ListTag sectionsTag = (ListTag) levelTag.get("Sections");
        List<CompoundTag> sectionsTags = sectionsTag.getValue();
        for (CompoundTag section : sectionsTags) {
            ListTag paletteTag = (ListTag) section.getValue().get("Palette");
            byte yPos = ((Tag<Byte>) section.getValue().get("Y")).getValue();
            long[] compressedSectionEncodedBlockIds = (long[]) section.getValue().get("BlockStates").getValue();
            int[] sectionBlockIdxs = nbtLongArrayDecompressor.decompress(compressedSectionEncodedBlockIds, 256 * 16);
            for (int i = 0; i < sectionBlockIdxs.length; i++) {
                String stringBlockId = ((CompoundTag) paletteTag.getValue().get(sectionBlockIdxs[i])).getValue().get("Name").getValue().toString();
                int intBlockId = blockIdsCache.getNumericBlockId(stringBlockId);
                blockIds[i + yPos * 4096] = intBlockId;
            }
        }
        return blockIds;
    }
}