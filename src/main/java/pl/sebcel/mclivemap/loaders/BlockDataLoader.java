package pl.sebcel.mclivemap.loaders;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.sebcel.mclivemap.BlockData;
import pl.sebcel.mclivemap.domain.Block;

public class BlockDataLoader {

    public BlockData loadBlockData(String blockDataFilePath) {
        System.out.println("Loading Minecraft block data from " + blockDataFilePath);
        Map<Integer, Block> data = new HashMap<>();

        try {
            String blockDefinitions = new String(Files.readAllBytes(Paths.get(blockDataFilePath)));
            JSONObject root = new JSONObject(blockDefinitions);
            JSONArray dataArray = root.getJSONArray("data");
            dataArray.forEach(x -> saveData(data, x));

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load block data from " + blockDataFilePath + ": " + ex.getMessage(), ex);
        }

        return new BlockData(data);
    }

    private void saveData(Map<Integer, Block> data, Object x) {
        JSONObject o = (JSONObject) x;

        int id = o.getInt("id");
        boolean isTransparent = o.optBoolean("transparent");
        String colorCode = o.optString("color");
        String name = o.getString("name");
        Color color = getColor(colorCode);

        Block block = new Block(id, name, isTransparent, color);
        data.put(id, block);
    }

    private Color getColor(String colorCode) {
        if (colorCode == null) {
            return null;
        }

        if (colorCode.trim().length() == 0) {
            return null;
        }

        String redStr = colorCode.substring(0, 2);
        String greenStr = colorCode.substring(2, 4);
        String blueStr = colorCode.substring(4, 6);

        int red = Integer.parseInt(redStr, 16);
        int green = Integer.parseInt(greenStr, 16);
        int blue = Integer.parseInt(blueStr, 16);

        return new Color(red, green, blue);
    }
}