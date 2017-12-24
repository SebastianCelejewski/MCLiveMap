package pl.sebcel.mclivemap;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.sebcel.mclivemap.domain.Block;

public class BlockData {

    private Map<Integer, Block> data = new HashMap<>();

    public void initialize() {
        String blockDataFileName = "vanilla_ids.json";
        System.out.println("Loading Minecraft block data from " + blockDataFileName);
        try {
            String blockDefinitions = new String(Files.readAllBytes(Paths.get(blockDataFileName)));
            JSONObject root = new JSONObject(blockDefinitions);
            JSONArray dataArray = root.getJSONArray("data");
            dataArray.forEach(this::saveData);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load block data from " + blockDataFileName + ": " + ex.getMessage(), ex);
        }
    }

    private void saveData(Object x) {
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

    public boolean isTransparent(int id) {
        if (data.containsKey(id)) {
            return data.get(id).isTransparent();
        } else {
            return false;
        }
    }

    public Color getColor(int id) {
        if (data.containsKey(id)) {
            return data.get(id).getColor();
        } else {
            return Color.WHITE;
        }
    }

}
