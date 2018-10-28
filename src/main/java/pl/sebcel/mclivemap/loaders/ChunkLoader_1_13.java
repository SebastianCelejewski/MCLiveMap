package pl.sebcel.mclivemap.loaders;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.LongArrayTag;
import com.flowpowered.nbt.Tag;

public class ChunkLoader_1_13 implements IChunkLoader {

    private NBTLongArrayDecompressor nbtLongArrayDecompressor = new NBTLongArrayDecompressor();

    @Override
    public int[] getHeightMap(CompoundMap levelTag) {
        CompoundTag heightmapsTag = (CompoundTag) levelTag.get("Heightmaps");
        LongArrayTag surfaceHeightMapTag = (LongArrayTag) (heightmapsTag.getValue().get("WORLD_SURFACE"));
        long[] compressedHeightMapData = surfaceHeightMapTag.getValue();
        int[] heightMapData = nbtLongArrayDecompressor.decompress(compressedHeightMapData, 256);
        return heightMapData;
    }

    @Override
    public String[] getStringBlockIds(CompoundMap levelTag) {
        String[] stringBlockIds = new String[16 * 16 * 256];
        ListTag sectionsTag = (ListTag) levelTag.get("Sections");
        List<CompoundTag> sectionsTags = sectionsTag.getValue();
        for (CompoundTag section : sectionsTags) {
            ListTag paletteTag = (ListTag) section.getValue().get("Palette");
            byte yPos = ((Tag<Byte>) section.getValue().get("Y")).getValue();
            long[] compressedSectionEncodedBlockIds = (long[]) section.getValue().get("BlockStates").getValue();
            int[] sectionBlockIdxs = nbtLongArrayDecompressor.decompress(compressedSectionEncodedBlockIds, 256 * 16);
            for (int i = 0; i < sectionBlockIdxs.length; i++) {
                stringBlockIds[i + yPos * 4096] = ((CompoundTag) paletteTag.getValue().get(sectionBlockIdxs[i])).getValue().get("Name").getValue().toString();
            }
        }
        return stringBlockIds;
    }

    @Override
    public int[] getNumbericBlockIds(CompoundMap levelTag) {
        throw new NotImplementedException();
    }
}
