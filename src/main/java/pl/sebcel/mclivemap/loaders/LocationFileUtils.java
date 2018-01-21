package pl.sebcel.mclivemap.loaders;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocationFileUtils {

    public static boolean matchLocationFile(String fileName, String datePattern) {
        return fileName.startsWith("location-") && fileName.endsWith(datePattern + ".csv"); 
    }

    public static String getPlayerName(String locationFilePath, String datePattern) {
        Path path = Paths.get(locationFilePath);
        String shortName = path.getFileName().toString();

        int beginIndex = "location-".length();
        int endIndex = shortName.length() - datePattern.length() - ".csv".length() - 1;
        return shortName.substring(beginIndex, endIndex);
    }
}