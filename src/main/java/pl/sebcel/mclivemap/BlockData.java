package pl.sebcel.mclivemap;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import pl.sebcel.mclivemap.domain.Block;

public class BlockData {

    private Map<Integer, Block> data = new HashMap<>();

    public BlockData(Map<Integer, Block> data) {
        this.data = data;
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