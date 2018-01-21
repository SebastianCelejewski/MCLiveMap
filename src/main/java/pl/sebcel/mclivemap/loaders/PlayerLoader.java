package pl.sebcel.mclivemap.loaders;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.sebcel.mclivemap.domain.PlayerData;
import pl.sebcel.mclivemap.domain.PlayerLocation;

public class PlayerLoader {

    private DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM");

    public List<PlayerData> loadPlayersData(String locationFilesDirectoryPath) {
        String datePattern = fileNameDateFormat.format(new Date());

        String[] locationFiles = findLocationFiles(locationFilesDirectoryPath, datePattern);

        List<PlayerData> playersData = new ArrayList<>();

        for (String locationFile : locationFiles) {
            playersData.add(loadPlayerLocations(locationFilesDirectoryPath + File.separator + locationFile, datePattern));
        }

        return playersData;
    }

    private String[] findLocationFiles(String locationFilesDirectoryPath, String datePattern) {
        System.out.println("Looking for player location files in " + locationFilesDirectoryPath);
        File locationFilesDirectory = new File(locationFilesDirectoryPath);

        if (!locationFilesDirectory.exists()) {
            throw new RuntimeException("Directory " + locationFilesDirectoryPath + " does not exist");
        }

        if (!locationFilesDirectory.isDirectory()) {
            throw new RuntimeException(locationFilesDirectoryPath + " is not a directory");
        }

        String[] locationFiles = locationFilesDirectory.list((dir, name) -> LocationFileUtils.matchLocationFile(name, datePattern));
        System.out.println("Found " + locationFiles.length + " file location files for date pattern '" + datePattern + "'");

        return locationFiles;
    }

    private PlayerData loadPlayerLocations(String locationFilePath, String datePattern) {
        System.out.println(" - Loading player locations from " + locationFilePath);
        PlayerData playerData = new PlayerData();

        try {
            String playerName = LocationFileUtils.getPlayerName(locationFilePath, datePattern);
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