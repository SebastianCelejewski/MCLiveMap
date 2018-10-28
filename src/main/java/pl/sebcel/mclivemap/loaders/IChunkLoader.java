package pl.sebcel.mclivemap.loaders;

import com.flowpowered.nbt.CompoundMap;

public interface IChunkLoader {
    
    public int[] getHeightMap(CompoundMap levelTag);
    
    public int[] getBlockIds(CompoundMap levelTag);

}