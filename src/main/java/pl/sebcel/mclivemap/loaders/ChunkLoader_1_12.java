package pl.sebcel.mclivemap.loaders;

import java.util.List;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;

public class ChunkLoader_1_12 implements IChunkLoader {

    @Override
    public int[] getHeightMap(CompoundMap levelTag) {
        return (int[]) levelTag.get("HeightMap").getValue();
    }

    @Override
    public int[] getBlockIds(CompoundMap levelTag) {
        int[] blockIds = new int[16 * 16 * 256];
        ListTag sectionsTag = (ListTag) levelTag.get("Sections");
        List<CompoundTag> sectionsTags = sectionsTag.getValue();
        for (CompoundTag section : sectionsTags) {
            byte yPos = ((Tag<Byte>) section.getValue().get("Y")).getValue();
            byte[] sectionBlockIds = (byte[]) section.getValue().get("Blocks").getValue();
            for (int idx = 0; idx < sectionBlockIds.length; idx++) {
                blockIds[idx + yPos * 4096] = sectionBlockIds[idx];
            }
        }
        return blockIds;
    }
}