package pl.sebcel.mclivemap.loaders;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import pl.sebcel.mclivemap.domain.Region;
import pl.sebcel.mclivemap.domain.RegionCoordinates;
import pl.sebcel.mclivemap.render.TerrainRenderer;

public class RegionImageLoader {

    private RegionLoader regionLoader;
    private TerrainRenderer terrainRenderer;
    private String cacheDirectoryName;
    private int cacheInvalidationTimeInMinutes;
    
    private boolean cacheEnabled = true;

    public void setRegionLoader(RegionLoader regionLoader) {
        this.regionLoader = regionLoader;
    }

    public void setTerrainRenderer(TerrainRenderer terrainRenderer) {
        this.terrainRenderer = terrainRenderer;
    }

    public void setCacheDirectoryName(String cacheDirectoryName) {
        this.cacheDirectoryName = cacheDirectoryName;
    }

    public void setCacheInvalidationTimeInMinutes(int cacheInvalidationTimeInMinutes) {
        this.cacheInvalidationTimeInMinutes = cacheInvalidationTimeInMinutes;
    }

    public Map<RegionCoordinates, BufferedImage> loadRegionImages(Set<RegionCoordinates> allRegionsToBeLoaded, String worldDirectory) {
        Map<RegionCoordinates, BufferedImage> result = new HashMap<>();

        for (RegionCoordinates regionCoordinates : allRegionsToBeLoaded) {
            String fileName = cacheDirectoryName + File.separator + "region." + regionCoordinates.getRegionX() + "." + regionCoordinates.getRegionZ() + ".png";
            BufferedImage regionImage;

            File imageFile = new File(fileName);
            if (cacheEnabled && imageCanBeReused(imageFile)) {
                try {
                    System.out.println(" - Loading region " + regionCoordinates + " from saved files");
                    regionImage = ImageIO.read(imageFile);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to load region image from " + imageFile.getAbsolutePath() + ": " + ex.getMessage(), ex);
                }
            } else {
                Region region = regionLoader.loadRegion(worldDirectory, regionCoordinates);
                if (!region.isLoadedSuccessfully()) {
                    throw new RuntimeException("Region did not load successfully. Cannot render region image.");
                }
                regionImage = terrainRenderer.renderTerrain(region);
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

        return imageFileAge < cacheInvalidationTimeInMinutes * 60 * 1000;
    }
}
