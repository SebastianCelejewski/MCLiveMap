package pl.sebcel.mclivemap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;
import pl.sebcel.mclivemap.domain.Region;
import pl.sebcel.mclivemap.domain.RegionCoordinates;
import pl.sebcel.mclivemap.domain.WorldMap;
import pl.sebcel.mclivemap.loaders.BlockDataLoader;
import pl.sebcel.mclivemap.loaders.PlayerLoader;
import pl.sebcel.mclivemap.loaders.RegionLoader;
import pl.sebcel.mclivemap.render.PlayerRenderer;
import pl.sebcel.mclivemap.render.TerrainRenderer;
import pl.sebcel.mclivemap.render.TerrainRenderer.Mode;

public class Program {

    private RegionLoader regionLoader = new RegionLoader();
    private BlockDataLoader blockDataLoader = new BlockDataLoader();
    private PlayerLoader playerLoader = new PlayerLoader();
    private TerrainRenderer terrainRenderer = new TerrainRenderer();
    private PlayerRenderer playerRenderer = new PlayerRenderer();

    public static void main(String[] args) {
        new Program().run(args);
    }

    public void run(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar mclivemap.jar <path_to_world_directory> <path_to_locations_directory> [height|block]");
            System.exit(255);
        }

        String worldDirectory = args[0];
        String locationsDirectory = args[1];
        Mode mode = Mode.fromString(args[2]);

        BlockData blockData = blockDataLoader.loadBlockData("vanilla_ids.json");
        List<PlayerData> playersData = playerLoader.loadPlayersData(locationsDirectory);
        
        long startTime = new Date().getTime();

        for (PlayerData playerData : playersData) {
            PlayerLocation playerLocation = playerData.getLastLocation();
            RegionCoordinates regionCoordinates = RegionCoordinates.fromPlayerLocation(playerLocation);
            List<RegionCoordinates> regionsCoordinatesForMap = new ArrayList<>();
            regionsCoordinatesForMap.add(regionCoordinates);
            regionsCoordinatesForMap.add(regionCoordinates.left());
            regionsCoordinatesForMap.add(regionCoordinates.left().up());
            regionsCoordinatesForMap.add(regionCoordinates.left().down());
            regionsCoordinatesForMap.add(regionCoordinates.right());
            regionsCoordinatesForMap.add(regionCoordinates.right().up());
            regionsCoordinatesForMap.add(regionCoordinates.right().down());
            regionsCoordinatesForMap.add(regionCoordinates.up());
            regionsCoordinatesForMap.add(regionCoordinates.down());

            int minX = regionCoordinates.getMinX() - 256;
            int maxX = regionCoordinates.getMaxX() + 256;
            int minZ = regionCoordinates.getMinZ() - 256;
            int maxZ = regionCoordinates.getMaxZ() + 256;

            WorldMap worldMap = new WorldMap(minX, maxX, minZ, maxZ);

            List<Region> regions = regionLoader.loadRegions(worldDirectory, regionsCoordinatesForMap);

            for (Region region : regions) {
                terrainRenderer.renderTerrain(worldMap, region, mode, blockData);
            }
            playerRenderer.renderPlayers(worldMap, playersData);

            byte[] mapImage = worldMap.getImage();
            String fileName = "map-" + playerData.getName() + ".png";
            saveFile(fileName, mapImage);
        }

        long endTime = new Date().getTime();
        long duration = endTime - startTime;
        
        System.out.println("Done. Duration: " + duration/1000 + " seconds.");
    }

    private void saveFile(String filePath, byte[] fileContent) {
        try {
            Files.write(Paths.get(filePath), fileContent, StandardOpenOption.CREATE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save file " + filePath + ": " + ex.getMessage(), ex);
        }
    }
}