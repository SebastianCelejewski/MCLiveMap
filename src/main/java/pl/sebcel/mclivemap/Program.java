package pl.sebcel.mclivemap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;
import pl.sebcel.mclivemap.domain.Region;
import pl.sebcel.mclivemap.domain.RegionCoordinates;
import pl.sebcel.mclivemap.domain.WorldMap;
import pl.sebcel.mclivemap.loaders.BlockDataLoader;
import pl.sebcel.mclivemap.loaders.PlayerLoader;
import pl.sebcel.mclivemap.loaders.RegionLoader;
import pl.sebcel.mclivemap.render.PlayerRenderer;
import pl.sebcel.mclivemap.render.SiteRenderer;
import pl.sebcel.mclivemap.render.TerrainRenderer;

public class Program {

    private RegionLoader regionLoader = new RegionLoader();
    private BlockDataLoader blockDataLoader = new BlockDataLoader();
    private PlayerLoader playerLoader = new PlayerLoader();
    private TerrainRenderer terrainRenderer = new TerrainRenderer();
    private PlayerRenderer playerRenderer = new PlayerRenderer();
    private SiteRenderer siteRenderer = new SiteRenderer();

    public static void main(String[] args) {
        new Program().run(args);
    }

    public void run(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar mclivemap.jar <path_to_world_directory> <path_to_locations_directory> <output_directory>");
            System.exit(255);
        }

        String worldDirectory = args[0];
        String locationsDirectory = args[1];
        String outputDirectory = args[2];

        BlockData blockData = blockDataLoader.loadBlockData("vanilla_ids.json");
        List<PlayerData> playersData = playerLoader.loadPlayersData(locationsDirectory);

        if (!new File(outputDirectory).exists()) {
            System.err.println("Output directory " + outputDirectory + " does not exist");
            System.exit(255);
        }
        if (!new File(outputDirectory).isDirectory()) {
            System.out.println(outputDirectory + " is not a directory");
            System.exit(255);
        }

        long startTime = new Date().getTime();

        Set<RegionCoordinates> allRegionsToBeLoaded = new HashSet<>();
        for (PlayerData playerData : playersData) {
            PlayerLocation playerLocation = playerData.getLastLocation();
            RegionCoordinates regionCoordinates = RegionCoordinates.fromPlayerLocation(playerLocation);
            allRegionsToBeLoaded.add(regionCoordinates);
            allRegionsToBeLoaded.add(regionCoordinates.left());
            allRegionsToBeLoaded.add(regionCoordinates.left().up());
            allRegionsToBeLoaded.add(regionCoordinates.left().down());
            allRegionsToBeLoaded.add(regionCoordinates.right());
            allRegionsToBeLoaded.add(regionCoordinates.right().up());
            allRegionsToBeLoaded.add(regionCoordinates.right().down());
            allRegionsToBeLoaded.add(regionCoordinates.up());
            allRegionsToBeLoaded.add(regionCoordinates.down());
        }

        Map<RegionCoordinates, BufferedImage> regionImages = new HashMap<>();

        System.out.println("Loading terrain");
        for (RegionCoordinates regionCoordinates : allRegionsToBeLoaded) {
            Region region = regionLoader.loadRegion(worldDirectory, regionCoordinates);
            BufferedImage regionImage = terrainRenderer.renderTerrain(region, blockData);
            regionImages.put(regionCoordinates, regionImage);
        }

        System.out.println("Creating maps");
        for (PlayerData playerData : playersData) {
            System.out.println(" - Creating map for " + playerData.getName());
            PlayerLocation playerLocation = playerData.getLastLocation();
            RegionCoordinates regionCoordinates = RegionCoordinates.fromPlayerLocation(playerLocation);

            int minX = regionCoordinates.getMinX() - 512;
            int maxX = regionCoordinates.getMaxX() + 512;
            int minZ = regionCoordinates.getMinZ() - 512;
            int maxZ = regionCoordinates.getMaxZ() + 512;

            WorldMap worldMap = new WorldMap(minX, maxX, minZ, maxZ);

            List<RegionCoordinates> regionsToBeDrawn = new ArrayList<>();
            regionsToBeDrawn.add(regionCoordinates);
            regionsToBeDrawn.add(regionCoordinates.left());
            regionsToBeDrawn.add(regionCoordinates.left().up());
            regionsToBeDrawn.add(regionCoordinates.left().down());
            regionsToBeDrawn.add(regionCoordinates.right());
            regionsToBeDrawn.add(regionCoordinates.right().up());
            regionsToBeDrawn.add(regionCoordinates.right().down());
            regionsToBeDrawn.add(regionCoordinates.up());
            regionsToBeDrawn.add(regionCoordinates.down());

            System.out.println("   - Rendering terrain");
            for (RegionCoordinates regCoord : regionsToBeDrawn) {
                BufferedImage regionImage = regionImages.get(regCoord);
                worldMap.setImage(regionImage, regCoord.getMinX(), regCoord.getMinZ());
            }
            System.out.println("   - Rendering players");
            playerRenderer.renderPlayers(worldMap, playersData);

            byte[] mapImage = worldMap.getImage();
            String fileName = outputDirectory + File.separator + "map-" + playerData.getName() + ".png";
            saveFile(fileName, mapImage);
        }

        System.out.println("Rendering site");
        String site = siteRenderer.renderSite(playersData, "template.html");
        saveFile(outputDirectory + File.separator + "index.html", site);

        long endTime = new Date().getTime();
        long duration = endTime - startTime;

        System.out.println("Done. Duration: " + duration / 1000 + " seconds.");
    }

    private void saveFile(String filePath, byte[] fileContent) {
        try {
            Files.write(Paths.get(filePath), fileContent, StandardOpenOption.CREATE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save file " + filePath + ": " + ex.getMessage(), ex);
        }
    }

    private void saveFile(String filePath, String contents) {
        try {
            saveFile(filePath, contents.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save file " + filePath + ": " + ex.getMessage(), ex);
        }
    }
}