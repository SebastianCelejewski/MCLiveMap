package pl.sebcel.mclivemap;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.sebcel.mclivemap.utils.ColorUtils;

public class ColourTable {

    private Map<String, Color> data = new HashMap<>();

    public void loadColourTable(String colourTableFilePath) {
        try {
            data = new HashMap<>();
            List<String> colourTableEntries = Files.readAllLines(Paths.get(colourTableFilePath));
            for (String colourTableEntry : colourTableEntries) {
                String[] tokens = colourTableEntry.split(",");
                String blockId = tokens[0];
                String colourCode = tokens[1];
                Color color = ColorUtils.getColor(colourCode);
                data.put(blockId, color);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load colour table from file " + colourTableFilePath + ": " + ex.getMessage(), ex);
        }
    }

    public Color getColor(String id) {
        return data.get(id);
    }

}