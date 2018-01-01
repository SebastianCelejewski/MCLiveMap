package pl.sebcel.mclivemap.utils;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    public static void saveFile(String filePath, byte[] fileContent) {
        try {
            Files.write(Paths.get(filePath), fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save file " + filePath + ": " + ex.getMessage(), ex);
        }
    }

    public static void saveFile(String filePath, String contents) {
        try {
            FileWriter fw = new FileWriter(filePath);
            fw.write(contents);
            fw.close();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save file " + filePath + ": " + ex.getMessage(), ex);
        }
    }
}
