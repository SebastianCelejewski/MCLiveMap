package pl.sebcel.mclivemap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import pl.sebcel.mclivemap.NBTUtils.Mode;
import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;

public class Program {

    private LocationUtils locationUtils;
    private NBTUtils nbtUtils;
    private MapUtils mapUtils;
    private BlockData blockData;

    public static void main(String[] args) throws Exception {
        new Program().run(args);
    }

    public void run(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: java -jar mclivemap.jar <path_to_world_directory> <path_to_locations_directory> [height|block]");
            System.exit(255);
        }

        String worldDirectory = args[0];
        String locationsDirectory = args[1];
        Mode mode = Mode.fromString(args[2]);
        
        blockData = new BlockData();
        nbtUtils = new NBTUtils(blockData);
        mapUtils = new MapUtils(blockData);
        locationUtils = new LocationUtils();

        blockData.initialize();
        List<PlayerData> playersData = locationUtils.loadPlayersLocations(locationsDirectory);
        HeightMap terrainData = nbtUtils.loadTerrainData(worldDirectory, new int[][] {
            {0,0}, 
            {-1,0},
            {0,-1},
            {-1,-1},
            {0,1},
            {0,2},
            {-1,1},
            {-1,2}
        }, mode);

        byte[] mapImage = mapUtils.renderMap(terrainData, playersData, mode);

        Files.write(Paths.get("output-map.png"), mapImage, StandardOpenOption.CREATE);

        System.out.println("Done.");
    }
    
    private int getRegionX(PlayerLocation playerLocation) {
        int regionX = (int) Math.floor(playerLocation.getX() / 512);
        if (playerLocation.getX() < 0) {
            regionX = regionX - 1;
        }
        return regionX;
    }

    private int getRegionZ(PlayerLocation playerLocation) {
        int regionZ = (int) Math.floor(playerLocation.getZ() / 512);
        if (playerLocation.getZ() < 0) {
            regionZ = regionZ - 1;
        }
        return regionZ;
    }
    
}