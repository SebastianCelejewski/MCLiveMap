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

import javax.imageio.ImageIO;

import pl.sebcel.mclivemap.domain.Bounds;
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

        verifyDirectory(worldDirectory);
        verifyDirectory(locationsDirectory);
        verifyDirectory(outputDirectory);

        BlockData blockData = blockDataLoader.loadBlockData("vanilla_ids.json");
        List<PlayerData> playersData = playerLoader.loadPlayersData(locationsDirectory);

        long startTime = new Date().getTime();

        Set<RegionCoordinates> allRegionsToBeLoaded = calculateAllRegionsToBeLoaded(playersData);

        System.out.println("Loading terrain");
        Map<RegionCoordinates, BufferedImage> regionImages = loadRegionImages(allRegionsToBeLoaded, worldDirectory, blockData);

        System.out.println("Creating maps");
        for (PlayerData playerData : playersData) {
            System.out.println(" - Creating map for " + playerData.getName());
            PlayerLocation playerLocation = playerData.getLastLocation();
            RegionCoordinates regionCoordinates = RegionCoordinates.fromPlayerLocation(playerLocation);

            int minX = regionCoordinates.getBounds().getMinX() - 512;
            int maxX = regionCoordinates.getBounds().getMaxX() + 512;
            int minZ = regionCoordinates.getBounds().getMinZ() - 512;
            int maxZ = regionCoordinates.getBounds().getMaxZ() + 512;
            
            Bounds mapBounds = new Bounds(minX, minZ, maxX, maxZ);
            WorldMap worldMap = new WorldMap(mapBounds);

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
                worldMap.setImageFragment(regionImage, regCoord.getBounds());
            }
            System.out.println("   - Rendering players");
            playerRenderer.renderPlayers(worldMap, playersData);

            byte[] mapImage = worldMap.getImageAsPNG();
            String fileName = outputDirectory + File.separator + "map-" + playerData.getName() + ".png";
            System.out.println("   - Saving rendered map to " + fileName);
            saveFile(fileName, mapImage);
        }

        System.out.println("Rendering site");
        String site = siteRenderer.renderSite(playersData, "template.html");
        saveFile(outputDirectory + File.separator + "index.html", site);

        long endTime = new Date().getTime();
        long duration = endTime - startTime;

        System.out.println("Done. Duration: " + duration / 1000 + " seconds.");
    }

    private void verifyDirectory(String directory) {
        if (!new File(directory).exists()) {
            System.err.println("Directory " + directory + " does not exist");
            System.exit(255);
        }
        if (!new File(directory).isDirectory()) {
            System.out.println(directory + " is not a directory");
            System.exit(255);
        }
    }

    private Set<RegionCoordinates> calculateAllRegionsToBeLoaded(List<PlayerData> playersData) {
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

        return allRegionsToBeLoaded;
    }

    private Map<RegionCoordinates, BufferedImage> loadRegionImages(Set<RegionCoordinates> allRegionsToBeLoaded, String worldDirectory, BlockData blockData) {
        Map<RegionCoordinates, BufferedImage> result = new HashMap<>();

        for (RegionCoordinates regionCoordinates : allRegionsToBeLoaded) {
            String fileName = "region." + regionCoordinates.getRegionX() + "." + regionCoordinates.getRegionZ() + ".png";
            BufferedImage regionImage;

            File imageFile = new File(fileName);
            if (imageCanBeReused(imageFile)) {
                try {
                    System.out.println(" - Loading region " + regionCoordinates + " from saved files");
                    regionImage = ImageIO.read(imageFile);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to load region image from " + imageFile.getAbsolutePath() + ": " + ex.getMessage(), ex);
                }
            } else {
                Region region = regionLoader.loadRegion(worldDirectory, regionCoordinates);
                regionImage = terrainRenderer.renderTerrain(region, blockData);
                try {
                    ImageIO.write(regionImage, "png", imageFile);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to save region image to " + imageFile.getAbsolutePath() + ": " + ex.getMessage());
                }
            }

            result.put(regionCoordinates, regionImage);
        }

        return result;
    }

    private boolean imageCanBeReused(File imageFile) {
        if (!imageFile.exists()) {
            return false;
        }

        long currentTime = new Date().getTime();
        long imageCreationTime = imageFile.lastModified();
        long imageFileAge = currentTime - imageCreationTime;

        return imageFileAge < 15 * 60 * 1000;
    }

    private void saveFile(String filePath, byte[] fileContent) {
        try {
            Files.write(Paths.get(filePath), fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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