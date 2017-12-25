package pl.sebcel.mclivemap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

        for (PlayerData playerData : playersData) {
            PlayerLocation playerLocation = playerData.getLastLocation();
            RegionCoordinates regionCoordinates = RegionCoordinates.fromPlayerLocation(playerLocation);

            int minX = regionCoordinates.getMinX();
            int maxX = regionCoordinates.getMaxX();
            int minZ = regionCoordinates.getMinZ();
            int maxZ = regionCoordinates.getMaxZ();

            WorldMap worldMap = new WorldMap(minX, maxX, minZ, maxZ);

            Region region = regionLoader.loadRegion(worldDirectory, regionCoordinates);

            terrainRenderer.renderTerrain(worldMap, region, mode, blockData);
            playerRenderer.renderPlayers(worldMap, playersData);

            byte[] mapImage = worldMap.getImage();
            String fileName = "map-" + playerData.getName() + ".png";
            saveFile(fileName, mapImage);
        }

        System.out.println("Done.");
    }

    private void saveFile(String filePath, byte[] fileContent) {
        try {
            Files.write(Paths.get(filePath), fileContent, StandardOpenOption.CREATE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save file " + filePath + ": " + ex.getMessage(), ex);
        }
    }
}