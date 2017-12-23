package pl.sebcel.mclivemap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;

public class LocationUtils {

    public List<PlayerData> loadPlayersLocations(String fileLocationsDirectoryPath) {
        String[] locationFiles = findLocationFiles(fileLocationsDirectoryPath);

        List<PlayerData> playersData = new ArrayList<>();

        for (String locationFile : locationFiles) {
            playersData.add(loadPlayerLocations(locationFile));
        }

        return playersData;
    }

    private String[] findLocationFiles(String fileLocationsDirectoryPath) {
        System.out.println("Looking for player location files in " + fileLocationsDirectoryPath);
        File locationFilesDirectory = new File(fileLocationsDirectoryPath);

        if (!locationFilesDirectory.exists()) {
            throw new RuntimeException("Directory " + fileLocationsDirectoryPath + " does not exist");
        }

        if (!locationFilesDirectory.isDirectory()) {
            throw new RuntimeException(fileLocationsDirectoryPath + " is not a directory");
        }

        String[] locationFiles = locationFilesDirectory.list((dir, name) -> name.startsWith("location-") && name.endsWith(".csv"));
        System.out.println("Found " + locationFiles.length + " file location files");

        return locationFiles;
    }

    private PlayerData loadPlayerLocations(String locationFilePath) {
        System.out.println(" - Loading player locations from " + locationFilePath);
        PlayerData playerData = new PlayerData();

        try {
            String playerName = locationFilePath.substring(locationFilePath.indexOf("-") + 1, locationFilePath.lastIndexOf("."));
            playerData.setName(playerName);

            List<String> locations = Files.readAllLines(Paths.get(locationFilePath));
            for (String location : locations) {
                String[] tokens = location.split(",");
                int dimension = Integer.parseInt(tokens[1]);
                int x = Integer.parseInt(tokens[2]);
                int y = Integer.parseInt(tokens[3]);
                int z = Integer.parseInt(tokens[4]);
                PlayerLocation playerLocation = new PlayerLocation(dimension, x, y, z);
                playerData.addLocation(playerLocation);
            }

            return playerData;
            
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load player location data from " + locationFilePath + ": " + ex.getMessage(), ex);
        }

    }
}